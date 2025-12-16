package com.footballsync.model

import io.circe._
import io.circe.generic.semiauto._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object Players {
  private val dateformatter = DateTimeFormatter.ofPattern("yyyy-M-d")

  implicit val decodeLocalDate: Decoder[LocalDate] = Decoder.decodeString.emapTry { str =>
    Try(LocalDate.parse(str, dateformatter))
  }
  final case class PlayerBirth(
    date: Option[LocalDate],
    place: Option[String],
    country: Option[String]
  )

  final case class Player(
    id: Int,
    name: Option[String],
    firstname: Option[String],
    lastname: Option[String],
    age: Option[Int],
    birth: Option[PlayerBirth],
    nationality: Option[String],
    height: Option[String],
    weight: Option[String],
    injured: Option[Boolean],
    photo: Option[String]
  )

  final case class TeamInfo(
    id: Option[Int],
    name: Option[String],
    logo: Option[String]
  )

  final case class LeagueInfo(
    id: Option[Int],
    name: Option[String],
    country: Option[String],
    logo: Option[String],
    flag: Option[String],
    season: Option[Int]
  )

  final case class PlayerGames(
    appearences: Option[Int],
    lineups: Option[Int],
    minutes: Option[Int],
    number: Option[Int],
    position: Option[String],
    rating: Option[String],
    captain: Option[Boolean]
  )

  final case class Substitutes(
    in: Option[Int],
    out: Option[Int],
    bench: Option[Int]
  )

  final case class Shots(
    total: Option[Int],
    on: Option[Int]
  )

  final case class Goals(
    total: Option[Int],
    conceded: Option[Int],
    assists: Option[Int],
    saves: Option[Int]
  )

  final case class Passes(
    total: Option[Int],
    key: Option[Int],
    accuracy: Option[Int]
  )

  final case class Tackles(
    total: Option[Int],
    blocks: Option[Int],
    interceptions: Option[Int]
  )

  final case class Duels(
    total: Option[Int],
    won: Option[Int]
  )

  final case class Dribbles(
    attempts: Option[Int],
    success: Option[Int],
    past: Option[Int]
  )

  final case class Fouls(
    drawn: Option[Int],
    committed: Option[Int]
  )

  final case class Cards(
    yellow: Option[Int],
    yellowred: Option[Int],
    red: Option[Int]
  )

  final case class PlayerPenalty(
    won: Option[Int],
    commited: Option[Int],
    scored: Option[Int],
    missed: Option[Int],
    saved: Option[Int]
  )

  final case class PlayerStatistic(
    team: Option[TeamInfo],
    league: Option[LeagueInfo],
    games: Option[PlayerGames],
    substitutes: Option[Substitutes],
    shots: Option[Shots],
    goals: Option[Goals],
    passes: Option[Passes],
    tackles: Option[Tackles],
    duels: Option[Duels],
    dribbles: Option[Dribbles],
    fouls: Option[Fouls],
    cards: Option[Cards],
    penalty: Option[PlayerPenalty]
  )

  implicit val playerBirthDecoder: Decoder[PlayerBirth] = deriveDecoder
  implicit val playerBirthEncoder: Encoder[PlayerBirth] = deriveEncoder

  implicit val playerDecoder: Decoder[Player] = deriveDecoder
  implicit val playerEncoder: Encoder[Player] = deriveEncoder

  implicit val teamInfoDecoder: Decoder[TeamInfo] = deriveDecoder
  implicit val teamInfoEncoder: Encoder[TeamInfo] = deriveEncoder

  implicit val leagueInfoDecoder: Decoder[LeagueInfo] = deriveDecoder
  implicit val leagueInfoEncoder: Encoder[LeagueInfo] = deriveEncoder

  implicit val playerGamesDecoder: Decoder[PlayerGames] = deriveDecoder
  implicit val playerGamesEncoder: Encoder[PlayerGames] = deriveEncoder

  implicit val substitutesDecoder: Decoder[Substitutes] = deriveDecoder
  implicit val substitutesEncoder: Encoder[Substitutes] = deriveEncoder

  implicit val shotsDecoder: Decoder[Shots] = deriveDecoder
  implicit val shotsEncoder: Encoder[Shots] = deriveEncoder

  implicit val goalsDecoder: Decoder[Goals] = deriveDecoder
  implicit val goalsEncoder: Encoder[Goals] = deriveEncoder

  implicit val passesDecoder: Decoder[Passes] = deriveDecoder
  implicit val passesEncoder: Encoder[Passes] = deriveEncoder

  implicit val tacklesDecoder: Decoder[Tackles] = deriveDecoder
  implicit val tacklesEncoder: Encoder[Tackles] = deriveEncoder

  implicit val duelsDecoder: Decoder[Duels] = deriveDecoder
  implicit val duelsEncoder: Encoder[Duels] = deriveEncoder

  implicit val dribblesDecoder: Decoder[Dribbles] = deriveDecoder
  implicit val dribblesEncoder: Encoder[Dribbles] = deriveEncoder

  implicit val foulsDecoder: Decoder[Fouls] = deriveDecoder
  implicit val foulsEncoder: Encoder[Fouls] = deriveEncoder

  implicit val cardsDecoder: Decoder[Cards] = deriveDecoder
  implicit val cardsEncoder: Encoder[Cards] = deriveEncoder

  implicit val penaltyDecoder: Decoder[PlayerPenalty] = deriveDecoder
  implicit val penaltyEncoder: Encoder[PlayerPenalty] = deriveEncoder

  implicit val playerStatisticDecoder: Decoder[PlayerStatistic] = deriveDecoder
  implicit val playerStatisticEncoder: Encoder[PlayerStatistic] = deriveEncoder

}
