package com.brightercode.discourse.api

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Post
import com.brightercode.discourse.model.Post._
import play.api.libs.json.Json.toJson
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.Future

trait Posts {
  self: DiscourseForumApiClient =>

  def createPost(post: Post): Future[StandaloneWSRequest#Self#Response] =
    url("posts.json")
      .post(toJson(post))
}
