# API Reference

This is a comprehensive reference of all methods available in the FootballApiClient interface.

## Methods Overview

The `FootballApiClient[F[_]]` trait provides access to all API-Football v3 endpoints. All methods return an effect type `F` (typically `IO`) containing an `ApiResponse[T]`.

### Method Categories

- [Fixtures & Matches](#fixtures--matches)
- [Teams](#teams)
- [Players](#players)
- [Leagues & Competitions](#leagues--competitions)
- [Statistics](#statistics)
- [Additional Data](#additional-data)
- [Streaming Methods](#streaming-methods)

## Fixtures & Matches

### fetchSingleFixture
```scala
def fetchSingleFixture(fixtureId: String): F[ApiResponse[SingleFixtureResponse]]
```
Fetches a single fixture by its ID.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchSingleFixture("1034627")
```

### fetchSingleFixtures
```scala
def fetchSingleFixtures(fixtureIds: List[String]): F[ApiResponse[SingleFixtureResponse]]
```
Fetches multiple fixtures by their IDs.

**Parameters:**
- `fixtureIds`: List of fixture IDs (max 20)

**Example:**
```scala
apiClient.fetchSingleFixtures(List("1034627", "1034628", "1034629"))
```

### fetchFixtures
```scala
def fetchFixtures(leagueId: String, season: String): F[ApiResponse[MultiFixtureResponse]]
```
Fetches all fixtures for a league and season.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year (e.g., "2023")

**Example:**
```scala
apiClient.fetchFixtures("39", "2023") // Premier League 2023
```

### fetchFixtureEvents
```scala
def fetchFixtureEvents(fixtureId: String): F[ApiResponse[FixtureEventsResponse]]
```
Fetches events (goals, cards, substitutions) for a fixture.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchFixtureEvents("1034627")
```

### fetchFixtureStatistics
```scala
def fetchFixtureStatistics(fixtureId: String): F[ApiResponse[FixtureStatisticsResponse]]
```
Fetches detailed statistics for a fixture.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchFixtureStatistics("1034627")
```

### fetchFixtureLineups
```scala
def fetchFixtureLineups(fixtureId: String): F[ApiResponse[FixtureLineupsResponse]]
```
Fetches lineups for a fixture.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchFixtureLineups("1034627")
```

### fetchFixturePlayerStatistics
```scala
def fetchFixturePlayerStatistics(fixtureId: String): F[ApiResponse[FixturePlayerStatisticsResponse]]
```
Fetches individual player statistics for a fixture.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchFixturePlayerStatistics("1034627")
```

### fetchHeadToHead
```scala
def fetchHeadToHead(h2h: String): F[ApiResponse[MultiFixtureResponse]]
```
Fetches head-to-head fixtures between two teams.

**Parameters:**
- `h2h`: Team IDs in format "team1-team2" or "team1-team2-league-season"

**Example:**
```scala
apiClient.fetchHeadToHead("33-34") // Man Utd vs Liverpool
```

### fetchPredictions
```scala
def fetchPredictions(fixtureId: String): F[ApiResponse[PredictionResponse]]
```
Fetches predictions for a fixture.

**Parameters:**
- `fixtureId`: The ID of the fixture

**Example:**
```scala
apiClient.fetchPredictions("1034627")
```

## Teams

### fetchTeam
```scala
def fetchTeam(teamId: String): F[ApiResponse[TeamResponse]]
```
Fetches information about a specific team.

**Parameters:**
- `teamId`: The ID of the team

**Example:**
```scala
apiClient.fetchTeam("33") // Manchester United
```

### fetchTeams
```scala
def fetchTeams(leagueId: String, season: String): F[ApiResponse[TeamResponse]]
```
Fetches all teams in a league for a season.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchTeams("39", "2023")
```

### fetchTeamsByCountry
```scala
def fetchTeamsByCountry(country: String): F[ApiResponse[TeamResponse]]
```
Fetches teams by country.

**Parameters:**
- `country`: The country name

**Example:**
```scala
apiClient.fetchTeamsByCountry("England")
```

### fetchSquad
```scala
def fetchSquad(teamId: String): F[ApiResponse[SquadResponse]]
```
Fetches the current squad of a team.

**Parameters:**
- `teamId`: The ID of the team

**Example:**
```scala
apiClient.fetchSquad("33")
```

### fetchCoaches
```scala
def fetchCoaches(teamId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[CoachResponse]]
```
Fetches coaches information.

**Parameters:**
- `teamId`: Optional team ID to get team coach
- `coachId`: Optional coach ID to get specific coach

**Example:**
```scala
apiClient.fetchCoaches(teamId = Some("33"))
apiClient.fetchCoaches(coachId = Some("123"))
```

## Players

### fetchPlayerTeams
```scala
def fetchPlayerTeams(playerId: String): F[ApiResponse[PlayerTeamsResponse]]
```
Fetches teams a player has played for.

**Parameters:**
- `playerId`: The ID of the player

**Example:**
```scala
apiClient.fetchPlayerTeams("276")
```

### fetchTransfers
```scala
def fetchTransfers(playerId: Option[String] = None, teamId: Option[String] = None): F[ApiResponse[TransferResponse]]
```
Fetches transfer information.

**Parameters:**
- `playerId`: Optional player ID for player transfers
- `teamId`: Optional team ID for team transfers

**Example:**
```scala
apiClient.fetchTransfers(playerId = Some("276"))
apiClient.fetchTransfers(teamId = Some("33"))
```

### fetchTrophies
```scala
def fetchTrophies(playerId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[TrophyResponse]]
```
Fetches trophies won by players or coaches.

**Parameters:**
- `playerId`: Optional player ID for player trophies
- `coachId`: Optional coach ID for coach trophies

**Example:**
```scala
apiClient.fetchTrophies(playerId = Some("276"))
```

### fetchSidelined
```scala
def fetchSidelined(playerId: Option[String] = None, coachId: Option[String] = None): F[ApiResponse[SidelinedResponse]]
```
Fetches sidelined information (injuries, suspensions).

**Parameters:**
- `playerId`: Optional player ID
- `coachId`: Optional coach ID

**Example:**
```scala
apiClient.fetchSidelined(playerId = Some("276"))
```

### fetchInjuries
```scala
def fetchInjuries(leagueId: String, season: String, teamId: Option[String] = None): F[ApiResponse[InjuryResponse]]
```
Fetches injury reports.

**Parameters:**
- `leagueId`: The league ID
- `season`: The season year
- `teamId`: Optional team ID to filter by team

**Example:**
```scala
apiClient.fetchInjuries("39", "2023")
apiClient.fetchInjuries("39", "2023", teamId = Some("33"))
```

## Leagues & Competitions

### fetchAllLeagues
```scala
def fetchAllLeagues(): F[ApiResponse[LeagueResponse]]
```
Fetches all available leagues.

**Example:**
```scala
apiClient.fetchAllLeagues()
```

### fetchStandings
```scala
def fetchStandings(leagueId: String, season: String): F[ApiResponse[StandingResponse]]
```
Fetches league standings/table.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchStandings("39", "2023") // Premier League table
```

### fetchSeasons
```scala
def fetchSeasons(): F[ApiResponse[SeasonResponse]]
```
Fetches all available seasons.

**Example:**
```scala
apiClient.fetchSeasons()
```

### fetchTeamSeasons
```scala
def fetchTeamSeasons(teamId: String): F[ApiResponse[SeasonResponse]]
```
Fetches seasons a team has played.

**Parameters:**
- `teamId`: The ID of the team

**Example:**
```scala
apiClient.fetchTeamSeasons("33")
```

### fetchPlayerSeasons
```scala
def fetchPlayerSeasons(playerId: Option[String] = None): F[ApiResponse[SeasonResponse]]
```
Fetches seasons for a player.

**Parameters:**
- `playerId`: Optional player ID

**Example:**
```scala
apiClient.fetchPlayerSeasons(playerId = Some("276"))
```

### fetchFixtureRounds
```scala
def fetchFixtureRounds(leagueId: String, season: String): F[ApiResponse[RoundResponse]]
```
Fetches rounds for a league/season.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchFixtureRounds("39", "2023")
```

## Statistics

### getTeamStatistics
```scala
def getTeamStatistics(teamId: String, leagueId: String, season: String): F[ApiResponse[TeamStatisticsResponse]]
```
Fetches detailed statistics for a team in a league/season.

**Parameters:**
- `teamId`: The ID of the team
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.getTeamStatistics("33", "39", "2023")
```

### fetchTopScorers
```scala
def fetchTopScorers(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
```
Fetches top scorers for a league/season.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchTopScorers("39", "2023")
```

### fetchTopAssists
```scala
def fetchTopAssists(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
```
Fetches top assists for a league/season.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchTopAssists("39", "2023")
```

### fetchTopYellowCards
```scala
def fetchTopYellowCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
```
Fetches players with most yellow cards.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchTopYellowCards("39", "2023")
```

### fetchTopRedCards
```scala
def fetchTopRedCards(leagueId: String, season: String): F[ApiResponse[PlayerStatisticsResponse]]
```
Fetches players with most red cards.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchTopRedCards("39", "2023")
```

## Additional Data

### fetchCountries
```scala
def fetchCountries(): F[ApiResponse[CountryResponse]]
```
Fetches all available countries.

**Example:**
```scala
apiClient.fetchCountries()
```

### fetchTeamCountries
```scala
def fetchTeamCountries(): F[ApiResponse[CountryResponse]]
```
Fetches countries that have teams.

**Example:**
```scala
apiClient.fetchTeamCountries()
```

### fetchVenues
```scala
def fetchVenues(
  name: Option[String] = None,
  city: Option[String] = None,
  country: Option[String] = None,
  id: Option[String] = None
): F[ApiResponse[VenueResponse]]
```
Fetches venues/stadiums.

**Parameters:**
- `name`: Optional venue name filter
- `city`: Optional city filter
- `country`: Optional country filter
- `id`: Optional venue ID

**Example:**
```scala
apiClient.fetchVenues(name = Some("Old Trafford"))
apiClient.fetchVenues(city = Some("Manchester"))
```

### fetchTimezones
```scala
def fetchTimezones(): F[ApiResponse[TimezoneResponse]]
```
Fetches all supported timezones.

**Example:**
```scala
apiClient.fetchTimezones()
```

## Streaming Methods

These methods return `fs2.Stream[F, T]` for handling large datasets with automatic pagination.

### fetchPlayersStream
```scala
def fetchPlayersStream(leagueId: String, season: String): Stream[F, ApiResponse[PlayerStatisticsResponse]]
```
Streams all players in a league/season, handling pagination automatically.

**Parameters:**
- `leagueId`: The ID of the league
- `season`: The season year

**Example:**
```scala
apiClient.fetchPlayersStream("39", "2023")
  .evalMap(response => IO.println(s"Fetched page ${response.paging.current}"))
  .compile
  .drain
```

## Response Types

All methods return `F[ApiResponse[T]]` where `T` depends on the endpoint:

### Common Response Fields

```scala
case class ApiResponse[T](
  get: String,                    // The endpoint path
  parameters: Either[List[Map[String, String]], Map[String, String]], // Request parameters
  errors: Either[List[Map[String, String]], Map[String, String]],     // API errors (empty map if none)
  results: Int,                   // Number of results
  paging: Paging,                 // Pagination info
  response: List[T]               // The actual data
)

case class Paging(
  current: Int,      // Current page
  total: Int         // Total pages
)
```

**Note:** The `errors` and `parameters` fields use `Either` to handle both list and map formats from the API. To check for errors:
```scala
response.errors match {
  case Right(errorMap) if errorMap.nonEmpty => // Handle errors
  case Left(errorList) if errorList.nonEmpty => // Handle errors
  case _ => // No errors
}
```

### Specific Response Types

- `SingleFixtureResponse`: Single fixture data
- `MultiFixtureResponse`: Multiple fixtures
- `TeamResponse`: Team information
- `PlayerStatisticsResponse`: Player stats
- `LeagueResponse`: League data
- `StandingResponse`: League table
- `TeamStatisticsResponse`: Detailed team stats
- `FixtureEventsResponse`: Match events
- `FixtureLineupsResponse`: Team lineups
- `FixtureStatisticsResponse`: Match statistics
- `VenueResponse`: Stadium/venue information
- `CountryResponse`: Country data
- `SeasonResponse`: Season information
- `And more...`

## Error Handling

Always check for errors in responses:

```scala
apiClient.fetchFixtures("39", "2023").flatMap { response =>
  val hasErrors = response.errors match {
    case Right(errorMap) => errorMap.nonEmpty
    case Left(errorList) => errorList.nonEmpty
  }

  if (hasErrors) {
    IO.raiseError(new RuntimeException(s"API errors: ${response.errors}"))
  } else {
    IO.println(s"Found ${response.results} fixtures")
  }
}
```

## Pagination

For non-streaming endpoints:
- Most endpoints return paginated results (usually 50-100 items per page)
- Check `response.paging` for pagination information
- Use streaming methods for large datasets

## Common Parameters

Many methods support additional parameters not shown here (e.g., date ranges, filters). Check the [API-Football documentation](https://www.api-football.com/documentation) for all available parameters.

## Next Steps

- [Getting Started](getting-started.md) - Basic setup and usage
- [API Client Basics](api-client-basics.md) - Common patterns and examples
- [Error Handling](error-handling.md) - Strategies for handling errors
- [Streaming](streaming.md) - Working with large datasets