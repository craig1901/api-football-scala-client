package com.footballsync.model

import io.circe._
import io.circe.generic.semiauto._

object Leagues {
  case class LeagueCountry(
    code: Option[String],
    flag: Option[String],
    name: String
  )

  case class League(
    id: Int,
    logo: String,
    name: String,
    `type`: String
  )

  case class LeagueFixturesCoverage(
    events: Boolean,
    lineups: Boolean,
    statistics_fixtures: Boolean,
    statistics_players: Boolean
  )

  case class LeagueCoverage(
    fixtures: LeagueFixturesCoverage,
    injuries: Boolean,
    odds: Boolean,
    players: Boolean,
    predictions: Boolean,
    standings: Boolean,
    top_assists: Boolean,
    top_cards: Boolean,
    top_scorers: Boolean
  )

  case class LeagueSeason(
    coverage: LeagueCoverage,
    current: Boolean,
    end: Option[String],
    start: Option[String],
    year: Int
  )

  implicit val leagueCountryCodec: Codec[LeagueCountry] = deriveCodec
  implicit val leagueCodec: Codec[League] = deriveCodec
  implicit val leagueFixturesCoverageCodec: Codec[LeagueFixturesCoverage] = deriveCodec
  implicit val leagueCoverageCodec: Codec[LeagueCoverage] = deriveCodec
  implicit val leagueSeasonCodec: Codec[LeagueSeason] = deriveCodec
}
