package com.footballsync.model

import io.circe._
import io.circe.generic.semiauto._

case class TeamStatistics(
  teamId: Int,
  leagueId: Int,
  season: Int,
  form: Option[String],
  fixturesPlayedHome: Option[Int],
  fixturesPlayedAway: Option[Int],
  fixturesPlayedTotal: Option[Int],
  fixturesWinsHome: Option[Int],
  fixturesWinsAway: Option[Int],
  fixturesWinsTotal: Option[Int],
  fixturesDrawsHome: Option[Int],
  fixturesDrawsAway: Option[Int],
  fixturesDrawsTotal: Option[Int],
  fixturesLosesHome: Option[Int],
  fixturesLosesAway: Option[Int],
  fixturesLosesTotal: Option[Int],
  goalsForTotalHome: Option[Int],
  goalsForTotalAway: Option[Int],
  goalsForTotalTotal: Option[Int],
  goalsForAverageHome: Option[String],
  goalsForAverageAway: Option[String],
  goalsForAverageTotal: Option[String],
  goalsAgainstTotalHome: Option[Int],
  goalsAgainstTotalAway: Option[Int],
  goalsAgainstTotalTotal: Option[Int],
  goalsAgainstAverageHome: Option[String],
  goalsAgainstAverageAway: Option[String],
  goalsAgainstAverageTotal: Option[String],
  biggestStreakWins: Option[Int],
  biggestStreakDraws: Option[Int],
  biggestStreakLoses: Option[Int],
  biggestWinsHome: Option[String],
  biggestWinsAway: Option[String],
  biggestLosesHome: Option[String],
  biggestLosesAway: Option[String],
  biggestGoalsForHome: Option[Int],
  biggestGoalsForAway: Option[Int],
  biggestGoalsAgainstHome: Option[Int],
  biggestGoalsAgainstAway: Option[Int],
  cleanSheetHome: Option[Int],
  cleanSheetAway: Option[Int],
  cleanSheetTotal: Option[Int],
  failedToScoreHome: Option[Int],
  failedToScoreAway: Option[Int],
  failedToScoreTotal: Option[Int],
  penaltyScoredTotal: Option[Int],
  penaltyScoredPercentage: Option[String],
  penaltyMissedTotal: Option[Int],
  penaltyMissedPercentage: Option[String],
  penaltyTotal: Option[Int]
)
object TeamStatistics {
  implicit val teamStatisticsCodec: Codec[TeamStatistics] = deriveCodec

  case class TeamStatisticsTeam(
    id: Int,
    name: String,
    logo: String
  )

  object TeamStatisticsTeam {
    implicit val teamCodec: Codec[TeamStatisticsTeam] = deriveCodec
  }

  case class TeamStatisticsLeague(
    id: Option[Int],
    name: Option[String],
    country: Option[String],
    logo: Option[String],
    flag: Option[String],
    season: Option[Int]
  )

  object TeamStatisticsLeague {
    implicit val teamStatisticsLeagueCodec: Codec[TeamStatisticsLeague] = deriveCodec
  }

  case class TeamStatisticsFixtures(
    played: TeamStatisticsFixturesPlayed,
    wins: TeamStatisticsFixturesWins,
    draws: TeamStatisticsFixturesDraws,
    loses: TeamStatisticsFixturesLoses
  )

  object TeamStatisticsFixtures {
    implicit val teamStatisticsFixturesCodec: Codec[TeamStatisticsFixtures] = deriveCodec
  }

  case class TeamStatisticsFixturesPlayed(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsFixturesPlayed {
    implicit val teamStatisticsFixturesPlayedCodec: Codec[TeamStatisticsFixturesPlayed] = deriveCodec
  }

  case class TeamStatisticsFixturesWins(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsFixturesWins {
    implicit val teamStatisticsFixturesWinsCodec: Codec[TeamStatisticsFixturesWins] = deriveCodec
  }

  case class TeamStatisticsFixturesDraws(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsFixturesDraws {
    implicit val teamStatisticsFixturesDrawsCodec: Codec[TeamStatisticsFixturesDraws] = deriveCodec
  }

  case class TeamStatisticsFixturesLoses(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsFixturesLoses {
    implicit val teamStatisticsFixturesLosesCodec: Codec[TeamStatisticsFixturesLoses] = deriveCodec
  }

  case class TeamStatisticsGoals(
    `for`: TeamStatisticsGoalsFor,
    against: TeamStatisticsGoalsAgainst
  )

  object TeamStatisticsGoals {
    implicit val teamStatisticsGoalsCodec: Codec[TeamStatisticsGoals] = deriveCodec
  }

  case class TeamStatisticsGoalsFor(
    total: TeamStatisticsGoalsForTotal,
    average: TeamStatisticsGoalsForAverage,
    minute: Map[String, TeamStatisticsGoalsForMinute]
  )

  object TeamStatisticsGoalsFor {
    implicit val teamStatisticsGoalsForCodec: Codec[TeamStatisticsGoalsFor] = deriveCodec
  }

  case class TeamStatisticsGoalsForTotal(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsGoalsForTotal {
    implicit val teamStatisticsGoalsForTotalCodec: Codec[TeamStatisticsGoalsForTotal] = deriveCodec
  }

  case class TeamStatisticsGoalsForAverage(
    home: String,
    away: String,
    total: String
  )

  object TeamStatisticsGoalsForAverage {
    implicit val teamStatisticsGoalsForAverageCodec: Codec[TeamStatisticsGoalsForAverage] = deriveCodec
  }

  case class TeamStatisticsGoalsForMinute(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsGoalsForMinute {
    implicit val teamStatisticsGoalsForMinuteCodec: Codec[TeamStatisticsGoalsForMinute] = deriveCodec
  }

  case class TeamStatisticsGoalsAgainst(
    total: TeamStatisticsGoalsAgainstTotal,
    average: TeamStatisticsGoalsAgainstAverage,
    minute: Map[String, TeamStatisticsGoalsAgainstMinute]
  )

  object TeamStatisticsGoalsAgainst {
    implicit val teamStatisticsGoalsAgainstCodec: Codec[TeamStatisticsGoalsAgainst] = deriveCodec
  }

  case class TeamStatisticsGoalsAgainstTotal(
    home: Int,
    away: Int,
    total: Int
  )

  object TeamStatisticsGoalsAgainstTotal {
    implicit val teamStatisticsGoalsAgainstTotalCodec: Codec[TeamStatisticsGoalsAgainstTotal] = deriveCodec
  }

  case class TeamStatisticsGoalsAgainstAverage(
    home: String,
    away: String,
    total: String
  )

  object TeamStatisticsGoalsAgainstAverage {
    implicit val teamStatisticsGoalsAgainstAverageCodec: Codec[TeamStatisticsGoalsAgainstAverage] = deriveCodec
  }

  case class TeamStatisticsGoalsAgainstMinute(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsGoalsAgainstMinute {
    implicit val teamStatisticsGoalsAgainstMinuteCodec: Codec[TeamStatisticsGoalsAgainstMinute] = deriveCodec
  }

  case class TeamStatisticsBiggest(
    streak: TeamStatisticsBiggestStreak,
    wins: TeamStatisticsBiggestWins,
    loses: TeamStatisticsBiggestLoses,
    goals: TeamStatisticsBiggestGoals
  )

  object TeamStatisticsBiggest {
    implicit val teamStatisticsBiggestCodec: Codec[TeamStatisticsBiggest] = deriveCodec
  }

  case class TeamStatisticsBiggestStreak(
    wins: Option[Int],
    draws: Option[Int],
    loses: Option[Int]
  )

  object TeamStatisticsBiggestStreak {
    implicit val teamStatisticsBiggestStreakCodec: Codec[TeamStatisticsBiggestStreak] = deriveCodec
  }

  case class TeamStatisticsBiggestWins(
    home: Option[String],
    away: Option[String]
  )

  object TeamStatisticsBiggestWins {
    implicit val teamStatisticsBiggestWinsCodec: Codec[TeamStatisticsBiggestWins] = deriveCodec
  }

  case class TeamStatisticsBiggestLoses(
    home: Option[String],
    away: Option[String]
  )

  object TeamStatisticsBiggestLoses {
    implicit val teamStatisticsBiggestLosesCodec: Codec[TeamStatisticsBiggestLoses] = deriveCodec
  }

  case class TeamStatisticsBiggestGoals(
    `for`: TeamStatisticsBiggestGoalsFor,
    against: TeamStatisticsBiggestGoalsAgainst
  )

  object TeamStatisticsBiggestGoals {
    implicit val teamStatisticsBiggestGoalsCodec: Codec[TeamStatisticsBiggestGoals] = deriveCodec
  }

  case class TeamStatisticsBiggestGoalsFor(
    home: Option[Int],
    away: Option[Int]
  )

  object TeamStatisticsBiggestGoalsFor {
    implicit val teamStatisticsBiggestGoalsForCodec: Codec[TeamStatisticsBiggestGoalsFor] = deriveCodec
  }

  case class TeamStatisticsBiggestGoalsAgainst(
    home: Option[Int],
    away: Option[Int]
  )

  object TeamStatisticsBiggestGoalsAgainst {
    implicit val teamStatisticsBiggestGoalsAgainstCodec: Codec[TeamStatisticsBiggestGoalsAgainst] = deriveCodec
  }

  case class TeamStatisticsCleanSheet(
    home: Option[Int],
    away: Option[Int],
    total: Option[Int]
  )

  object TeamStatisticsCleanSheet {
    implicit val teamStatisticsCleanSheetCodec: Codec[TeamStatisticsCleanSheet] = deriveCodec
  }

  case class TeamStatisticsFailedToScore(
    home: Option[Int],
    away: Option[Int],
    total: Option[Int]
  )

  object TeamStatisticsFailedToScore {
    implicit val teamStatisticsFailedToScoreCodec: Codec[TeamStatisticsFailedToScore] = deriveCodec
  }

  case class TeamStatisticsPenalty(
    scored: TeamStatisticsPenaltyScored,
    missed: TeamStatisticsPenaltyMissed,
    total: Option[Int]
  )

  object TeamStatisticsPenalty {
    implicit val teamStatisticsPenaltyCodec: Codec[TeamStatisticsPenalty] = deriveCodec
  }

  case class TeamStatisticsPenaltyScored(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsPenaltyScored {
    implicit val teamStatisticsPenaltyScoredCodec: Codec[TeamStatisticsPenaltyScored] = deriveCodec
  }

  case class TeamStatisticsPenaltyMissed(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsPenaltyMissed {
    implicit val teamStatisticsPenaltyMissedCodec: Codec[TeamStatisticsPenaltyMissed] = deriveCodec
  }

  case class TeamStatisticsLineup(
    formation: Option[String],
    played: Option[Int]
  )

  object TeamStatisticsLineup {
    implicit val teamStatisticsLineupCodec: Codec[TeamStatisticsLineup] = deriveCodec
  }

  case class TeamStatisticsCards(
    yellow: Map[String, TeamStatisticsCardsYellow],
    red: Map[String, TeamStatisticsCardsRed]
  )

  object TeamStatisticsCards {
    implicit val teamStatisticsCardsCodec: Codec[TeamStatisticsCards] = deriveCodec
  }

  case class TeamStatisticsCardsYellow(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsCardsYellow {
    implicit val teamStatisticsCardsYellowCodec: Codec[TeamStatisticsCardsYellow] = deriveCodec
  }

  case class TeamStatisticsCardsRed(
    total: Option[Int],
    percentage: Option[String]
  )

  object TeamStatisticsCardsRed {
    implicit val teamStatisticsCardsRedCodec: Codec[TeamStatisticsCardsRed] = deriveCodec
  }
}
