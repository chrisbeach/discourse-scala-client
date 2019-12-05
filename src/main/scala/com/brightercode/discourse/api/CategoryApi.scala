package com.brightercode.discourse.api

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Category
import play.api.libs.json.{JsArray, JsDefined, JsError, JsSuccess, JsUndefined, JsValue}
import play.api.libs.ws.JsonBodyReadables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CategoryApi(api: DiscourseForumApiClient) {

  def list(): Future[Seq[Category]] =
    api.url(s"categories.json")
      .get()
      .map( _.body[JsValue] \ "category_list" \ "categories" match {
        case JsDefined(array: JsArray) => array.value.map { js =>
          js.validate[Category] match {
            case JsSuccess(value, _) => value
            case JsError(errors) => sys.error(s"Couldn't read category: $errors")
          }
        }
        case undefined: JsUndefined => sys.error(s"Couldn't read list: ${undefined.error} ${undefined.validationError}")
        case _ => sys.error(s"Couldn't read list")
      }
    )
}