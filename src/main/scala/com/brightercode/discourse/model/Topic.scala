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

  sealed trait Order
  case object Created extends Order
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

