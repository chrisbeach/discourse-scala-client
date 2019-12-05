package com.brightercode.discourse.apidecorators

import java.util
import java.util.concurrent.LinkedBlockingQueue

import com.brightercode.discourse.api.TopicApi
import com.brightercode.discourse.exceptions.RateLimitException
import com.brightercode.discourse.model.TopicTemplate
import com.brightercode.discourse.util.RateLimitUtil
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.control.NonFatal

/**
 * @param topicQueueSize maximum number of topics to queue
 */
case class TopicApiAsyncConfig(topicQueueSize: Int = 1000)

/**
 * Posts topics to Discourse forum asynchronously
 *
 * @param ec execution context on which to run background thread
 */
class TopicApiAsync(api: TopicApi)
                   (implicit config: TopicApiAsyncConfig = TopicApiAsyncConfig(),
                    ec: ExecutionContext) extends LazyLogging {

  private val queue = new LinkedBlockingQueue[TopicTemplate](config.topicQueueSize)

  def enqueue(topic: TopicTemplate): Unit = {
    logger.trace(s"Enqueuing $topic")
    queue.put(topic)
  }

  Future {
    Thread.currentThread().setDaemon(true) // Don't prevent exit
    Thread.currentThread().setName("Async topic APIs")
    processLoop()
  }

  @tailrec
  private def processLoop(): Unit = {
    try {
      val outstandingTopics = new util.ArrayList[TopicTemplate]()
      queue.drainTo(outstandingTopics)
      process(outstandingTopics.asScala.toList)
    } catch {
      case e: RateLimitException => RateLimitUtil.sleepForRateLimit(e)
      case NonFatal(e) => logger.warn(e.getMessage, e)
    }
    processLoop()
  }

  private def process(topics: List[TopicTemplate]): Unit =
    if (topics.nonEmpty) {
      logger.debug(s"${topics.size} topic(s) outstanding. Taking first.")
      topics.headOption.foreach { topic =>
        try {
          val createdTopic = Await.result(api.create(topic), 10 seconds)
          logger.info(createdTopic.toString)
        } catch {
          case e: RateLimitException => throw e
          case NonFatal(e) => logger.error(e.getMessage, e)
        }
      }
      Thread.sleep(5000)
    } else {
      Thread.sleep(1000)
    }
}
