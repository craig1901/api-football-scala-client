package com.footballsync.model

import cats.syntax.either._
import io.circe._
import io.circe.syntax._

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

package object model {
  implicit def eitherDecoder[A, B](implicit a: Decoder[A], b: Decoder[B]): Decoder[Either[A, B]] = {
    val left: Decoder[Either[A, B]] = a.map(Left.apply)
    val right: Decoder[Either[A, B]] = b.map(Right.apply)
    left.or(right)
  }

  implicit def eitherEncoder[A, B](implicit a: Encoder[A], b: Encoder[B]): Encoder[Either[A, B]] = {
    Encoder.instance {
      case Left(value)  => value.asJson(a)
      case Right(value) => value.asJson(b)
    }
  }

  implicit def instantDecoder: Decoder[Instant] = Decoder.decodeString.emap { str =>
    val tryParseInstant =
      Either
        .catchNonFatal(Instant.parse(str))
        .leftMap(_ => "First parsing attempt failed")

    tryParseInstant
      .orElse {
        Either
          .catchNonFatal(ZonedDateTime.parse(str, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant)
          .leftMap(err => s"Error decoding Instant - $str with error: ${err.getMessage}")
      }
  }

  implicit def instantEncoder: Encoder[Instant] = Encoder.encodeString.contramap(_.toString)
}
