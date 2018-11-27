package com.brightercode.discourse

import com.brightercode.discourse.api.{CategoryApi, PostApi, TopicApi}
import com.brightercode.discourse.util.PlayWebServiceClient
import DiscourseForumApiClient.queryStringParams

/**
  * Partial implementation of Discourse forum API
  *
  * @see https://docs.discourse.org/
  *
  * @param urlBase e.g. https://se23.life
  * @param apiKey see https://[your forum]/admin/api/keys
  * @param username to act as
  */
class DiscourseForumApiClient(urlBase: String, apiKey: String, username: String)
  extends PlayWebServiceClient(urlBase, queryStringParams(apiKey, username)) {

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

  def withForum[T](urlBase: String,
                   apiKey: String,
                   username: String)
                  (operation: DiscourseForumApiClient => T): T = {
    val forum = new DiscourseForumApiClient(urlBase, apiKey, username)
    try {
      operation(forum)
    } finally {
      forum.shutdown()
    }
  }
}