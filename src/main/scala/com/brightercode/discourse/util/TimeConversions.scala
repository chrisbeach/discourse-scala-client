package com.brightercode.discourse.util

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

object TimeConversions {
  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)
}