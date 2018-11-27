package com.brightercode.discourse.api

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Topic
import com.brightercode.discourse.model.Topic.{Created, Order}
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.json.{JsArray, JsDefined, JsValue, Json}
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Topics {
  self: DiscourseForumApiClient =>

  def latestTopics(categorySlug: String,
                   page: Int = 0,
                   order: Option[Order] = None): Future[Seq[Topic]] =
    url(s"c/$categorySlug.json", extraQueryParams = orderParam(order))
      .get()
      .map(_.body[JsValue]).map { json =>
        json \ "topic_list" \ "topics" match {
          case JsDefined(array: JsArray) => array.value.map { _.validate[Topic].get }
          case _ => sys.error(s"Couldn't read topics from ${Json.prettyPrint(json)}")
        }
      }

  def topic(id: Int): Future[Topic] =
    url(s"t/$id.json")
      .get()
      .map { _.body[JsValue].validate[Topic].get }

  def bookmarkTopic(topicId: Int): Future[StandaloneWSRequest#Self#Response] =
    url(s"t/$topicId/bookmarkTopic")
      .put(Json.obj())

  private def orderParam(maybeOrder: Option[Topic.Order]) =
    maybeOrder match {
      case Some(Created) => Map("order" -> "created")
      case None => Map.empty[String, String]
    }
}
