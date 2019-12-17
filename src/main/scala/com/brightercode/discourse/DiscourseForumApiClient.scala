package com.brightercode.discourse

import akka.actor.ActorSystem
import cats.effect.{Bracket, IO, Resource}
import com.brightercode.discourse.DiscourseForumApiClient.apiHeaders
import com.brightercode.discourse.api.{CategoryApi, PostApi, TopicApi}
import com.brightercode.discourse.util.PlayWebServiceClient

import scala.concurrent.duration.FiniteDuration

/**
  * Partial implementation of Discourse forum API
  *
  * @see https://docs.discourse.org/
  *
  */
class DiscourseForumApiClient private (config: DiscourseEndpointConfig, system: ActorSystem)
  extends PlayWebServiceClient(config.baseUrl, system, apiHeaders(config.key, config.username)) {

  val topics = new TopicApi(this)
  val posts = new PostApi(this)
  val categories = new CategoryApi(this)
}

object DiscourseForumApiClient {
  private[discourse] def apiHeaders(apiKey: String, username: String) =
    Seq(
      "Api-Key" -> apiKey,
      "Api-Username" -> username,
      "Accept" -> "application/json",
      "Content-Type" -> "application/json"
    )

  /**
    * Provide a forum to caller and ensure it is shutdown when the caller finishes execution
    */
  def withDiscourseForum[T](config: DiscourseEndpointConfig, system: ActorSystem)
                           (operation: DiscourseForumApiClient => T): T = {
    val forum = new DiscourseForumApiClient(config, system)
    try {
      operation(forum)
    } finally {
      forum.shutdown()
    }
  }

  /**
   * Provide a forum to caller and ensure it is shutdown when the caller finishes execution
   */
  def discourseForumResource(config: DiscourseEndpointConfig,
                             system: ActorSystem): Resource[IO, DiscourseForumApiClient] =
    Resource.make(
      IO(new DiscourseForumApiClient(config, system))
    )(
      forum => IO(forum.shutdown())
    )
}


/**
  * @see [[com.brightercode.discourse.util.TypesafeConfigHelper]]
  *
  * @param baseUrl e.g. https://se23.life
  * @param username to act as
  * @param key see https://[your forum]/admin/api/keys
  */
case class DiscourseEndpointConfig(baseUrl: String,
                                   username: String,
                                   key: String,
                                   timeout: FiniteDuration) {
  require(baseUrl != "https://your.discourse.forum", "Please customise application.conf before running")
}
