package com.brightercode.discourse.exceptions

import scala.concurrent.duration._
import scala.language.postfixOps

case class RateLimitException(messages: List[String], waitDuration: Option[FiniteDuration])
  extends Exception(messages.mkString(", "))

object RateLimitException {
  def apply(error: TypedDiscourseException) =
    new RateLimitException(
      error.errors,
      error.extras.flatMap(_.fields.toMap.get("wait_seconds").map(jsValue => jsValue.as[Long] seconds))
    )
}