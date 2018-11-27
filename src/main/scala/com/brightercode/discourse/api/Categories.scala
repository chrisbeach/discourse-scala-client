package com.brightercode.discourse.api

import com.brightercode.discourse.DiscourseForumApiClient
import com.brightercode.discourse.model.Category
import play.api.libs.json.{JsArray, JsDefined, JsUndefined, JsValue}
import play.api.libs.ws.JsonBodyReadables._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait Categories {
  self: DiscourseForumApiClient =>

  def categories(): Future[Seq[Category]] =
    url(s"categories.json")
      .get()
      .map( _.body[JsValue] \ "category_list" \ "categories" match {
        case JsDefined(array: JsArray) => array.value.map { _.validate[Category].get }
        case undefined: JsUndefined => sys.error(s"Couldn't read categories: ${undefined.error} ${undefined.validationError}")
        case _ => sys.error(s"Couldn't read categories")
      }
    )
}