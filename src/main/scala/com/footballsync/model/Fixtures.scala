package com.footballsync.model

import com.footballsync.model.model.eitherDecoder
import com.footballsync.model.model.eitherEncoder
import com.footballsync.model.model.instantDecoder
import com.footballsync.model.model.instantEncoder
import io.circe._
import io.circe.generic.semiauto._

import java.time.Instant

object Fixtures {
  object FixtureEvent {
    case class EventTeam(
      id: Option[Int],
      name: Option[String],
      logo: Option[String]
    )
    case class EventPlayer(id: Option[Int], name: Option[String])
    case class EventAssist(id: Option[Int], name: Option[String])
    case class EventTime(elapsed: Option[Int], extra: Option[Int])

    case class FixtureEvent(
      time: EventTime,
      team: EventTeam,
      player: EventPlayer,
      assist: EventAssist,
      `type`: Option[String],
      detail: Option[String],
      comments: Option[String]
    )

    implicit val eventTeamCodec: Codec[EventTeam] = deriveCodec
    implicit val eventPlayerCodec: Codec[EventPlayer] = deriveCodec
    implicit val eventAssistCodec: Codec[EventAssist] = deriveCodec
    implicit val eventTimeCodec: Codec[EventTime] = deriveCodec
    implicit val fixtureEventCodec: Codec[FixtureEvent] = deriveCodec
  }

  object FixtureLeague {
    case class FixtureLeague(
      id: Int,
      name: Option[String],
      country: Option[String],
      logo: Option[String],
      flag: Option[String],
      season: Option[Int],
      round: Option[String]
    )

    implicit val fixtureLeagueCodec: Codec[FixtureLeague] = deriveCodec
  }

  object FixtureLineup {
    case class FixtureLineupCoach(id: Option[Int], name: Option[String], photo: Option[String])
    case class LineupPlayer(
      id: Option[Int],
      name: Option[String],
      number: Option[Int],
      pos: Option[String],
      grid: Option[String]
    )
    case class FixtureLineupPlayer(player: Option[LineupPlayer])
    case class FixtureLineupPlayerColors(primary: Option[String], number: Option[String], border: Option[String])
    case class FixtureLineupColors(
      player: Option[FixtureLineupPlayerColors],
      goalkeeper: Option[FixtureLineupPlayerColors]
    )
    case class FixtureLineupTeam(
      id: Option[Int],
      name: Option[String],
      logo: Option[String],
      colors: Option[FixtureLineupColors]
    )
    case class FixtureLineup(
      coach: Option[FixtureLineupCoach],
      formation: Option[String],
      startXI: Option[List[FixtureLineupPlayer]],
      substitutes: Option[List[FixtureLineupPlayer]],
      team: Option[FixtureLineupTeam]
    )

    implicit val fixtureLineupCoachCodec: Codec[FixtureLineupCoach] = deriveCodec
    implicit val lineupPlayerCodec: Codec[LineupPlayer] = deriveCodec
    implicit val fixtureLineupPlayerCodec: Codec[FixtureLineupPlayer] = deriveCodec
    implicit val fixtureLineupPlayerColorsCodec: Codec[FixtureLineupPlayerColors] = deriveCodec
    implicit val fixtureLineupColorsCodec: Codec[FixtureLineupColors] = deriveCodec
    implicit val fixtureLineupTeamCodec: Codec[FixtureLineupTeam] = deriveCodec
    implicit val fixtureLineupCodec: Codec[FixtureLineup] = deriveCodec
  }

  object FixturePlayers {
    case class FixturePlayerInfo(
      id: Option[Int],
      name: Option[String],
      photo: Option[String]
    )
    case class FixturePlayerGames(
      minutes: Option[Int],
      number: Option[Int],
      position: Option[String],
      rating: Option[String],
      captain: Option[Boolean],
      substitute: Option[Boolean]
    )
    case class FixturePlayerShots(total: Option[Int], on: Option[Int])
    case class FixturePlayerGoals(
      total: Option[Int],
      conceded: Option[Int],
      assists: Option[Int],
      saves: Option[Int]
    )
    case class FixturePlayerPasses(total: Option[Int], key: Option[Int], accuracy: Option[String])
    case class FixturePlayerTackles(total: Option[Int], blocks: Option[Int], interceptions: Option[Int])
    case class FixturePlayerDuels(total: Option[Int], won: Option[Int])
    case class FixturePlayerDribbles(attempts: Option[Int], success: Option[Int], past: Option[Int])
    case class FixturePlayerFouls(drawn: Option[Int], committed: Option[Int])
    case class FixturePlayerCards(yellow: Option[Int], red: Option[Int])
    case class FixturePlayerPenalty(
      won: Option[Int],
      commited: Option[Int],
      scored: Option[Int],
      missed: Option[Int],
      saved: Option[Int]
    )
    case class FixturePlayerStatistics(
      games: Option[FixturePlayerGames],
      offsides: Option[Int],
      shots: Option[FixturePlayerShots],
      goals: Option[FixturePlayerGoals],
      passes: Option[FixturePlayerPasses],
      tackles: Option[FixturePlayerTackles],
      duels: Option[FixturePlayerDuels],
      dribbles: Option[FixturePlayerDribbles],
      fouls: Option[FixturePlayerFouls],
      cards: Option[FixturePlayerCards],
      penalty: Option[FixturePlayerPenalty]
    )
    case class FixturePlayer(player: FixturePlayerInfo, statistics: Seq[FixturePlayerStatistics])
    case class FixturePlayersTeam(id: Int, name: String, logo: String, update: String)
    case class FixturePlayers(team: FixturePlayersTeam, players: List[FixturePlayer])

    implicit val fixturePlayerInfoCodec: Codec[FixturePlayerInfo] = deriveCodec
    implicit val fixturePlayerGamesCodec: Codec[FixturePlayerGames] = deriveCodec
    implicit val fixturePlayerShotsCodec: Codec[FixturePlayerShots] = deriveCodec
    implicit val fixturePlayerGoalsCodec: Codec[FixturePlayerGoals] = deriveCodec
    implicit val fixturePlayerPassesCodec: Codec[FixturePlayerPasses] = deriveCodec
    implicit val fixturePlayerTacklesCodec: Codec[FixturePlayerTackles] = deriveCodec
    implicit val fixturePlayerDuelsCodec: Codec[FixturePlayerDuels] = deriveCodec
    implicit val fixturePlayerDribblesCodec: Codec[FixturePlayerDribbles] = deriveCodec
    implicit val fixturePlayerFoulsCodec: Codec[FixturePlayerFouls] = deriveCodec
    implicit val fixturePlayerCardsCodec: Codec[FixturePlayerCards] = deriveCodec
    implicit val fixturePlayerPenaltyCodec: Codec[FixturePlayerPenalty] = deriveCodec
    implicit val fixturePlayerStatsCodec: Codec[FixturePlayerStatistics] = deriveCodec
    implicit val fixturePlayerCodec: Codec[FixturePlayer] = deriveCodec
    implicit val fixturePlayersTeamCodec: Codec[FixturePlayersTeam] = deriveCodec
    implicit val fixturePlayersCodec: Codec[FixturePlayers] = deriveCodec
  }

  object FixtureScore {
    case class Halftime(home: Option[Int], away: Option[Int])
    case class Fulltime(home: Option[Int], away: Option[Int])
    case class ExtraTime(home: Option[Int] = None, away: Option[Int] = None)
    case class Penalty(home: Option[Int] = None, away: Option[Int] = None)
    case class FixtureScore(
      halftime: Halftime,
      fulltime: Fulltime,
      extratime: ExtraTime,
      penalty: Penalty
    )

    implicit val halftimeCodec: Codec[Halftime] = deriveCodec
    implicit val fulltimeCodec: Codec[Fulltime] = deriveCodec
    implicit val extraTimeCodec: Codec[ExtraTime] = deriveCodec
    implicit val penaltyCodec: Codec[Penalty] = deriveCodec
    implicit val fixtureScoreCodec: Codec[FixtureScore] = deriveCodec
  }

  object Fixture {
    case class FixturePeriods(first: Option[Long], second: Option[Long])
    case class FixtureVenue(id: Option[Int], name: Option[String], city: Option[String])
    case class FixtureStatus(long: Option[String], short: Option[String], elapsed: Option[Int])
    case class FixtureTeams(home: FixtureTeam, away: FixtureTeam)
    case class FixtureTeam(
      id: Int,
      name: Option[String],
      logo: Option[String],
      winner: Option[Boolean]
    )
    case class FixtureGoals(home: Option[Int], away: Option[Int])
    case class Fixture(
      id: Int,
      referee: Option[String],
      timezone: Option[String],
      date: Option[Instant],
      timestamp: Option[Long],
      periods: Option[FixturePeriods],
      venue: Option[FixtureVenue],
      status: Option[FixtureStatus]
    )

    implicit val fixturePeriodsCodec: Codec[FixturePeriods] = deriveCodec
    implicit val fixtureVenueCodec: Codec[FixtureVenue] = deriveCodec
    implicit val fixtureStatusCodec: Codec[FixtureStatus] = deriveCodec
    implicit val fixtureTeamsCodec: Codec[FixtureTeams] = deriveCodec
    implicit val fixtureTeamCodec: Codec[FixtureTeam] = deriveCodec
    implicit val fixtureGoalsCodec: Codec[FixtureGoals] = deriveCodec
    implicit val fixtureCodec: Codec[Fixture] = deriveCodec
  }

  object FixtureStatistics {
    case class StatisticTeam(
      id: Int,
      name: Option[String],
      logo: Option[String]
    )
    case class Statistic(`type`: String, value: Option[Either[String, Int]])
    case class TeamStatistic(team: StatisticTeam, statistics: Seq[Statistic])

    implicit val teamCodec: Codec[StatisticTeam] = deriveCodec
    implicit val statisticCodec: Codec[Statistic] = deriveCodec
    implicit val teamStatisticCodec: Codec[TeamStatistic] = deriveCodec
  }
}
