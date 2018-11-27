package com.brightercode.discourse.model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

object Topic {
  implicit val topicReads: Reads[Topic] =
    (
      (JsPath \ "id").read[Int] and
        (JsPath \ "title").read[String] and
        (JsPath \ "pinned").read[Boolean] and
        (JsPath \ "posts_count").read[Int] and
        (JsPath \ "reply_count").read[Int] and
        (JsPath \ "views").read[Int] and
        (JsPath \ "posters" \ 0 \ "user_id").read[Int] and
        (JsPath \ "topic_post_bookmarked").read[Boolean]
    )(Topic.apply _)

  sealed abstract class Order(val queryStringValue: String)
  case object Default extends Order("default")
  case object Created extends Order("created")
  case object Activity extends Order("activity")
  case object Views extends Order("views")
  case object Posts extends Order("posts")
  case object Category extends Order("category")
  case object Likes extends Order("likes")
  case object OpLikes extends Order("op_likes")
  case object Posters extends Order("posters")
}

case class Topic(id: Int,
                 title: String,
                 pinned: Boolean,
                 postCount: Int,
                 replyCount: Int,
                 views: Int,
                 authorUserId: Int,
                 topicPostBookmarked: Boolean) {
  override def toString: String = s"'$title' (id=$id, author=$authorUserId, bookmarked=$topicPostBookmarked)"
}

