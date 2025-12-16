package com.footballsync.api.client.model.codec

import cats.kernel.Eq
import com.footballsync.model.Fixtures.Fixture._
import com.footballsync.model.Fixtures.FixtureEvent._
import com.footballsync.model.Fixtures.FixtureLeague._
import com.footballsync.model.Fixtures.FixtureLineup._
import com.footballsync.model.Fixtures.FixturePlayers._
import com.footballsync.model.Fixtures.FixtureScore._
import com.footballsync.model.Fixtures.FixtureStatistics._
import com.footballsync.model.FootballDataResponses._
import com.footballsync.model.Leagues._
import com.footballsync.model.Players._
import com.footballsync.model.TeamStatistics._
import com.footballsync.model.Teams._
import io.circe.testing.ArbitraryInstances
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.rng.Seed

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

trait PropertyBasedTestingInstances extends ArbitraryInstances {
  implicit def a[A]: Eq[A] = Eq.fromUniversalEquals

  implicit def genEitherListOrMap: Gen[Either[List[Map[String, String]], Map[String, String]]] = {
    val genStringMap = Gen.mapOf(Gen.zip(Gen.alphaStr, Gen.alphaStr))
    val genListOfMaps = Gen.listOfN(1, genStringMap)

    Gen.either(
      genListOfMaps,
      genStringMap
    )
  }

  implicit def arbitraryEitherListOrMap: Arbitrary[Either[List[Map[String, String]], Map[String, String]]] = Arbitrary {
    genEitherListOrMap
  }

  implicit def instantGen: Gen[Instant] = for {
    year <- Gen.choose(1970, 2023)
    dayOfYear <- Gen.choose(1, if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 366 else 365)
    secondOfDay <- Gen.choose(0, 86399)
  } yield ZonedDateTime
    .of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    .plusDays(dayOfYear - 1)
    .plusSeconds(secondOfDay)
    .toInstant

  implicit def localDateGen: Gen[LocalDate] = {
    val today = LocalDate.now
    val fiftyYears = today.minusYears(50)
    Gen.choose(fiftyYears, today)
  }

  implicit def arbitraryFixturePeriods: Arbitrary[FixturePeriods] = Arbitrary(
    for {
      first <- Gen.option[Long](Gen.long)
      second <- Gen.option[Long](Gen.long)
    } yield FixturePeriods(first = first, second = second)
  )
  implicit def arbitraryFixtureVenue: Arbitrary[FixtureVenue] = Arbitrary(
    for {
      id <- Gen.option[Int](Gen.choose[Int](1, 10000))
      name <- Gen.option(Gen.alphaNumStr)
      city <- Gen.option(Gen.alphaNumStr)
    } yield FixtureVenue(id = id, name = name, city = city)
  )
  implicit def arbitraryFixtureStatus: Arbitrary[FixtureStatus] = Arbitrary(
    for {
      long <- Gen.option(Gen.alphaNumStr)
      short <- Gen.option(Gen.stringOfN(4, Gen.alphaUpperChar))
      elapsed <- Gen.option(Gen.posNum[Int])
    } yield FixtureStatus(long = long, short = short, elapsed = elapsed)
  )
  implicit def arbitraryFixture: Arbitrary[Fixture] = Arbitrary(
    for {
      id <- Gen.posNum[Int]
      referee <- Gen.option(Gen.alphaNumStr)
      timezone <- Gen.option(Gen.alphaNumStr)
      date <- Gen.option(instantGen)
      timestamp <- Gen.option(Gen.choose[Long](1L, 1000000L))
      periods <- Gen.option(arbitraryFixturePeriods.arbitrary)
      venue <- Gen.option(arbitraryFixtureVenue.arbitrary)
      status <- Gen.option(arbitraryFixtureStatus.arbitrary)
    } yield Fixture(
      id = id,
      referee = referee,
      timezone = timezone,
      date = date,
      timestamp = timestamp,
      periods = periods,
      venue = venue,
      status = status
    )
  )

  implicit def arbitraryFixtureTeam: Arbitrary[FixtureTeam] = Arbitrary(
    for {
      id <- Gen.posNum[Int]
      name <- Gen.option(Gen.alphaNumStr)
      logo <- Gen.option(Gen.alphaNumStr)
      winner <- Gen.option(Gen.oneOf(Seq(true, false)))
    } yield FixtureTeam(id = id, name = name, logo = logo, winner = winner)
  )
  implicit def arbitraryFixtureTeams: Arbitrary[FixtureTeams] = Arbitrary(
    for {
      home <- arbitraryFixtureTeam.arbitrary
      away <- arbitraryFixtureTeam.arbitrary
    } yield FixtureTeams(home = home, away = away)
  )

  implicit def arbitraryFixtureGoals: Arbitrary[FixtureGoals] = Arbitrary(
    for {
      home <- Gen.option(Gen.posNum[Int])
      away <- Gen.option(Gen.posNum[Int])
    } yield FixtureGoals(home = home, away = away)
  )

  implicit val arbitraryFixturePlayerInfo: Arbitrary[FixturePlayerInfo] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      photo <- Gen.option(Gen.alphaStr)
    } yield FixturePlayerInfo(id, name, photo)
  }

  implicit val arbitraryFixturePlayerGames: Arbitrary[FixturePlayerGames] = Arbitrary {
    for {
      minutes <- Gen.option(Gen.posNum[Int])
      number <- Gen.option(Gen.posNum[Int])
      position <- Gen.option(Gen.alphaStr)
      rating <- Gen.option(Gen.alphaStr)
      captain <- Gen.option(Gen.oneOf(true, false))
      substitute <- Gen.option(Gen.oneOf(true, false))
    } yield FixturePlayerGames(minutes, number, position, rating, captain, substitute)
  }

  implicit val arbitraryFixturePlayerShots: Arbitrary[FixturePlayerShots] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      on <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerShots(total, on)
  }

  implicit val arbitraryFixturePlayerGoals: Arbitrary[FixturePlayerGoals] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      conceded <- Gen.option(Gen.posNum[Int])
      assists <- Gen.option(Gen.posNum[Int])
      saves <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerGoals(total, conceded, assists, saves)
  }

  implicit val arbitraryFixturePlayerPasses: Arbitrary[FixturePlayerPasses] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      key <- Gen.option(Gen.posNum[Int])
      accuracy <- Gen.option(Gen.alphaStr)
    } yield FixturePlayerPasses(total, key, accuracy)
  }

  implicit val arbitraryFixturePlayerTackles: Arbitrary[FixturePlayerTackles] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      blocks <- Gen.option(Gen.posNum[Int])
      interceptions <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerTackles(total, blocks, interceptions)
  }

  implicit val arbitraryFixturePlayerDuels: Arbitrary[FixturePlayerDuels] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      won <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerDuels(total, won)
  }

  implicit val arbitraryFixturePlayerDribbles: Arbitrary[FixturePlayerDribbles] = Arbitrary {
    for {
      attempts <- Gen.option(Gen.posNum[Int])
      success <- Gen.option(Gen.posNum[Int])
      past <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerDribbles(attempts, success, past)
  }

  implicit val arbitraryFixturePlayerFouls: Arbitrary[FixturePlayerFouls] = Arbitrary {
    for {
      drawn <- Gen.option(Gen.posNum[Int])
      committed <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerFouls(drawn, committed)
  }

  implicit val arbitraryFixturePlayerCards: Arbitrary[FixturePlayerCards] = Arbitrary {
    for {
      yellow <- Gen.option(Gen.posNum[Int])
      red <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerCards(yellow, red)
  }

  implicit val arbitraryFixturePlayerPenalty: Arbitrary[FixturePlayerPenalty] = Arbitrary {
    for {
      won <- Gen.option(Gen.posNum[Int])
      commited <- Gen.option(Gen.posNum[Int])
      scored <- Gen.option(Gen.posNum[Int])
      missed <- Gen.option(Gen.posNum[Int])
      saved <- Gen.option(Gen.posNum[Int])
    } yield FixturePlayerPenalty(won, commited, scored, missed, saved)
  }

  implicit val arbitraryFixturePlayerStatistics: Arbitrary[FixturePlayerStatistics] = Arbitrary {
    for {
      games <- Arbitrary.arbitrary[Option[FixturePlayerGames]]
      offsides <- Gen.option(Gen.posNum[Int])
      shots <- Arbitrary.arbitrary[Option[FixturePlayerShots]]
      goals <- Arbitrary.arbitrary[Option[FixturePlayerGoals]]
      passes <- Arbitrary.arbitrary[Option[FixturePlayerPasses]]
      tackles <- Arbitrary.arbitrary[Option[FixturePlayerTackles]]
      duels <- Arbitrary.arbitrary[Option[FixturePlayerDuels]]
      dribbles <- Arbitrary.arbitrary[Option[FixturePlayerDribbles]]
      fouls <- Arbitrary.arbitrary[Option[FixturePlayerFouls]]
      cards <- Arbitrary.arbitrary[Option[FixturePlayerCards]]
      penalty <- Arbitrary.arbitrary[Option[FixturePlayerPenalty]]
    } yield FixturePlayerStatistics(
      games,
      offsides,
      shots,
      goals,
      passes,
      tackles,
      duels,
      dribbles,
      fouls,
      cards,
      penalty
    )
  }

  implicit val arbitraryFixturePlayer: Arbitrary[FixturePlayer] = Arbitrary {
    for {
      player <- Arbitrary.arbitrary[FixturePlayerInfo]
      statistics <- Gen.listOfN(5, Arbitrary.arbitrary[FixturePlayerStatistics])
    } yield FixturePlayer(player, statistics)
  }

  implicit val arbitraryFixturePlayersTeam: Arbitrary[FixturePlayersTeam] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      name <- Gen.alphaStr
      logo <- Gen.alphaStr
      update <- Gen.alphaStr
    } yield FixturePlayersTeam(id, name, logo, update)
  }

  implicit val arbitraryFixturePlayers: Arbitrary[FixturePlayers] = Arbitrary {
    for {
      team <- Arbitrary.arbitrary[FixturePlayersTeam]
      players <- Gen.listOfN(11, Arbitrary.arbitrary[FixturePlayer])
    } yield FixturePlayers(team, players)

  }

  implicit val arbitraryEventTeam: Arbitrary[EventTeam] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
    } yield EventTeam(id, name, logo)
  }

  implicit val arbitraryEventPlayer: Arbitrary[EventPlayer] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
    } yield EventPlayer(id, name)
  }

  implicit val arbitraryEventAssist: Arbitrary[EventAssist] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
    } yield EventAssist(id, name)
  }

  implicit val arbitraryEventTime: Arbitrary[EventTime] = Arbitrary {
    for {
      elapsed <- Gen.option(Gen.posNum[Int])
      extra <- Gen.option(Gen.posNum[Int])
    } yield EventTime(elapsed, extra)
  }

  implicit val arbitraryFixtureEvent: Arbitrary[FixtureEvent] = Arbitrary {
    for {
      time <- Arbitrary.arbitrary[EventTime]
      team <- Arbitrary.arbitrary[EventTeam]
      player <- Arbitrary.arbitrary[EventPlayer]
      assist <- Arbitrary.arbitrary[EventAssist]
      eventType <- Gen.option(Gen.alphaStr)
      detail <- Gen.option(Gen.alphaStr)
      comments <- Gen.option(Gen.alphaStr)
    } yield FixtureEvent(time, team, player, assist, eventType, detail, comments)
  }

  implicit val arbitraryFixtureEventsResponse: Arbitrary[FixtureEventsResponse] = Arbitrary {
    for {
      time <- Arbitrary.arbitrary[EventTime]
      team <- Arbitrary.arbitrary[EventTeam]
      player <- Arbitrary.arbitrary[EventPlayer]
      assist <- Arbitrary.arbitrary[EventAssist]
      eventType <- Gen.option(Gen.stringOfN(100, Gen.alphaChar))
      detail <- Gen.option(Gen.stringOfN(100, Gen.alphaChar))
      comments <- Gen.option(Gen.stringOfN(100, Gen.alphaChar))
    } yield FixtureEventsResponse(time, team, player, assist, eventType, detail, comments)
  }

  implicit val arbitraryFixtureLeague: Arbitrary[FixtureLeague] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      name <- Gen.option(Gen.alphaStr)
      country <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
      flag <- Gen.option(Gen.alphaStr)
      season <- Gen.option(Gen.posNum[Int])
      round <- Gen.option(Gen.alphaStr)
    } yield FixtureLeague(id, name, country, logo, flag, season, round)
  }

  implicit val arbitraryFixtureLineupCoach: Arbitrary[FixtureLineupCoach] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      name <- Gen.option(Gen.alphaStr)
      photo <- Gen.option(Gen.alphaStr)
    } yield FixtureLineupCoach(Some(id), name, photo)
  }

  implicit val arbitraryLineupPlayer: Arbitrary[LineupPlayer] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      number <- Gen.option(Gen.posNum[Int])
      pos <- Gen.option(Gen.alphaStr)
      grid <- Gen.option(Gen.alphaStr)
    } yield LineupPlayer(id, name, number, pos, grid)
  }

  implicit val arbitraryFixtureLineupPlayer: Arbitrary[FixtureLineupPlayer] = Arbitrary {
    for {
      player <- Gen.option(Arbitrary.arbitrary[LineupPlayer])
    } yield FixtureLineupPlayer(player)
  }

  implicit val arbitraryFixtureLineupPlayerColors: Arbitrary[FixtureLineupPlayerColors] = Arbitrary {
    for {
      primary <- Gen.option(Gen.alphaStr)
      number <- Gen.option(Gen.alphaStr)
      border <- Gen.option(Gen.alphaStr)
    } yield FixtureLineupPlayerColors(primary, number, border)
  }

  implicit val arbitraryFixtureLineupColors: Arbitrary[FixtureLineupColors] = Arbitrary {
    for {
      playerColors <- Gen.option(Arbitrary.arbitrary[FixtureLineupPlayerColors])
      goalkeeperColors <- Gen.option(Arbitrary.arbitrary[FixtureLineupPlayerColors])
    } yield FixtureLineupColors(playerColors, goalkeeperColors)
  }

  implicit val arbitraryFixtureLineupTeam: Arbitrary[FixtureLineupTeam] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
      colors <- Gen.option(Arbitrary.arbitrary[FixtureLineupColors])
    } yield FixtureLineupTeam(id, name, logo, colors)
  }

  implicit val arbitraryFixtureLineup: Arbitrary[FixtureLineup] = Arbitrary {
    for {
      coach <- Gen.option(Arbitrary.arbitrary[FixtureLineupCoach])
      formation <- Gen.option(Gen.alphaStr)
      startXI <- Gen.option(Gen.listOfN(11, Arbitrary.arbitrary[FixtureLineupPlayer]))
      substitutes <- Gen.option(Gen.listOfN(8, Arbitrary.arbitrary[FixtureLineupPlayer]))
      team <- Gen.option(Arbitrary.arbitrary[FixtureLineupTeam])
    } yield FixtureLineup(coach, formation, startXI, substitutes, team)
  }

  implicit val arbitraryHalftime: Arbitrary[Halftime] = Arbitrary {
    for {
      home <- Gen.option(Gen.posNum[Int])
      away <- Gen.option(Gen.posNum[Int])
    } yield Halftime(home, away)
  }

  implicit val arbitraryFulltime: Arbitrary[Fulltime] = Arbitrary {
    for {
      home <- Gen.option(Gen.posNum[Int])
      away <- Gen.option(Gen.posNum[Int])
    } yield Fulltime(home, away)
  }

  implicit val arbitraryExtraTime: Arbitrary[ExtraTime] = Arbitrary {
    for {
      home <- Gen.option(Gen.posNum[Int])
      away <- Gen.option(Gen.posNum[Int])
    } yield ExtraTime(home, away)
  }

  implicit val arbitraryPenalty: Arbitrary[Penalty] = Arbitrary {
    for {
      home <- Gen.option(Gen.posNum[Int])
      away <- Gen.option(Gen.posNum[Int])
    } yield Penalty(home, away)
  }

  implicit val arbitraryFixtureScore: Arbitrary[FixtureScore] = Arbitrary {
    for {
      halftime <- Arbitrary.arbitrary[Halftime]
      fulltime <- Arbitrary.arbitrary[Fulltime]
      extratime <- Arbitrary.arbitrary[ExtraTime]
      penalty <- Arbitrary.arbitrary[Penalty]
    } yield FixtureScore(halftime, fulltime, extratime, penalty)
  }

  implicit val arbitraryStatisticTeam: Arbitrary[StatisticTeam] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      name <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
    } yield StatisticTeam(id, name, logo)
  }

  implicit val arbitraryStatistic: Arbitrary[Statistic] = Arbitrary {
    for {
      statType <- Gen.alphaStr
      value <- Gen.option(
        Gen.oneOf(
          Gen.alphaStr.map(Left(_)),
          Gen.posNum[Int].map(Right(_))
        )
      )
    } yield Statistic(statType, value)
  }

  implicit val arbitraryTeamStatistic: Arbitrary[TeamStatistic] = Arbitrary {
    for {
      team <- Arbitrary.arbitrary[StatisticTeam]
      statistics <- Gen.listOfN(10, Arbitrary.arbitrary[Statistic])
    } yield TeamStatistic(team, statistics)
  }

  implicit val arbitraryLeagueCountry: Arbitrary[LeagueCountry] = Arbitrary {
    for {
      code <- Gen.option(Gen.stringOfN(2, Gen.alphaChar))
      flag <- Gen.option(Gen.stringOfN(2, Gen.alphaChar))
      name <- Gen.stringOfN(50, Gen.alphaChar)
    } yield LeagueCountry(code, flag, name)
  }

  implicit val arbitraryLeague: Arbitrary[League] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      logo <- Gen.stringOfN(50, Gen.alphaChar)
      name <- Gen.stringOfN(50, Gen.alphaChar)
      `type` <- Gen.stringOfN(50, Gen.alphaChar)
    } yield League(id, logo, name, `type`)
  }

  implicit val arbitraryLeagueFixturesCoverage: Arbitrary[LeagueFixturesCoverage] = Arbitrary {
    for {
      events <- Gen.oneOf(true, false)
      lineups <- Gen.oneOf(true, false)
      statistics_fixtures <- Gen.oneOf(true, false)
      statistics_players <- Gen.oneOf(true, false)
    } yield LeagueFixturesCoverage(events, lineups, statistics_fixtures, statistics_players)
  }

  implicit val arbitraryLeagueCoverage: Arbitrary[LeagueCoverage] = Arbitrary {
    for {
      fixtures <- Arbitrary.arbitrary[LeagueFixturesCoverage]
      injuries <- Gen.oneOf(true, false)
      odds <- Gen.oneOf(true, false)
      players <- Gen.oneOf(true, false)
      predictions <- Gen.oneOf(true, false)
      standings <- Gen.oneOf(true, false)
      top_assists <- Gen.oneOf(true, false)
      top_cards <- Gen.oneOf(true, false)
      top_scorers <- Gen.oneOf(true, false)
    } yield LeagueCoverage(
      fixtures,
      injuries,
      odds,
      players,
      predictions,
      standings,
      top_assists,
      top_cards,
      top_scorers
    )
  }

  implicit val arbitraryLeagueSeason: Arbitrary[LeagueSeason] = Arbitrary {
    for {
      coverage <- Arbitrary.arbitrary[LeagueCoverage]
      current <- Gen.oneOf(true, false)
      end <- Gen.option(Gen.alphaStr)
      start <- Gen.option(Gen.alphaStr)
      year <- Gen.posNum[Int]
    } yield LeagueSeason(coverage, current, end, start, year)
  }

  implicit val arbitraryTeam: Arbitrary[Team] = Arbitrary {
    for {
      code <- Gen.option(Gen.stringOfN(4, Gen.alphaChar))
      country <- Gen.option(Gen.stringOfN(15, Gen.alphaChar))
      founded <- Gen.option(Gen.posNum[Int])
      id <- Gen.posNum[Int]
      logo <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      name <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      national <- Gen.option(Gen.oneOf(true, false))
    } yield Team(code, country, founded, id, logo, name, national)
  }

  implicit val arbitraryVenue: Arbitrary[Venue] = Arbitrary {
    for {
      address <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      capacity <- Gen.option(Gen.posNum[Int])
      city <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      id <- Gen.option(Gen.posNum[Int])
      image <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      name <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
      surface <- Gen.option(Gen.stringOfN(50, Gen.alphaChar))
    } yield Venue(address, capacity, city, id, image, name, surface)
  }

  implicit val arbitrarySingleFixtureResponse: Arbitrary[SingleFixtureResponse] = Arbitrary {
    for {
      fixture <- Arbitrary.arbitrary[Fixture]
      league <- Arbitrary.arbitrary[FixtureLeague]
      teams <- Arbitrary.arbitrary[FixtureTeams]
      goals <- Arbitrary.arbitrary[FixtureGoals]
      score <- Arbitrary.arbitrary[FixtureScore]
      events <- Gen.listOfN(10, Arbitrary.arbitrary[FixtureEvent])
      lineups <- Gen.listOfN(2, Arbitrary.arbitrary[FixtureLineup])
      statistics <- Gen.listOfN(2, Arbitrary.arbitrary[TeamStatistic])
      players <- Gen.listOfN(2, Arbitrary.arbitrary[FixturePlayers])
    } yield SingleFixtureResponse(fixture, league, teams, goals, score, events, lineups, statistics, players)
  }

  implicit val arbitraryMultiFixtureResponse: Arbitrary[MultiFixtureResponse] = Arbitrary {
    for {
      fixture <- Arbitrary.arbitrary[Fixture]
      league <- Arbitrary.arbitrary[FixtureLeague]
      teams <- Arbitrary.arbitrary[FixtureTeams]
      goals <- Arbitrary.arbitrary[FixtureGoals]
      score <- Arbitrary.arbitrary[FixtureScore]
    } yield MultiFixtureResponse(fixture, league, teams, goals, score)
  }

  implicit val arbitraryTeamResponse: Arbitrary[TeamResponse] = Arbitrary {
    for {
      team <- Arbitrary.arbitrary[Team]
      venue <- Arbitrary.arbitrary[Venue]
    } yield TeamResponse(team, venue)
  }

  implicit val arbitraryLeagueResponse: Arbitrary[LeagueResponse] = Arbitrary {
    for {
      country <- Arbitrary.arbitrary[LeagueCountry]
      league <- Arbitrary.arbitrary[League]
      seasons <- Gen.listOfN(1, Arbitrary.arbitrary[LeagueSeason])
    } yield LeagueResponse(country, league, seasons)
  }

  implicit val arbitraryPlayerStatisticsResponse: Arbitrary[PlayerStatisticsResponse] = Arbitrary {
    for {
      player <- Arbitrary.arbitrary[Player]
      statistics <- Gen.listOfN(1, Arbitrary.arbitrary[PlayerStatistic])
    } yield PlayerStatisticsResponse(player, statistics)
  }

  implicit val arbitraryFootballDataResponse: Arbitrary[FootballDataResponse] = Arbitrary {
    Gen.oneOf(
      Arbitrary.arbitrary[SingleFixtureResponse],
      Arbitrary.arbitrary[MultiFixtureResponse],
      Arbitrary.arbitrary[TeamResponse],
      Arbitrary.arbitrary[LeagueResponse]
    )
  }

  implicit val arbitraryPaging: Arbitrary[Paging] = Arbitrary {
    for {
      current <- Gen.choose(1, 1)
      total <- Gen.choose(1, 3)
    } yield Paging(current, total)
  }

  implicit def arbitraryApiResponse[Response <: FootballDataResponse](implicit
    arbResponse: Arbitrary[Response]
  ): Arbitrary[ApiResponse[Response]] = Arbitrary {
    for {
      get <- Gen.alphaStr
      parameters <- Gen.oneOf(
        Gen.listOfN(1, Gen.mapOf(Gen.zip(Gen.alphaStr, Gen.alphaStr))).map(Left(_)),
        Gen.mapOfN(1, Gen.zip(Gen.alphaStr, Gen.alphaStr)).map(Right(_))
      )
      errors <- Gen.oneOf(
        Gen.listOfN(1, Gen.mapOf(Gen.zip(Gen.alphaStr, Gen.alphaStr))).map(Left(_)),
        Gen.mapOf(Gen.zip(Gen.alphaStr, Gen.alphaStr)).map(Right(_))
      )
      results <- Gen.posNum[Int]
      paging <- Arbitrary.arbitrary[Paging]
      response <- Gen.listOfN(1, Arbitrary.arbitrary[Response])
    } yield ApiResponse[Response](get, parameters, errors, results, paging, response)
  }

  implicit val arbitraryPlayerBirth: Arbitrary[PlayerBirth] = Arbitrary {
    for {
      date <- Gen.option(localDateGen)
      place <- Gen.option(Gen.alphaStr)
      country <- Gen.option(Gen.alphaStr)
    } yield PlayerBirth(date, place, country)
  }

  implicit val arbitraryPlayer: Arbitrary[Player] = Arbitrary {
    for {
      id <- Gen.posNum[Int]
      name <- Gen.option(Gen.alphaStr)
      firstname <- Gen.option(Gen.alphaStr)
      lastname <- Gen.option(Gen.alphaStr)
      age <- Gen.option(Gen.posNum[Int])
      birth <- Gen.option(arbitraryPlayerBirth.arbitrary)
      nationality <- Gen.option(Gen.alphaStr)
      height <- Gen.option(Gen.alphaStr)
      weight <- Gen.option(Gen.alphaStr)
      injured <- Gen.option(Gen.oneOf(true, false))
      photo <- Gen.option(Gen.alphaStr)
    } yield Player(id, name, firstname, lastname, age, birth, nationality, height, weight, injured, photo)
  }

  implicit val arbitraryTeamInfo: Arbitrary[TeamInfo] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
    } yield TeamInfo(id, name, logo)
  }

  implicit val arbitraryLeagueInfo: Arbitrary[LeagueInfo] = Arbitrary {
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      country <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
      flag <- Gen.option(Gen.alphaStr)
      season <- Gen.option(Gen.posNum[Int])
    } yield LeagueInfo(id, name, country, logo, flag, season)
  }

  implicit val arbitraryPlayerGames: Arbitrary[PlayerGames] = Arbitrary {
    for {
      appearences <- Gen.option(Gen.posNum[Int])
      lineups <- Gen.option(Gen.posNum[Int])
      minutes <- Gen.option(Gen.posNum[Int])
      number <- Gen.option(Gen.posNum[Int])
      position <- Gen.option(Gen.alphaStr)
      rating <- Gen.option(Gen.alphaStr)
      captain <- Gen.option(Gen.oneOf(true, false))
    } yield PlayerGames(appearences, lineups, minutes, number, position, rating, captain)
  }

  implicit val arbitrarySubstitutes: Arbitrary[Substitutes] = Arbitrary {
    for {
      in <- Gen.option(Gen.posNum[Int])
      out <- Gen.option(Gen.posNum[Int])
      bench <- Gen.option(Gen.posNum[Int])
    } yield Substitutes(in, out, bench)
  }

  implicit val arbitraryShots: Arbitrary[Shots] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      on <- Gen.option(Gen.posNum[Int])
    } yield Shots(total, on)
  }

  implicit val arbitraryGoals: Arbitrary[Goals] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      conceded <- Gen.option(Gen.posNum[Int])
      assists <- Gen.option(Gen.posNum[Int])
      saves <- Gen.option(Gen.posNum[Int])
    } yield Goals(total, conceded, assists, saves)
  }

  implicit val arbitraryPasses: Arbitrary[Passes] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      key <- Gen.option(Gen.posNum[Int])
      accuracy <- Gen.option(Gen.posNum[Int])
    } yield Passes(total, key, accuracy)
  }

  implicit val arbitraryTackles: Arbitrary[Tackles] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      blocks <- Gen.option(Gen.posNum[Int])
      interceptions <- Gen.option(Gen.posNum[Int])
    } yield Tackles(total, blocks, interceptions)
  }

  implicit val arbitraryDuels: Arbitrary[Duels] = Arbitrary {
    for {
      total <- Gen.option(Gen.posNum[Int])
      won <- Gen.option(Gen.posNum[Int])
    } yield Duels(total, won)
  }

  implicit val arbitraryDribbles: Arbitrary[Dribbles] = Arbitrary {
    for {
      attempts <- Gen.option(Gen.posNum[Int])
      success <- Gen.option(Gen.posNum[Int])
      past <- Gen.option(Gen.posNum[Int])
    } yield Dribbles(attempts, success, past)
  }

  implicit val arbitraryFouls: Arbitrary[Fouls] = Arbitrary {
    for {
      drawn <- Gen.option(Gen.posNum[Int])
      committed <- Gen.option(Gen.posNum[Int])
    } yield Fouls(drawn, committed)
  }

  implicit val arbitraryCards: Arbitrary[Cards] = Arbitrary {
    for {
      yellow <- Gen.option(Gen.posNum[Int])
      yellowred <- Gen.option(Gen.posNum[Int])
      red <- Gen.option(Gen.posNum[Int])
    } yield Cards(yellow, yellowred, red)
  }

  implicit val arbitraryPlayerPenalty: Arbitrary[PlayerPenalty] = Arbitrary {
    for {
      won <- Gen.option(Gen.posNum[Int])
      commited <- Gen.option(Gen.posNum[Int])
      scored <- Gen.option(Gen.posNum[Int])
      missed <- Gen.option(Gen.posNum[Int])
      saved <- Gen.option(Gen.posNum[Int])
    } yield PlayerPenalty(won, commited, scored, missed, saved)
  }

  implicit val arbitraryPlayerStatistic: Arbitrary[PlayerStatistic] = Arbitrary {
    for {
      team <- Gen.option(arbitraryTeamInfo.arbitrary)
      league <- Gen.option(arbitraryLeagueInfo.arbitrary)
      games <- Gen.option(arbitraryPlayerGames.arbitrary)
      substitutes <- Gen.option(arbitrarySubstitutes.arbitrary)
      shots <- Gen.option(arbitraryShots.arbitrary)
      goals <- Gen.option(arbitraryGoals.arbitrary)
      passes <- Gen.option(arbitraryPasses.arbitrary)
      tackles <- Gen.option(arbitraryTackles.arbitrary)
      duels <- Gen.option(arbitraryDuels.arbitrary)
      dribbles <- Gen.option(arbitraryDribbles.arbitrary)
      fouls <- Gen.option(arbitraryFouls.arbitrary)
      cards <- Gen.option(arbitraryCards.arbitrary)
      penalty <- Gen.option(arbitraryPlayerPenalty.arbitrary)
    } yield PlayerStatistic(
      team,
      league,
      games,
      substitutes,
      shots,
      goals,
      passes,
      tackles,
      duels,
      dribbles,
      fouls,
      cards,
      penalty
    )
  }

  implicit val arbitraryTeamStatisticsTeam: Arbitrary[TeamStatisticsTeam] = Arbitrary(
    for {
      id <- Gen.posNum[Int]
      name <- Gen.alphaStr
      logo <- Gen.alphaStr
    } yield TeamStatisticsTeam(id, name, logo)
  )

  implicit val arbitraryTeamStatisticsLeague: Arbitrary[TeamStatisticsLeague] = Arbitrary(
    for {
      id <- Gen.option(Gen.posNum[Int])
      name <- Gen.option(Gen.alphaStr)
      country <- Gen.option(Gen.alphaStr)
      logo <- Gen.option(Gen.alphaStr)
      flag <- Gen.option(Gen.alphaStr)
      season <- Gen.option(Gen.chooseNum(2000, 2030))
    } yield TeamStatisticsLeague(id, name, country, logo, flag, season)
  )

  implicit val arbitraryTeamStatisticsFixturesPlayed: Arbitrary[TeamStatisticsFixturesPlayed] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 30)
      away <- Gen.chooseNum(0, 30)
      total <- Gen.chooseNum(0, 60)
    } yield TeamStatisticsFixturesPlayed(home, away, total)
  )

  implicit val arbitraryTeamStatisticsFixturesWins: Arbitrary[TeamStatisticsFixturesWins] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 30)
      away <- Gen.chooseNum(0, 30)
      total <- Gen.chooseNum(0, 60)
    } yield TeamStatisticsFixturesWins(home, away, total)
  )

  implicit val arbitraryTeamStatisticsFixturesDraws: Arbitrary[TeamStatisticsFixturesDraws] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 30)
      away <- Gen.chooseNum(0, 30)
      total <- Gen.chooseNum(0, 60)
    } yield TeamStatisticsFixturesDraws(home, away, total)
  )

  implicit val arbitraryTeamStatisticsFixturesLoses: Arbitrary[TeamStatisticsFixturesLoses] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 30)
      away <- Gen.chooseNum(0, 30)
      total <- Gen.chooseNum(0, 60)
    } yield TeamStatisticsFixturesLoses(home, away, total)
  )

  implicit val arbitraryTeamStatisticsFixtures: Arbitrary[TeamStatisticsFixtures] = Arbitrary(
    for {
      played <- Gen.resultOf(TeamStatisticsFixturesPlayed.apply _)
      wins <- Gen.resultOf(TeamStatisticsFixturesWins.apply _)
      draws <- Gen.resultOf(TeamStatisticsFixturesDraws.apply _)
      loses <- Gen.resultOf(TeamStatisticsFixturesLoses.apply _)
    } yield TeamStatisticsFixtures(played, wins, draws, loses)
  )

  implicit val arbitraryTeamStatisticsGoalsForTotal: Arbitrary[TeamStatisticsGoalsForTotal] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 100)
      away <- Gen.chooseNum(0, 100)
      total <- Gen.chooseNum(0, 200)
    } yield TeamStatisticsGoalsForTotal(home, away, total)
  )

  implicit val arbitraryTeamStatisticsGoalsForAverage: Arbitrary[TeamStatisticsGoalsForAverage] = Arbitrary(
    for {
      home <- Gen.choose(0.0, 5.0).map(_.toString)
      away <- Gen.choose(0.0, 5.0).map(_.toString)
      total <- Gen.choose(0.0, 5.0).map(_.toString)
    } yield TeamStatisticsGoalsForAverage(home, away, total)
  )

  implicit val arbitraryTeamStatisticsGoalsForMinute: Arbitrary[TeamStatisticsGoalsForMinute] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 50))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsGoalsForMinute(total, percentage)
  )

  implicit val arbitraryTeamStatisticsGoalsFor: Arbitrary[TeamStatisticsGoalsFor] = Arbitrary(
    for {
      total <- Gen.resultOf(TeamStatisticsGoalsForTotal.apply _)
      average <- Gen.resultOf(TeamStatisticsGoalsForAverage.apply _)
      minuteKeys <- Gen.listOf(Gen.oneOf("0-15", "16-30", "31-45", "46-60", "61-75", "76-90", "91-105", "106-120"))
      minuteValues <- Gen.listOfN(5, Gen.resultOf(TeamStatisticsGoalsForMinute.apply _))
      minuteMap = minuteKeys.zip(minuteValues).toMap
    } yield TeamStatisticsGoalsFor(total, average, minuteMap)
  )

  implicit val arbitraryTeamStatisticsGoalsAgainstTotal: Arbitrary[TeamStatisticsGoalsAgainstTotal] = Arbitrary(
    for {
      home <- Gen.chooseNum(0, 100)
      away <- Gen.chooseNum(0, 100)
      total <- Gen.chooseNum(0, 200)
    } yield TeamStatisticsGoalsAgainstTotal(home, away, total)
  )

  implicit val arbitraryTeamStatisticsGoalsAgainstAverage: Arbitrary[TeamStatisticsGoalsAgainstAverage] = Arbitrary(
    for {
      home <- Gen.choose(0.0, 5.0).map(_.toString)
      away <- Gen.choose(0.0, 5.0).map(_.toString)
      total <- Gen.choose(0.0, 5.0).map(_.toString)
    } yield TeamStatisticsGoalsAgainstAverage(home, away, total)
  )

  implicit val arbitraryTeamStatisticsGoalsAgainstMinute: Arbitrary[TeamStatisticsGoalsAgainstMinute] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 50))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsGoalsAgainstMinute(total, percentage)
  )

  implicit val arbitraryTeamStatisticsGoalsAgainst: Arbitrary[TeamStatisticsGoalsAgainst] = Arbitrary(
    for {
      total <- Gen.resultOf(TeamStatisticsGoalsAgainstTotal.apply _)
      average <- Gen.resultOf(TeamStatisticsGoalsAgainstAverage.apply _)
      minuteKeys <- Gen.listOf(Gen.oneOf("0-15", "16-30", "31-45", "46-60", "61-75", "76-90", "91-105", "106-120"))
      minuteValues <- Gen.listOfN(5, Gen.resultOf(TeamStatisticsGoalsAgainstMinute.apply _))
      minuteMap = minuteKeys.zip(minuteValues).toMap
    } yield TeamStatisticsGoalsAgainst(total, average, minuteMap)
  )

  implicit val arbitraryTeamStatisticsGoals: Arbitrary[TeamStatisticsGoals] = Arbitrary(
    for {
      forStats <- Gen.resultOf(TeamStatisticsGoalsFor.apply _)
      against <- Gen.resultOf(TeamStatisticsGoalsAgainst.apply _)
    } yield TeamStatisticsGoals(forStats, against)
  )

  implicit val arbitraryTeamStatisticsBiggestStreak: Arbitrary[TeamStatisticsBiggestStreak] = Arbitrary(
    for {
      wins <- Gen.option(Gen.chooseNum(0, 20))
      draws <- Gen.option(Gen.chooseNum(0, 20))
      loses <- Gen.option(Gen.chooseNum(0, 20))
    } yield TeamStatisticsBiggestStreak(wins, draws, loses)
  )

  implicit val arbitraryTeamStatisticsBiggestWins: Arbitrary[TeamStatisticsBiggestWins] = Arbitrary(
    for {
      home <- Gen.option(Gen.listOfN(2, Gen.chooseNum(0, 9)).map(scores => s"${scores.head}-${scores.last}"))
      away <- Gen.option(Gen.listOfN(2, Gen.chooseNum(0, 9)).map(scores => s"${scores.head}-${scores.last}"))
    } yield TeamStatisticsBiggestWins(home, away)
  )

  implicit val arbitraryTeamStatisticsBiggestLoses: Arbitrary[TeamStatisticsBiggestLoses] = Arbitrary(
    for {
      home <- Gen.option(Gen.listOfN(2, Gen.chooseNum(0, 9)).map(scores => s"${scores.head}-${scores.last}"))
      away <- Gen.option(Gen.listOfN(2, Gen.chooseNum(0, 9)).map(scores => s"${scores.head}-${scores.last}"))
    } yield TeamStatisticsBiggestLoses(home, away)
  )

  implicit val arbitraryTeamStatisticsBiggestGoalsFor: Arbitrary[TeamStatisticsBiggestGoalsFor] = Arbitrary(
    for {
      home <- Gen.option(Gen.chooseNum(0, 10))
      away <- Gen.option(Gen.chooseNum(0, 10))
    } yield TeamStatisticsBiggestGoalsFor(home, away)
  )

  implicit val arbitraryTeamStatisticsBiggestGoalsAgainst: Arbitrary[TeamStatisticsBiggestGoalsAgainst] = Arbitrary(
    for {
      home <- Gen.option(Gen.chooseNum(0, 10))
      away <- Gen.option(Gen.chooseNum(0, 10))
    } yield TeamStatisticsBiggestGoalsAgainst(home, away)
  )

  implicit val arbitraryTeamStatisticsBiggestGoals: Arbitrary[TeamStatisticsBiggestGoals] = Arbitrary(
    for {
      forStats <- Gen.resultOf(TeamStatisticsBiggestGoalsFor.apply _)
      against <- Gen.resultOf(TeamStatisticsBiggestGoalsAgainst.apply _)
    } yield TeamStatisticsBiggestGoals(forStats, against)
  )

  implicit val arbitraryTeamStatisticsBiggest: Arbitrary[TeamStatisticsBiggest] = Arbitrary(
    for {
      streak <- Gen.resultOf(TeamStatisticsBiggestStreak.apply _)
      wins <- Gen.resultOf(TeamStatisticsBiggestWins.apply _)
      loses <- Gen.resultOf(TeamStatisticsBiggestLoses.apply _)
      goals <- Gen.resultOf(TeamStatisticsBiggestGoals.apply _)
    } yield TeamStatisticsBiggest(streak, wins, loses, goals)
  )

  implicit val arbitraryTeamStatisticsCleanSheet: Arbitrary[TeamStatisticsCleanSheet] = Arbitrary(
    for {
      home <- Gen.option(Gen.chooseNum(0, 20))
      away <- Gen.option(Gen.chooseNum(0, 20))
      total <- Gen.option(Gen.chooseNum(0, 40))
    } yield TeamStatisticsCleanSheet(home, away, total)
  )

  implicit val arbitraryTeamStatisticsFailedToScore: Arbitrary[TeamStatisticsFailedToScore] = Arbitrary(
    for {
      home <- Gen.option(Gen.chooseNum(0, 20))
      away <- Gen.option(Gen.chooseNum(0, 20))
      total <- Gen.option(Gen.chooseNum(0, 40))
    } yield TeamStatisticsFailedToScore(home, away, total)
  )

  implicit val arbitraryTeamStatisticsPenaltyScored: Arbitrary[TeamStatisticsPenaltyScored] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 20))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsPenaltyScored(total, percentage)
  )

  implicit val arbitraryTeamStatisticsPenaltyMissed: Arbitrary[TeamStatisticsPenaltyMissed] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 20))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsPenaltyMissed(total, percentage)
  )

  implicit val arbitraryTeamStatisticsPenalty: Arbitrary[TeamStatisticsPenalty] = Arbitrary(
    for {
      scored <- Gen.resultOf(TeamStatisticsPenaltyScored.apply _)
      missed <- Gen.resultOf(TeamStatisticsPenaltyMissed.apply _)
      total <- Gen.option(Gen.chooseNum(0, 40))
    } yield TeamStatisticsPenalty(scored, missed, total)
  )

  implicit val arbitraryTeamStatisticsLineup: Arbitrary[TeamStatisticsLineup] = Arbitrary(
    for {
      formation <- Gen.option(Gen.oneOf("4-4-2", "4-3-3", "3-5-2", "5-3-2", "4-2-3-1"))
      played <- Gen.option(Gen.chooseNum(0, 40))
    } yield TeamStatisticsLineup(formation, played)
  )

  implicit val arbitraryTeamStatisticsCardsYellow: Arbitrary[TeamStatisticsCardsYellow] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 100))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsCardsYellow(total, percentage)
  )

  implicit val arbitraryTeamStatisticsCardsRed: Arbitrary[TeamStatisticsCardsRed] = Arbitrary(
    for {
      total <- Gen.option(Gen.chooseNum(0, 20))
      percentage <- Gen.option(Gen.choose(0.0, 100.0).map(p => s"${p}%"))
    } yield TeamStatisticsCardsRed(total, percentage)
  )

  implicit val arbitraryTeamStatisticsCards: Arbitrary[TeamStatisticsCards] = Arbitrary(for {
    yellowKeys <- Gen.listOf(Gen.oneOf("0-15", "16-30", "31-45", "46-60", "61-75", "76-90", "91-105", "106-120"))
    yellowValues <- Gen.listOfN(5, Gen.resultOf(TeamStatisticsCardsYellow.apply _))
    yellowMap = yellowKeys.zip(yellowValues).toMap

    redKeys <- Gen.listOf(Gen.oneOf("0-15", "16-30", "31-45", "46-60", "61-75", "76-90", "91-105", "106-120"))
    redValues <- Gen.listOfN(5, Gen.resultOf(TeamStatisticsCardsRed.apply _))
    redMap = redKeys.zip(redValues).toMap
  } yield TeamStatisticsCards(yellowMap, redMap))

  implicit val arbitraryTeamStatisticsResponse: Arbitrary[TeamStatisticsResponse] = Arbitrary(
    for {
      league <- Gen.resultOf(TeamStatisticsLeague.apply _)
      team <- Gen.resultOf(TeamStatisticsTeam.apply _)
      form <- Gen.option(Gen.alphaStr)
      fixtures <- Gen.resultOf(TeamStatisticsFixtures.apply _)
      goals <- Gen.resultOf(TeamStatisticsGoals.apply _)
      biggest <- Gen.resultOf(TeamStatisticsBiggest.apply _)
      cleanSheet <- Gen.resultOf(TeamStatisticsCleanSheet.apply _)
      failedToScore <- Gen.resultOf(TeamStatisticsFailedToScore.apply _)
      penalty <- Gen.resultOf(TeamStatisticsPenalty.apply _)
      lineups <- Gen.listOfN(5, Gen.resultOf(TeamStatisticsLineup.apply _))
      cards <- Gen.resultOf(TeamStatisticsCards.apply _)
    } yield TeamStatisticsResponse(
      league,
      team,
      form,
      fixtures,
      goals,
      biggest,
      cleanSheet,
      failedToScore,
      penalty,
      lineups,
      cards
    )
  )
}

object PropertyBasedTestingInstances {

  implicit def seed: Seed = Seed.random()
  implicit def generate[A](implicit arb: Arbitrary[A]): A =
    arb.arbitrary.pureApply(Gen.Parameters.default, seed)
  implicit def generateN[A](n: Int)(implicit arb: Arbitrary[A]): List[A] =
    Gen.listOfN(n, arb.arbitrary).pureApply(Gen.Parameters.default, seed)
}
