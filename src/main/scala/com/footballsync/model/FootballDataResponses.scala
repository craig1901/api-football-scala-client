package com.footballsync.model

import cats.syntax.all._
import com.footballsync.model.Fixtures.Fixture._
import com.footballsync.model.Fixtures.FixtureEvent._
import com.footballsync.model.Fixtures.FixtureLeague._
import com.footballsync.model.Fixtures.FixtureLineup.FixtureLineup
import com.footballsync.model.Fixtures.FixturePlayers._
import com.footballsync.model.Fixtures.FixtureScore._
import com.footballsync.model.Fixtures.FixtureStatistics._
import com.footballsync.model.Leagues.League
import com.footballsync.model.Leagues.LeagueCountry
import com.footballsync.model.Leagues.LeagueSeason
import com.footballsync.model.Players._
import com.footballsync.model.TeamStatistics.TeamStatisticsBiggest
import com.footballsync.model.TeamStatistics.TeamStatisticsCards
import com.footballsync.model.TeamStatistics.TeamStatisticsCleanSheet
import com.footballsync.model.TeamStatistics.TeamStatisticsFailedToScore
import com.footballsync.model.TeamStatistics.TeamStatisticsFixtures
import com.footballsync.model.TeamStatistics.TeamStatisticsGoals
import com.footballsync.model.TeamStatistics.TeamStatisticsLeague
import com.footballsync.model.TeamStatistics.TeamStatisticsLineup
import com.footballsync.model.TeamStatistics.TeamStatisticsPenalty
import com.footballsync.model.TeamStatistics.TeamStatisticsTeam
import com.footballsync.model.Teams
import com.footballsync.model.model.eitherDecoder
import com.footballsync.model.model.eitherEncoder
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

object FootballDataResponses {
  sealed trait FootballDataResponse

  case class SingleFixtureResponse(
    fixture: Fixture,
    league: FixtureLeague,
    teams: FixtureTeams,
    goals: FixtureGoals,
    score: FixtureScore,
    events: List[FixtureEvent],
    lineups: List[FixtureLineup],
    statistics: List[TeamStatistic],
    players: List[FixturePlayers]
  ) extends FootballDataResponse

  implicit val singleFixtureResponseDecoder: Decoder[SingleFixtureResponse] = deriveDecoder
  implicit val singleFixtureResponseEncoder: Encoder[SingleFixtureResponse] = deriveEncoder

  case class MultiFixtureResponse(
    fixture: Fixture,
    league: FixtureLeague,
    teams: FixtureTeams,
    goals: FixtureGoals,
    score: FixtureScore
  ) extends FootballDataResponse

  implicit val multiFixtureResponseDecoder: Decoder[MultiFixtureResponse] = deriveDecoder
  implicit val multiFixtureResponseEncoder: Encoder[MultiFixtureResponse] = deriveEncoder

  case class FixtureEventsResponse(
    time: EventTime,
    team: EventTeam,
    player: EventPlayer,
    assist: EventAssist,
    `type`: Option[String],
    detail: Option[String],
    comments: Option[String]
  ) extends FootballDataResponse

  implicit val fixtureEventResponseDecoder: Decoder[FixtureEventsResponse] = deriveDecoder
  implicit val fixtureEventResponseEncoder: Encoder[FixtureEventsResponse] = deriveEncoder

  case class TeamResponse(
    team: Teams.Team,
    venue: Teams.Venue
  ) extends FootballDataResponse

  implicit val teamResponseDecoder: Decoder[TeamResponse] = deriveDecoder
  implicit val teamResponseEncoder: Encoder[TeamResponse] = deriveEncoder

  case class LeagueResponse(
    country: LeagueCountry,
    league: League,
    seasons: List[LeagueSeason]
  ) extends FootballDataResponse

  implicit val leagueResponseDecoder: Decoder[LeagueResponse] = deriveDecoder
  implicit val leagueResponseEncoder: Encoder[LeagueResponse] = deriveEncoder

  final case class PlayerStatisticsResponse(
    player: Player,
    statistics: List[PlayerStatistic]
  ) extends FootballDataResponse

  implicit val playerStatisticsResponseDecoder: Decoder[PlayerStatisticsResponse] = deriveDecoder
  implicit val playerStatisticsResponseEncoder: Encoder[PlayerStatisticsResponse] = deriveEncoder

  case class TeamStatisticsResponse(
    league: TeamStatisticsLeague,
    team: TeamStatisticsTeam,
    form: Option[String],
    fixtures: TeamStatisticsFixtures,
    goals: TeamStatisticsGoals,
    biggest: TeamStatisticsBiggest,
    clean_sheet: TeamStatisticsCleanSheet,
    failed_to_score: TeamStatisticsFailedToScore,
    penalty: TeamStatisticsPenalty,
    lineups: List[TeamStatisticsLineup],
    cards: TeamStatisticsCards
  ) extends FootballDataResponse

  implicit val teamStatisticsResponseDecoder: Decoder[TeamStatisticsResponse] = deriveDecoder
  implicit val teamStatisticsResponseEncoder: Encoder[TeamStatisticsResponse] = deriveEncoder

  case class TimezoneResponse(value: String) extends FootballDataResponse
  case class SeasonResponse(year: Int) extends FootballDataResponse
  case class CountryResponse(name: String, code: Option[String], flag: Option[String]) extends FootballDataResponse
  case class RoundResponse(round: String) extends FootballDataResponse
  case class FixtureStatisticsResponse(inner: TeamStatistic) extends FootballDataResponse
  case class FixtureLineupsResponse(inner: FixtureLineup) extends FootballDataResponse
  case class FixturePlayerStatisticsResponse(inner: FixturePlayers) extends FootballDataResponse

  case class SquadPlayer(
    id: Int,
    name: String,
    age: Option[Int],
    number: Option[Int],
    position: Option[String],
    photo: Option[String]
  )
  case class SquadResponse(
    team: TeamInfo,
    players: List[SquadPlayer]
  ) extends FootballDataResponse

  case class PlayerTeamPlayer(id: Int, name: String)
  case class PlayerTeamsResponse(
    team: TeamInfo,
    player: PlayerTeamPlayer,
    seasons: List[Int]
  ) extends FootballDataResponse

  case class VenueResponse(
    id: Int,
    name: String,
    address: Option[String],
    city: Option[String],
    country: Option[String],
    capacity: Option[Int],
    surface: Option[String],
    image: Option[String]
  ) extends FootballDataResponse

  case class StandingStats(played: Int, win: Int, draw: Int, lose: Int, goals: Goals)
  case class StandingRow(
    rank: Int,
    team: TeamInfo,
    points: Int,
    goalsDiff: Int,
    group: String,
    form: String,
    status: String,
    description: Option[String],
    all: StandingStats,
    home: StandingStats,
    away: StandingStats,
    update: String
  )
  case class StandingLeague(
    id: Int,
    name: String,
    country: String,
    logo: String,
    flag: String,
    season: Int,
    standings: List[List[StandingRow]]
  )
  case class StandingResponse(league: StandingLeague) extends FootballDataResponse

  case class CoachCareer(team: TeamInfo, start: String, end: Option[String])
  case class CoachResponse(
    id: Int,
    name: String,
    firstname: Option[String],
    lastname: Option[String],
    age: Option[Int],
    birth: Option[PlayerBirth],
    nationality: Option[String],
    height: Option[String],
    weight: Option[String],
    photo: Option[String],
    team: Option[TeamInfo],
    career: List[CoachCareer]
  ) extends FootballDataResponse

  case class TransferTeam(id: Option[Int], name: String, logo: String)
  case class TransferTeams(in: TransferTeam, out: TransferTeam)
  case class TransferDetail(date: String, `type`: Option[String], teams: TransferTeams)
  case class TransferResponse(
    player: FixturePlayerInfo,
    update: String,
    transfers: List[TransferDetail]
  ) extends FootballDataResponse

  case class TrophyResponse(
    league: String,
    country: String,
    season: String,
    place: String,
    winner: String
  ) extends FootballDataResponse

  case class SidelinedResponse(
    `type`: String,
    start: String,
    end: Option[String],
    illness: String
  ) extends FootballDataResponse

  case class InjuryFixture(id: Int, timezone: String, date: String, timestamp: Long)
  case class InjuryLeague(id: Int, season: Int, name: String, country: String, logo: String, flag: String)
  case class InjuryResponse(
    player: FixturePlayerInfo,
    team: TeamInfo,
    fixture: Option[InjuryFixture],
    league: Option[InjuryLeague]
  ) extends FootballDataResponse

  case class PredictionValues(
    winner: Option[TeamInfo],
    win_or_draw: Boolean,
    under_over: Option[String],
    goals: Option[Goals],
    advice: Option[String],
    percent: Option[Map[String, String]]
  )
  case class PredictionResponse(
    predictions: PredictionValues,
    league: Option[LeagueInfo],
    teams: Option[FixtureTeams],
    h2h: Option[List[Fixture]]
  ) extends FootballDataResponse

  case class Paging(current: Int, total: Int)
  case class ApiResponse[T <: FootballDataResponse](
    get: String,
    parameters: Either[List[Map[String, String]], Map[String, String]],
    errors: Either[List[Map[String, String]], Map[String, String]],
    results: Int,
    paging: Paging,
    response: List[T]
  )

  implicit val timezoneResponseDecoder: Decoder[TimezoneResponse] = Decoder.decodeString.map(TimezoneResponse(_))
  implicit val timezoneResponseEncoder: Encoder[TimezoneResponse] = Encoder.encodeString.contramap(_.value)

  implicit val seasonResponseDecoder: Decoder[SeasonResponse] = Decoder.decodeInt.map(SeasonResponse(_))
  implicit val seasonResponseEncoder: Encoder[SeasonResponse] = Encoder.encodeInt.contramap(_.year)

  implicit val countryResponseDecoder: Decoder[CountryResponse] = deriveDecoder
  implicit val countryResponseEncoder: Encoder[CountryResponse] = deriveEncoder

  implicit val roundResponseDecoder: Decoder[RoundResponse] = Decoder.decodeString.map(RoundResponse(_))
  implicit val roundResponseEncoder: Encoder[RoundResponse] = Encoder.encodeString.contramap(_.round)

  implicit val fixtureStatisticsResponseDecoder: Decoder[FixtureStatisticsResponse] = Decoder[TeamStatistic].map(FixtureStatisticsResponse(_))
  implicit val fixtureStatisticsResponseEncoder: Encoder[FixtureStatisticsResponse] = Encoder[TeamStatistic].contramap(_.inner)

  implicit val fixtureLineupsResponseDecoder: Decoder[FixtureLineupsResponse] = Decoder[FixtureLineup].map(FixtureLineupsResponse(_))
  implicit val fixtureLineupsResponseEncoder: Encoder[FixtureLineupsResponse] = Encoder[FixtureLineup].contramap(_.inner)

  implicit val fixturePlayerStatisticsResponseDecoder: Decoder[FixturePlayerStatisticsResponse] = Decoder[FixturePlayers].map(FixturePlayerStatisticsResponse(_))
  implicit val fixturePlayerStatisticsResponseEncoder: Encoder[FixturePlayerStatisticsResponse] = Encoder[FixturePlayers].contramap(_.inner)

  implicit val squadPlayerCodec: Codec[SquadPlayer] = deriveCodec
  implicit val squadResponseDecoder: Decoder[SquadResponse] = deriveDecoder
  implicit val squadResponseEncoder: Encoder[SquadResponse] = deriveEncoder

  implicit val playerTeamPlayerCodec: Codec[PlayerTeamPlayer] = deriveCodec
  implicit val playerTeamsResponseDecoder: Decoder[PlayerTeamsResponse] = deriveDecoder
  implicit val playerTeamsResponseEncoder: Encoder[PlayerTeamsResponse] = deriveEncoder

  implicit val venueResponseDecoder: Decoder[VenueResponse] = deriveDecoder
  implicit val venueResponseEncoder: Encoder[VenueResponse] = deriveEncoder

  implicit val standingStatsCodec: Codec[StandingStats] = deriveCodec
  implicit val standingRowCodec: Codec[StandingRow] = deriveCodec
  implicit val standingLeagueCodec: Codec[StandingLeague] = deriveCodec
  implicit val standingResponseDecoder: Decoder[StandingResponse] = deriveDecoder
  implicit val standingResponseEncoder: Encoder[StandingResponse] = deriveEncoder

  implicit val coachCareerCodec: Codec[CoachCareer] = deriveCodec
  implicit val coachResponseDecoder: Decoder[CoachResponse] = deriveDecoder
  implicit val coachResponseEncoder: Encoder[CoachResponse] = deriveEncoder

  implicit val transferTeamCodec: Codec[TransferTeam] = deriveCodec
  implicit val transferTeamsCodec: Codec[TransferTeams] = deriveCodec
  implicit val transferDetailCodec: Codec[TransferDetail] = deriveCodec
  implicit val transferResponseDecoder: Decoder[TransferResponse] = deriveDecoder
  implicit val transferResponseEncoder: Encoder[TransferResponse] = deriveEncoder

  implicit val trophyResponseDecoder: Decoder[TrophyResponse] = deriveDecoder
  implicit val trophyResponseEncoder: Encoder[TrophyResponse] = deriveEncoder

  implicit val sidelinedResponseDecoder: Decoder[SidelinedResponse] = deriveDecoder
  implicit val sidelinedResponseEncoder: Encoder[SidelinedResponse] = deriveEncoder

  implicit val injuryFixtureCodec: Codec[InjuryFixture] = deriveCodec
  implicit val injuryLeagueCodec: Codec[InjuryLeague] = deriveCodec
  implicit val injuryResponseDecoder: Decoder[InjuryResponse] = deriveDecoder
  implicit val injuryResponseEncoder: Encoder[InjuryResponse] = deriveEncoder

  implicit val predictionValuesCodec: Codec[PredictionValues] = deriveCodec
  implicit val predictionResponseDecoder: Decoder[PredictionResponse] = deriveDecoder
  implicit val predictionResponseEncoder: Encoder[PredictionResponse] = deriveEncoder

  implicit val pagingDecoder: Decoder[Paging] = deriveDecoder
  implicit val pagingEncoder: Encoder[Paging] = deriveEncoder

  implicit val footballDataResponseDecoder: Decoder[FootballDataResponse] =
    List[Decoder[FootballDataResponse]](
      singleFixtureResponseDecoder.widen,
      multiFixtureResponseDecoder.widen,
      fixtureEventResponseDecoder.widen,
      teamResponseDecoder.widen,
      leagueResponseDecoder.widen,
      playerStatisticsResponseDecoder.widen,
      teamStatisticsResponseDecoder.widen,
      timezoneResponseDecoder.widen,
      seasonResponseDecoder.widen,
      countryResponseDecoder.widen,
      roundResponseDecoder.widen,
      fixtureStatisticsResponseDecoder.widen,
      fixtureLineupsResponseDecoder.widen,
      fixturePlayerStatisticsResponseDecoder.widen,
      squadResponseDecoder.widen,
      playerTeamsResponseDecoder.widen,
      venueResponseDecoder.widen,
      standingResponseDecoder.widen,
      coachResponseDecoder.widen,
      transferResponseDecoder.widen,
      trophyResponseDecoder.widen,
      sidelinedResponseDecoder.widen,
      injuryResponseDecoder.widen,
      predictionResponseDecoder.widen
    ).reduceLeft(_ or _)

  implicit val footballDataResponseEncoder: Encoder[FootballDataResponse] = (a: FootballDataResponse) =>
    a match {
      case singleFixtureResponse: SingleFixtureResponse   => singleFixtureResponse.asJson
      case multiFixtureResponse: MultiFixtureResponse     => multiFixtureResponse.asJson
      case fixtureEventsResponse: FixtureEventsResponse   => fixtureEventsResponse.asJson
      case teamResponse: TeamResponse                     => teamResponse.asJson
      case leagueResponse: LeagueResponse                 => leagueResponse.asJson
      case teamStatisticsResponse: TeamStatisticsResponse => teamStatisticsResponse.asJson
      case playersResponse: PlayerStatisticsResponse      => playersResponse.asJson
      case timezoneResponse: TimezoneResponse             => timezoneResponse.asJson
      case seasonResponse: SeasonResponse                 => seasonResponse.asJson
      case countryResponse: CountryResponse               => countryResponse.asJson
      case roundResponse: RoundResponse                   => roundResponse.asJson
      case fixtureStatisticsResponse: FixtureStatisticsResponse => fixtureStatisticsResponse.asJson
      case fixtureLineupsResponse: FixtureLineupsResponse       => fixtureLineupsResponse.asJson
      case fixturePlayerStatisticsResponse: FixturePlayerStatisticsResponse => fixturePlayerStatisticsResponse.asJson
      case squadResponse: SquadResponse                   => squadResponse.asJson
      case playerTeamsResponse: PlayerTeamsResponse       => playerTeamsResponse.asJson
      case venueResponse: VenueResponse                   => venueResponse.asJson
      case standingResponse: StandingResponse             => standingResponse.asJson
      case coachResponse: CoachResponse                   => coachResponse.asJson
      case transferResponse: TransferResponse             => transferResponse.asJson
      case trophyResponse: TrophyResponse                 => trophyResponse.asJson
      case sidelinedResponse: SidelinedResponse           => sidelinedResponse.asJson
      case injuryResponse: InjuryResponse                 => injuryResponse.asJson
      case predictionResponse: PredictionResponse         => predictionResponse.asJson
    }

  implicit val footballDataResponseCodec: Codec[FootballDataResponse] =
    Codec.from(footballDataResponseDecoder, footballDataResponseEncoder)

  implicit def apiResponseEncoder[T <: FootballDataResponse: Encoder]: Encoder[ApiResponse[T]] =
    deriveEncoder[ApiResponse[T]]

  implicit def apiResponseDecoder[T <: FootballDataResponse: Decoder]: Decoder[ApiResponse[T]] =
    cursor => {
      val flexibleResponseDecoder = Decoder.decodeList[T].or(Decoder[T].map(List(_)))
      for {
        get <- cursor.downField("get").as[String]
        parameters <- cursor.downField("parameters").as[Either[List[Map[String, String]], Map[String, String]]]
        errors <- cursor.downField("errors").as[Either[List[Map[String, String]], Map[String, String]]]
        results <- cursor.downField("results").as[Int]
        paging <- cursor.downField("paging").as[Paging]
        response <- cursor.downField("response").as[List[T]](flexibleResponseDecoder)
      } yield ApiResponse(get, parameters, errors, results, paging, response)
    }

}
