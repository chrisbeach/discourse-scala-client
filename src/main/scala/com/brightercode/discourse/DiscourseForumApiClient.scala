package com.brightercode.discourse

import com.brightercode.discourse.DiscourseForumApiClient.apiHeaders
import com.brightercode.discourse.methods.{CategoryApi, PostApi, TopicApi}
import com.brightercode.discourse.util.PlayWebServiceClient

import scala.concurrent.duration.FiniteDuration

/**
  * Partial implementation of Discourse forum API
  *
  * @see https://docs.discourse.org/
  *
  */
class DiscourseForumApiClient private (config: DiscourseEndpointConfig)
  extends PlayWebServiceClient(config.baseUrl, apiHeaders(config.key, config.username)) {

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
  def withDiscourseForum[T](config: DiscourseEndpointConfig)
                           (operation: DiscourseForumApiClient => T): T = {
    val forum = new DiscourseForumApiClient(config)
    try {
      operation(forum)
    } finally {
      forum.shutdown()
    }
  }
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
