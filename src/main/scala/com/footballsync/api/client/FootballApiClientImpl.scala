package com.footballsync.api.client

import cats.Applicative
import cats.effect._
import cats.syntax.all._
import com.footballsync.model.FootballDataResponses._
import com.footballsync.util.randomDelay
import fs2.Stream
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client._
import org.http4s.implicits._
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import retry.RetryPolicies._
import retry._

import scala.concurrent.duration._

import FootballApiClientImpl._

class FootballApiClientImpl[F[_]: Async](client: Client[F], apiKey: String) extends FootballApiClient[F] {

  implicit private val logger: Logger[F] = Slf4jLogger.getLoggerFromName[F](getClass.getSimpleName)

  private val baseUrl: Uri = uri"https://v3.football.api-sports.io"
  private val host: String = "v3.football.api-sports.io"

  private def headers: Headers = Headers(
    Header("X-RapidAPI-Key", apiKey),
    Header("X-RapidAPI-Host", host)
  )

  private def makeRequest[T <: FootballDataResponse](endpoint: String, params: Map[String, String])(implicit
    d: EntityDecoder[F, ApiResponse[T]]
  ): F[ApiResponse[T]] = {

    val requestUri: Uri = baseUrl.withPath(endpoint).withQueryParams(params)
    val request: Request[F] = Request[F](
      method = Method.GET,
      uri = requestUri,
      headers = headers
    )

    retryingOnAllErrors[ApiResponse[T]](
      retryPolicy[F], // Use the retryPolicy for F
      onError = (e: Throwable, retryDetails: RetryDetails) => {
        logger.warn(s"Retrying due to error: ${e.getMessage}. Retry #${retryDetails.retriesSoFar}")
      }
    )(
      logger.info(s"Making request to $requestUri") *> client
        .run(request)
        .use(processResponse[F, T])
    )
  }

  private def paginatedRequest[T <: FootballDataResponse](endpoint: String, params: Map[String, String])(implicit
    d: EntityDecoder[F, ApiResponse[T]]
  ): Stream[F, ApiResponse[T]] = {

    def fetchAllPages(page: Int): Stream[F, ApiResponse[T]] = {
      val request = randomDelay(133.millis, 25.millis).flatMap { delay =>
        logger.info(s"Sleeping for $delay for page: $page") >> Temporal[F].delayBy(
          makeRequest(endpoint, params + ("page" -> page.toString)),
          delay
        )
      }

      Stream.eval(request).flatMap { response =>
        if (response.paging.total > page) {
          Stream.emit(response) ++ fetchAllPages(page + 1)
        } else {
          Stream.emit(response)
        }
      }
    }

    fetchAllPages(1)
  }

  override def fetchSingleFixture(fixtureId: String): F[ApiResponse[SingleFixtureResponse]] = {
    val queryParams = Map("id" -> fixtureId)
    makeRequest[SingleFixtureResponse](fixturesPath, queryParams)
  }

  def fetchSingleFixtures(fixtureIds: List[String]): F[ApiResponse[SingleFixtureResponse]] = {
    val queryParams = Map("ids" -> fixtureIds.mkString("-"))
    makeRequest[SingleFixtureResponse](fixturesPath, queryParams)
  }

  override def fetchFixtures(leagueId: String, season: String): F[ApiResponse[MultiFixtureResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[MultiFixtureResponse](fixturesPath, queryParams)
  }

  override def fetchFixtureEvents(fixtureId: String): F[ApiResponse[FixtureEventsResponse]] = {
    val queryParams = Map("fixture" -> fixtureId)
    makeRequest[FixtureEventsResponse](fixtureEventsPath, queryParams)
  }

  override def fetchTeams(leagueId: String, season: String): F[ApiResponse[TeamResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[TeamResponse](teamsPath, queryParams)
  }

  override def fetchTeamsByCountry(country: String): F[ApiResponse[TeamResponse]] = {
    val queryParams = Map("country" -> country)
    makeRequest[TeamResponse](teamsPath, queryParams)
  }

  override def fetchTeam(teamId: String): F[ApiResponse[TeamResponse]] = {
    val queryParams = Map("id" -> teamId)
    makeRequest[TeamResponse](teamsPath, queryParams)
  }

  override def fetchAllLeagues(): F[ApiResponse[LeagueResponse]] = {
    makeRequest[LeagueResponse](leaguesPath, Map.empty)
  }

  override def fetchPlayersStream(
    leagueId: String,
    season: String
  ): fs2.Stream[F, ApiResponse[PlayerStatisticsResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    paginatedRequest[PlayerStatisticsResponse](playersPath, queryParams)
  }

  override def getTeamStatistics(
    teamId: String,
    leagueId: String,
    season: String
  ): F[ApiResponse[TeamStatisticsResponse]] = {
    val queryParams = Map("team" -> teamId, "league" -> leagueId, "season" -> season)
    makeRequest[TeamStatisticsResponse](teamStatisticsPath, queryParams)
  }

  override def fetchTimezones(): F[ApiResponse[TimezoneResponse]] = {
    makeRequest[TimezoneResponse](timezonePath, Map.empty)
  }

  override def fetchCountries(): F[ApiResponse[CountryResponse]] = {
    makeRequest[CountryResponse](countriesPath, Map.empty)
  }

  override def fetchSeasons(): F[ApiResponse[SeasonResponse]] = {
    makeRequest[SeasonResponse](seasonsPath, Map.empty)
  }

  override def fetchTeamSeasons(teamId: String): F[ApiResponse[SeasonResponse]] = {
    val queryParams = Map("team" -> teamId)
    makeRequest[SeasonResponse](teamSeasonsPath, queryParams)
  }

  override def fetchTeamCountries(): F[ApiResponse[CountryResponse]] = {
    makeRequest[CountryResponse](teamCountriesPath, Map.empty)
  }

  override def fetchFixtureRounds(leagueId: String, season: String): F[ApiResponse[RoundResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[RoundResponse](roundsPath, queryParams)
  }

  override def fetchHeadToHead(h2h: String): F[ApiResponse[MultiFixtureResponse]] = {
    val queryParams = Map("h2h" -> h2h)
    makeRequest[MultiFixtureResponse](headToHeadPath, queryParams)
  }

  override def fetchFixtureStatistics(fixtureId: String): F[ApiResponse[FixtureStatisticsResponse]] = {
    val queryParams = Map("fixture" -> fixtureId)
    makeRequest[FixtureStatisticsResponse](fixtureStatisticsPath, queryParams)
  }

  override def fetchFixtureLineups(fixtureId: String): F[ApiResponse[FixtureLineupsResponse]] = {
    val queryParams = Map("fixture" -> fixtureId)
    makeRequest[FixtureLineupsResponse](fixtureLineupsPath, queryParams)
  }

  override def fetchFixturePlayerStatistics(fixtureId: String): F[ApiResponse[FixturePlayerStatisticsResponse]] = {
    val queryParams = Map("fixture" -> fixtureId)
    makeRequest[FixturePlayerStatisticsResponse](fixturePlayersPath, queryParams)
  }

  override def fetchPlayerSeasons(playerId: Option[String]): F[ApiResponse[SeasonResponse]] = {
    val queryParams = playerId.map(id => Map("player" -> id)).getOrElse(Map.empty)
    makeRequest[SeasonResponse](playerSeasonsPath, queryParams)
  }

  override def fetchSquad(teamId: String): F[ApiResponse[SquadResponse]] = {
    val queryParams = Map("team" -> teamId)
    makeRequest[SquadResponse](squadsPath, queryParams)
  }

  override def fetchPlayerTeams(playerId: String): F[ApiResponse[PlayerTeamsResponse]] = {
    val queryParams = Map("player" -> playerId)
    makeRequest[PlayerTeamsResponse](playerTeamsPath, queryParams)
  }

  override def fetchTopScorers(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[PlayerStatisticsResponse](topScorersPath, queryParams)
  }

  override def fetchTopAssists(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[PlayerStatisticsResponse](topAssistsPath, queryParams)
  }

  override def fetchTopYellowCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[PlayerStatisticsResponse](topYellowCardsPath, queryParams)
  }

  override def fetchTopRedCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[PlayerStatisticsResponse](topRedCardsPath, queryParams)
  }

  override def fetchVenues(
    name: Option[String],
    city: Option[String],
    country: Option[String],
    id: Option[String]
  ): F[ApiResponse[VenueResponse]] = {
    val queryParams = Map(
      "name" -> name,
      "city" -> city,
      "country" -> country,
      "id" -> id
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[VenueResponse](venuesPath, queryParams)
  }

  override def fetchStandings(leagueId: String, season: String): F[ApiResponse[StandingResponse]] = {
    val queryParams = Map("league" -> leagueId, "season" -> season)
    makeRequest[StandingResponse](standingsPath, queryParams)
  }

  override def fetchCoaches(
    teamId: Option[String],
    coachId: Option[String]
  ): F[ApiResponse[CoachResponse]] = {
    val queryParams = Map(
      "team" -> teamId,
      "id" -> coachId
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[CoachResponse](coachesPath, queryParams)
  }

  override def fetchTransfers(
    playerId: Option[String],
    teamId: Option[String]
  ): F[ApiResponse[TransferResponse]] = {
    val queryParams = Map(
      "player" -> playerId,
      "team" -> teamId
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[TransferResponse](transfersPath, queryParams)
  }

  override def fetchTrophies(
    playerId: Option[String],
    coachId: Option[String]
  ): F[ApiResponse[TrophyResponse]] = {
    val queryParams = Map(
      "player" -> playerId,
      "coach" -> coachId
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[TrophyResponse](trophiesPath, queryParams)
  }

  override def fetchSidelined(
    playerId: Option[String],
    coachId: Option[String]
  ): F[ApiResponse[SidelinedResponse]] = {
    val queryParams = Map(
      "player" -> playerId,
      "coach" -> coachId
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[SidelinedResponse](sidelinedPath, queryParams)
  }

  override def fetchInjuries(
    leagueId: String,
    season: String,
    teamId: Option[String]
  ): F[ApiResponse[InjuryResponse]] = {
    val queryParams = Map(
      "league" -> Some(leagueId),
      "season" -> Some(season),
      "team" -> teamId
    ).collect { case (k, Some(v)) => k -> v }
    makeRequest[InjuryResponse](injuriesPath, queryParams)
  }

  override def fetchPredictions(fixtureId: String): F[ApiResponse[PredictionResponse]] = {
    val queryParams = Map("fixture" -> fixtureId)
    makeRequest[PredictionResponse](predictionsPath, queryParams)
  }
}

object FootballApiClientImpl {
  val fixturesPath: String = "fixtures"
  val fixtureEventsPath: String = "fixtures/events"
  val teamsPath: String = "teams"
  val leaguesPath: String = "leagues"
  val playersPath: String = "players"
  val teamStatisticsPath: String = "teams/statistics"
  val timezonePath: String = "timezone"
  val countriesPath: String = "countries"
  val seasonsPath: String = "leagues/seasons"
  val teamSeasonsPath: String = "teams/seasons"
  val teamCountriesPath: String = "teams/countries"
  val roundsPath: String = "fixtures/rounds"
  val headToHeadPath: String = "fixtures/headtohead"
  val fixtureStatisticsPath: String = "fixtures/statistics"
  val fixtureLineupsPath: String = "fixtures/lineups"
  val fixturePlayersPath: String = "fixtures/players"
  val playerSeasonsPath: String = "players/seasons"
  val squadsPath: String = "players/squads"
  val playerTeamsPath: String = "players/teams"
  val topScorersPath: String = "players/topscorers"
  val topAssistsPath: String = "players/topassists"
  val topYellowCardsPath: String = "players/topyellowcards"
  val topRedCardsPath: String = "players/topredcards"
  val venuesPath: String = "venues"
  val standingsPath: String = "standings"
  val coachesPath: String = "coachs"
  val transfersPath: String = "transfers"
  val trophiesPath: String = "trophies"
  val sidelinedPath: String = "sidelined"
  val injuriesPath: String = "injuries"
  val predictionsPath: String = "predictions"

  protected[client] val maxRetries: Int = 5
  protected[client] val retryDelay: FiniteDuration = 2.seconds

  private def retryPolicy[F[_]: Applicative]: RetryPolicy[F] =
    exponentialBackoff(retryDelay).join(limitRetries(maxRetries))

  def handleSuccess[F[_]: Async, T <: FootballDataResponse](response: Response[F])(implicit
    d: EntityDecoder[F, ApiResponse[T]],
    logger: Logger[F]
  ): F[ApiResponse[T]] = {
    for {
      decoded <- response.as[ApiResponse[T]]
      _ <- logger.debug(s"Response received $response, body: $decoded")
    } yield decoded
  }

  def handleError[F[_]: Async, T <: FootballDataResponse](
    response: Response[F]
  )(implicit logger: Logger[F]): F[ApiResponse[T]] = {
    response.bodyText.compile.foldMonoid.flatMap { body =>
      logger.error(s"Request failed with status ${response.status.code}: $body") *>
        Async[F].raiseError(HttpRequestFailedException(response.status, body))
    }
  }

  def processResponse[F[_]: Async, T <: FootballDataResponse](response: Response[F])(implicit
    d: EntityDecoder[F, ApiResponse[T]],
    logger: Logger[F]
  ): F[ApiResponse[T]] = {
    def handleSuccessOrFailure: F[ApiResponse[T]] = response.status.responseClass match {
      case Status.Successful =>
        handleSuccess(response)
      case _ =>
        handleError(response)
    }

    checkRateLimit[F](response.headers) *> handleSuccessOrFailure
  }

  def checkRateLimit[F[_]: Async](headers: Headers)(implicit logger: Logger[F]): F[Unit] = {
    val rateLimitRemainingHeader = ci"x-ratelimit-remaining"
    headers.get(rateLimitRemainingHeader) match {
      case Some(header) =>
        val remaining = header.head.value.toInt
        if (remaining <= 0) {
          logger.warn("Rate limit exceeded, backing off...") *> Async[F].raiseError(
            new RateLimitExceededException(retryDelay)
          )
        } else Async[F].unit
      case None =>
        logger.warn(s"Header: $rateLimitRemainingHeader not found in response") *> Async[F].unit
    }
  }

  def obfuscateHeaders(headers: Headers): Headers = {
    headers.removePayloadHeaders.redactSensitive(c => c === ci"X-RapidAPI-Key")
  }

  sealed abstract class FootballApiException(message: String) extends RuntimeException(message)

  case class RateLimitExceededException(retryAfter: FiniteDuration)
      extends FootballApiException(s"Rate limit exceeded. Retry after $retryAfter seconds.")

  case class HttpRequestFailedException(status: Status, body: String)
      extends FootballApiException(s"Request failed with status ${status.code}: $body")
}
