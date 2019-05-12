package com.brightercode.discourse.methods

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.exceptions.{DiscourseException, RateLimitException, TypedDiscourseException}
import com.brightercode.discourse.model.Topic.Order
import com.brightercode.discourse.model.TopicTemplate._
import com.brightercode.discourse.model._
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json.toJson
import play.api.libs.json.{Json, _}
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.StandaloneWSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TopicApi(api: DiscourseForumApiClient) extends LazyLogging {

  def create(topic: TopicTemplate): Future[CreatedPost] =
    api.url(s"posts.json")
      .post(toJson(topic))
    .map(_.body[JsValue]).map { json =>
      json.validate[CreatedPost].orElse {
        json.validate[DiscourseException].orElse {
          json.validate[TypedDiscourseException]
        }
      } match {
        case JsSuccess(value: CreatedPost, _) => value
        case JsSuccess(value: DiscourseException, _) => sys.error(value.toString)
        case JsSuccess(value: TypedDiscourseException, _) if value.errorType == "rate_limit" =>
          logger.debug(json.toString())
          throw RateLimitException(value)
        case JsSuccess(value: TypedDiscourseException, _) => sys.error(value.toString)
        case JsSuccess(value, _) => sys.error(s"Unexpected when reading new topic: $value")
        case JsError(errors) => sys.error(s"Errors reading new topic: $errors\nJSON: $json")
      }
    }

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