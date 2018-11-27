package com.brightercode.discourse.model

import play.api.libs.json.{Json, Writes}

case class Post(topicId: Int, raw: String)

object Post {
  implicit val postWrites: Writes[Post] = (post: Post) => Json.obj(
    "topic_id" -> post.topicId,
    "raw" -> post.raw
  )
}
