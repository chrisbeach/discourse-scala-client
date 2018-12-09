package com.brightercode.discourse

import com.brightercode.discourse.DiscourseForumApiClient.queryStringParams
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
  extends PlayWebServiceClient(config.baseUrl, queryStringParams(config.key, config.username)) {

  val topics = new TopicApi(this)
  val posts = new PostApi(this)
  val categories = new CategoryApi(this)
}

object DiscourseForumApiClient {
  private[discourse] def queryStringParams(apiKey: String, username: String) =
    Map(
      "api_key" -> apiKey,
      "api_username" -> username,
    )

  /**
    * Provide a forum to caller and ensure it is shutdown when the caller finishes execution
    */
  def withForum[T](config: DiscourseEndpointConfig)
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
  * @param baseUrl e.g. https://se23.life
  * @param username to act as
  * @param key see https://[your forum]/admin/api/keys
  */
case class DiscourseEndpointConfig(baseUrl: String, username: String, key: String, timeout: FiniteDuration) {
  require(baseUrl != "https://your.discourse.forum", "Please customise application.conf before running")
}
