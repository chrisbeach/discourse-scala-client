package com.brightercode.discourse.apidecorators

import cats.effect.concurrent.MVar
import cats.effect.{ContextShift, IO, Timer}
import com.brightercode.discourse.api.TopicApi
import com.brightercode.discourse.apidecorators.AsyncTopicPoster.Channel
import com.brightercode.discourse.exceptions.RateLimitException
import com.brightercode.discourse.model.{CreatedPost, TopicTemplate}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
 * @param topicQueueSize maximum number of topics to queue
 */
case class AsyncTopicPosterConfig(topicQueueSize: Int = 1000)

/**
 * Posts topics to Discourse forum asynchronously
 */
class AsyncTopicPoster(api: TopicApi)
                      (implicit config: AsyncTopicPosterConfig = AsyncTopicPosterConfig(),
                       timer: Timer[IO], cs: ContextShift[IO]) extends LazyLogging {

  /**
   * Consumes a topic from the channel, posts it to Discourse and then pauses, depending on the result, before recursing
   */
  def loop(channel: Channel[TopicTemplate]): IO[Unit] = {
    logger.trace(s"Entering postingLoop")
    for {
      topicTemplate <- channel.take
      _ = logger.trace(s"postingLoop consumed: $topicTemplate")
      result <- IO.fromFuture(IO(api.create(topicTemplate)))
        .redeem({
          case e: RateLimitException => PostResult(Failure(e), nextPollIn = e.waitDuration.getOrElse(60 seconds))
          case NonFatal(e) => PostResult(Failure(e), nextPollIn = 5 seconds)
        },
        createdPost => PostResult(Success(createdPost), nextPollIn = 5 seconds)
      )
      _ = result.log()
    } yield Timer[IO].sleep(result.nextPollIn).flatMap { _ =>
      loop(channel)
    }
  }

  case class PostResult(createdPost: Try[CreatedPost], nextPollIn: FiniteDuration) {
    def log(): Unit =
      createdPost match {
        case Success(post) => logger.info(s"Posted topic: $post")
        case Failure(e) => logger.warn(s"Error posting topic: $e", e)
      }
  }
}

object AsyncTopicPoster {
  type Channel[A] = MVar[IO, A]
}