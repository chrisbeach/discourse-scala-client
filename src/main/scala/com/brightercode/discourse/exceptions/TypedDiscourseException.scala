package com.brightercode.discourse.exceptions

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsObject, JsPath, Reads}

case class TypedDiscourseException(errorType: String, errors: List[String], extras: Option[JsObject] = None) {
  override def toString: String =
    s"$errorType: ${errors.mkString(", ")}" +
      extras.map(e => s", $e").getOrElse("")
}

object TypedDiscourseException {
  implicit val typedErrorReads: Reads[TypedDiscourseException] = (
    (JsPath \ "error_type").read[String] and
      (JsPath \ "errors").read[List[String]] and
      (JsPath \ "extras").readNullableWithDefault[JsObject](None)
    )(TypedDiscourseException.apply _)
}
