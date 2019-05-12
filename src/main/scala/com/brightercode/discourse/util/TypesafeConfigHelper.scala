package com.brightercode.discourse.util

import com.brightercode.discourse.DiscourseEndpointConfig
import com.typesafe.config.Config
import TimeConversions._

import scala.language.implicitConversions

object TypesafeConfigHelper {
  implicit def discourseConfigFromTypesafe(config: Config): DiscourseEndpointConfig =
    DiscourseEndpointConfig(
      baseUrl = config.getString("url"),
      username = config.getString("username"),
      key = config.getString("key"),
      timeout = config.getDuration("timeout")
    )
}
