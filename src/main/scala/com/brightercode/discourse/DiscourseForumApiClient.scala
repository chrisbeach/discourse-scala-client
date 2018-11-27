package com.brightercode.discourse

import com.brightercode.discourse.api.{Categories, Posts, Topics}
import com.brightercode.discourse.util.PlayWebServiceClient


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
  extends
    PlayWebServiceClient(urlBase, Map(
      "api_key" -> apiKey,
      "api_username" -> username,
    ))
    with Categories
    with Posts
    with Topics