package com.brightercode.discourse.util

import com.brightercode.discourse.exceptions.RateLimitException
import com.typesafe.scalalogging.LazyLogging

object RateLimitUtil extends LazyLogging {
  def sleepForRateLimit(e: RateLimitException): Unit = {
    logger.warn(e.getMessage, e)
    e.waitSecs match {
      case Some(secs) =>
        logger.warn(s"Pausing forum posts for $secs seconds")
        Thread.sleep(secs * 1000)
      case _ => logger.error("No wait time specified in RateLimitException")
    }
  }
}
