package com.footballsync.api.client

import com.footballsync.model.FootballDataResponses._
import fs2.Stream

trait FootballApiClient[F[_]] {
  def fetchSingleFixture(fixtureId: String): F[ApiResponse[SingleFixtureResponse]]
  def fetchSingleFixtures(fixtureIds: List[String]): F[ApiResponse[SingleFixtureResponse]]
  def fetchFixtures(leagueId: String, season: String): F[ApiResponse[MultiFixtureResponse]]
  def fetchFixtureEvents(fixtureId: String): F[ApiResponse[FixtureEventsResponse]]
  def fetchTeam(teamId: String): F[ApiResponse[TeamResponse]]
  def fetchTeamsByCountry(country: String): F[ApiResponse[TeamResponse]]
  def fetchTeams(leagueId: String, season: String): F[ApiResponse[TeamResponse]]
  def fetchAllLeagues(): F[ApiResponse[LeagueResponse]]
  def fetchPlayersStream(leagueId: String, season: String): Stream[F, ApiResponse[PlayerStatisticsResponse]]
  def getTeamStatistics(teamId: String, leagueId: String, season: String): F[ApiResponse[TeamStatisticsResponse]]
  def fetchTimezones(): F[ApiResponse[TimezoneResponse]]
  def fetchCountries(): F[ApiResponse[CountryResponse]]
  def fetchSeasons(): F[ApiResponse[SeasonResponse]]
  def fetchTeamSeasons(teamId: String): F[ApiResponse[SeasonResponse]]
  def fetchTeamCountries(): F[ApiResponse[CountryResponse]]
  def fetchFixtureRounds(leagueId: String, season: String): F[ApiResponse[RoundResponse]]
  def fetchHeadToHead(h2h: String): F[ApiResponse[MultiFixtureResponse]]
  def fetchFixtureStatistics(fixtureId: String): F[ApiResponse[FixtureStatisticsResponse]]
  def fetchFixtureLineups(fixtureId: String): F[ApiResponse[FixtureLineupsResponse]]
  def fetchFixturePlayerStatistics(fixtureId: String): F[ApiResponse[FixturePlayerStatisticsResponse]]
  def fetchPlayerSeasons(playerId: Option[String] = None): F[ApiResponse[SeasonResponse]]
  def fetchSquad(teamId: String): F[ApiResponse[SquadResponse]]
  def fetchPlayerTeams(playerId: String): F[ApiResponse[PlayerTeamsResponse]]
  def fetchTopScorers(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
  def fetchTopAssists(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
  def fetchTopYellowCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
  def fetchTopRedCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
  def fetchVenues(
    name: Option[String] = None,
    city: Option[String] = None,
    country: Option[String] = None,
    id: Option[String] = None
  ): F[ApiResponse[VenueResponse]]
  def fetchStandings(leagueId: String, season: String): F[ApiResponse[StandingResponse]]
  def fetchCoaches(teamId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[CoachResponse]]
  def fetchTransfers(playerId: Option[String] = None, teamId: Option[String] = None): F[ApiResponse[TransferResponse]]
  def fetchTrophies(playerId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[TrophyResponse]]
  def fetchSidelined(playerId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[SidelinedResponse]]
  def fetchInjuries(leagueId: String, season: String, teamId: Option[String] = None): F[ApiResponse[InjuryResponse]]
  def fetchPredictions(fixtureId: String): F[ApiResponse[PredictionResponse]]
}
