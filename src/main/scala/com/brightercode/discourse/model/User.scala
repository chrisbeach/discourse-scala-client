package com.brightercode.discourse.model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

trait User {
  def id: Int
  def username: String
  def name: Option[String]
  def avatar(size: AvatarSize = Avatar25): String
}

sealed abstract class AvatarSize(val key: String)
case object Avatar25 extends AvatarSize("25")
case object Avatar120 extends AvatarSize("120")

case class SimpleUser(id: Int,
                      username: String,
                      name: Option[String],
                      avatarTemplate: String
                    ) extends User {
  override def avatar(size: AvatarSize): String =
    avatarTemplate.replaceAll("\\{size\\}", size.key)
}

object SimpleUser {
  implicit val userReads: Reads[SimpleUser] = (
    (JsPath \ "id").read[Int] and
    (JsPath \ "username").read[String] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "avatar_template").read[String]
  )(SimpleUser.apply _)
}