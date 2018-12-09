package com.brightercode.discourse.methods

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Category
import play.api.libs.json.{JsArray, JsDefined, JsUndefined, JsValue}
import play.api.libs.ws.JsonBodyReadables._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class CategoryApi(api: DiscourseForumApiClient) {

  def list(): Future[Seq[Category]] =
    api.url(s"categories.json")
      .get()
      .map( _.body[JsValue] \ "category_list" \ "categories" match {
        case JsDefined(array: JsArray) => array.value.map { _.validate[Category].get }
        case undefined: JsUndefined => sys.error(s"Couldn't read list: ${undefined.error} ${undefined.validationError}")
        case _ => sys.error(s"Couldn't read list")
      }
    )
}