package com.brightercode.discourse.exceptions

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class DiscourseException(action: String, errors: List[String]) {
  override def toString: String = s"Errors during $action: ${errors.mkString(", ")}"
}

object DiscourseException {
  implicit val errorReads: Reads[DiscourseException] = (
  (JsPath \ "action").read[String] and
  (JsPath \ "errors").read[List[String]]
  )(DiscourseException.apply _)
}
