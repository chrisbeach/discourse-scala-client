package com.brightercode.discourse.model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

object SparseTopic {
  implicit val topicReads: Reads[SparseTopic] = (
    (JsPath \ "id").readNullable[Int] and
    (JsPath \ "title").read[String] and
    (JsPath \ "pinned").readNullableWithDefault[Boolean](None) and
    (JsPath \ "posts_count").read[Int] and
    (JsPath \ "reply_count").read[Int] and
    (JsPath \ "views").readNullableWithDefault[Int](None) and
    (JsPath \ "posters" \ 0 \ "user_id").read[Int] and
    (JsPath \ "topic_post_bookmarked").read[Boolean]
  )(SparseTopic.apply _)
}

case class TopicTemplate(title: String,
                         raw: String,
                         categoryId: Int)

object TopicTemplate {
  implicit val topicTemplateWrites: Writes[TopicTemplate] = (topic: TopicTemplate) =>
    Json.obj(
      "title" -> topic.title,
      "raw" -> topic.raw,
      "category" -> topic.categoryId
    )
}

case class CreatedPost(id: Int, cooked: String, userId: Int, topicId: Int)

object CreatedPost {
  implicit val createdPostReads: Reads[CreatedPost] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "cooked").read[String] and
      (JsPath \ "user_id").read[Int] and
      (JsPath \ "topic_id").read[Int]
    )(CreatedPost.apply _)
}

case class SparseTopic(id: Option[Int] = None,
                       title: String,
                       pinned: Option[Boolean] = None,
                       postCount: Int,
                       replyCount: Int,
                       views: Option[Int] = None,
                       authorUserId: Int,
                       topicPostBookmarked: Boolean) {
  override def toString: String = s"'$title' (id=$id, author=$authorUserId)"
}

case class Topic(id: Option[Int] = None,
                 title: String,
                 pinned: Boolean = false,
                 postCount: Int,
                 replyCount: Int,
                 views: Int,
                 author: User,
                 topicPostBookmarked: Boolean) {
  override def toString: String = s"'$title' (id=$id, author=@${author.username})"
}

object Topic {
  def from(sparseTopic: SparseTopic, userLookup: Int => User) =
    Topic(
      sparseTopic.id,
      sparseTopic.title,
      sparseTopic.pinned.getOrElse(false),
      sparseTopic.postCount,
      sparseTopic.replyCount,
      sparseTopic.views.getOrElse(0),
      userLookup(sparseTopic.authorUserId),
      sparseTopic.topicPostBookmarked
    )

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