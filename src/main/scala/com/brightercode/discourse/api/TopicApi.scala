package com.brightercode.discourse.api

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Topic
import com.brightercode.discourse.model.Topic.Order
import play.api.libs.json.{JsArray, JsDefined, JsValue, Json}
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopicApi(api: DiscourseForumApiClient) {

  def list(categorySlug: String,
           page: Int = 0,
           order: Option[Order] = None): Future[Seq[Topic]] =
    api.url(s"c/$categorySlug.json", extraQueryParams = orderParam(order))
      .get()
      .map(_.body[JsValue]).map { json =>
        json \ "topic_list" \ "topics" match {
          case JsDefined(array: JsArray) => array.value.map { _.validate[Topic].get }
          case _ => sys.error(s"Couldn't read topics from ${Json.prettyPrint(json)}")
        }
      }

  def get(id: Int): Future[Topic] =
    api.url(s"t/$id.json")
      .get()
      .map { _.body[JsValue].validate[Topic].get }

  def bookmark(topicId: Int): Future[StandaloneWSRequest#Self#Response] =
    api.url(s"t/$topicId/bookmark")
      .put(Json.obj())


  private def orderParam(maybeOrder: Option[Topic.Order]) =
    maybeOrder match {
      case Some(order) => Map("order" -> order.queryStringValue)
      case None => Map.empty[String, String]
    }
}
