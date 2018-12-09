package com.brightercode.discourse.methods

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Post
import com.brightercode.discourse.model.Post._
import play.api.libs.json.Json.toJson
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.Future

class PostApi(api: DiscourseForumApiClient) {

  def create(post: Post): Future[StandaloneWSRequest#Self#Response] =
    api.url("posts.json")
      .post(toJson(post))
}
