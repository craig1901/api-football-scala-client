package com.footballsync.model

import io.circe._
import io.circe.generic.semiauto._

object Teams {

  case class Team(
    code: Option[String],
    country: Option[String],
    founded: Option[Int],
    id: Int,
    logo: Option[String],
    name: Option[String],
    national: Option[Boolean]
  )

  case class Venue(
    address: Option[String],
    capacity: Option[Int],
    city: Option[String],
    id: Option[Int],
    image: Option[String],
    name: Option[String],
    surface: Option[String]
  )

  implicit val teamCodec: Codec[Team] = deriveCodec
  implicit val venueCodec: Codec[Venue] = deriveCodec
}
