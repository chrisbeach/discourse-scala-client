package com.brightercode.discourse.methods

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Topic.Order
import com.brightercode.discourse.model.{SimpleUser, SparseTopic, Topic}
import play.api.libs.json._
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
      val usersById = (json \ "users" match {
        case JsDefined(array: JsArray) => array.value.map {
          _.validate[SimpleUser] match {
            case JsSuccess(value, _) => value
            case JsError(errors) => sys.error(s"Errors reading user from topic list: $errors")
          }
        }
        case other => sys.error(s"Read unexpected $other when expecting users from ${Json.prettyPrint(json)}")
      }).map(user => user.id -> user).toMap

      json \ "topic_list" \ "topics" match {
        case JsDefined(array: JsArray) => array.value.map { obj =>
          Topic.from(obj.validate[SparseTopic].get, usersById.apply)
        }
        case _ => sys.error(s"Couldn't read topics from ${Json.prettyPrint(json)}")
      }
    }

  def bookmark(topicId: Int): Future[StandaloneWSRequest#Self#Response] =
    api.url(s"t/$topicId/bookmark")
      .put(Json.obj())


  private def orderParam(maybeOrder: Option[Topic.Order]) =
    maybeOrder match {
      case Some(order) => Map("order" -> order.queryStringValue)
      case None => Map.empty[String, String]
    }
}
