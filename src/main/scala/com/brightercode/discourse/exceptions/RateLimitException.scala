package com.brightercode.discourse.exceptions

case class RateLimitException(messages: List[String], waitSecs: Option[Long])
  extends Exception(messages.mkString(", "))

object RateLimitException {
  def apply(error: TypedDiscourseException) =
    new RateLimitException(
      error.errors,
      error.extras.flatMap(_.fields.toMap.get("wait_seconds").map(_.as[Long]))
    )
}