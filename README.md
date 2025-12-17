# API Football Scala Client

A functional, type-safe Scala client for the [API-Football](https://www.api-football.com/) service (v3). This library provides comprehensive access to football data including fixtures, teams, players, leagues, statistics, transfers, and more.

[![Scala CI](https://github.com/craig1901/api-football-scala-client/workflows/CI/badge.svg)](https://github.com/craig1901/api-football-scala-client/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.craig1901/api-football-scala-client_2.13.svg)](https://search.maven.org/search?q=g:io.github.craig1901%20AND%20a:api-football-scala-client_2.13)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Features

- **Type-safe**: Full Scala type safety with compile-time checks
- **Functional**: Built on Cats Effect for pure functional programming
- **Comprehensive**: Access to all API-Football v3 endpoints
- **Rate Limited**: Built-in rate limiting to respect API limits
- **Retry Logic**: Automatic retries with exponential backoff
- **Streaming**: Memory-efficient pagination with FS2 streams
- **JSON Handling**: Automatic JSON serialization/deserialization with Circe
- **Tested**: Comprehensive test suite with property-based testing

## Installation

Add the following to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "io.github.craig1901" %% "api-football-scala-client" % "version" // Replace with latest version
)
```

## Quick Start

```scala
import cats.effect._
import com.footballsync.api.client._
import com.footballsync.api.client.impl._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import com.footballsync.model.FootballDataResponses._

object ExampleApp extends IOApp.Simple {
  def run: IO[Unit] =
    EmberClientBuilder.default[IO].build.use { client =>
      val apiClient = new FootballApiClientImpl[IO](client, "your-api-key-here")

      for {
        // Fetch fixtures for Premier League 2023 season
        fixtures <- apiClient.fetchFixtures("39", "2023")
        _ <- IO.println(s"Found ${fixtures.results} fixtures")

        // Fetch team information for Manchester United
        team <- apiClient.fetchTeam("33")
        _ <- IO.println(s"Team: ${team.response.head.team.name}")

        // Stream all players in a league (handles pagination automatically)
        playerCount <- apiClient.fetchPlayersStream("39", "2023")
          .evalMap(players => IO.pure(players.response.length))
          .compile
          .foldMonoid
        _ <- IO.println(s"Total players: $playerCount")
      } yield ()
    }
}
```

## API Coverage

The client provides access to all major API-Football endpoints:

### Fixtures & Matches
- Single and multiple fixtures
- Fixture events, lineups, statistics
- Head-to-head records
- Live scores

### Teams & Players
- Team information and squads
- Player statistics and details
- Team seasons and countries
- Top scorers, assists, cards

### Leagues & Seasons
- All available leagues
- League standings and rounds
- Season information
- Team statistics by league/season

### Additional Data
- Transfers and trophies
- Injuries and sidelined players
- Predictions and odds
- Venues and referees
- Countries and timezones

## Building from Source

### Prerequisites

- Java 17 or later
- SBT 1.x

### Commands

```bash
# Clone the repository
git clone https://github.com/craig1901/api-football-scala-client.git
cd api-football-scala-client

# Compile the project
sbt compile

# Run all tests
sbt test

# Run a specific test
sbt "testOnly com.footballsync.api.client.FootballApiClientSpec"

# Format code
sbt scalafmtAll

# Check formatting
sbt scalafmtCheckAll

# Run tests with coverage
sbt clean coverage test coverageReport
```

## Testing

The project includes comprehensive tests:

- **Unit Tests**: Test each API method with mock HTTP responses
- **Property-based Tests**: Verify JSON serialization/deserialization
- **Integration Tests**: Test client behavior with realistic scenarios

To run all tests:
```bash
sbt test
```

## API Key

You need an API key from [API-Football](https://www.api-football.com/) to use this client:

1. Sign up at [api-football.com](https://www.api-football.com/)
2. Choose a subscription plan
3. Get your API key from the dashboard
4. Pass the key when creating the client instance

## Rate Limiting

The client includes built-in rate limiting to respect API limits:
- Random delays between requests (133ms ± 25ms)
- Automatic retries with exponential backoff
- Configurable retry policies

## Error Handling

All API responses are wrapped in `ApiResponse[T]` containing:
```scala
case class ApiResponse[T](
  get: String,        // Endpoint path
  parameters: Map[String, String],  // Request parameters
  errors: List[String],  // Any errors
  results: Int,       // Number of results
  paging: Paging,     // Pagination info
  response: List[T]   // The actual data
)
```

## Streaming Large Datasets

For endpoints that return large amounts of data, use the streaming methods:

```scala
// Stream all players in a league (handles pagination automatically)
apiClient.fetchPlayersStream("39", "2023")
  .evalMap { response =>
    response.response.traverse { player =>
      // Process each player
      IO.println(s"Player: ${player.player.name}")
    }
  }
  .compile
  .drain
```

## Examples

### Fetch Team Statistics

```scala
// Get Manchester United's statistics for Premier League 2023
val stats = apiClient.getTeamStatistics("33", "39", "2023")
stats.map { response =>
  response.response.foreach { stat =>
    println(s"Games Played: ${stat.fixtures.played.total}")
    println(s"Goals For: ${stat.goals.for.total.total}")
    println(s"Goals Against: ${stat.goals.against.total.total}")
  }
}
```

### Search Teams

```scala
// Search for teams by country
val teams = apiClient.fetchTeamsByCountry("England")
teams.map { response =>
  response.response.foreach { team =>
    println(s"${team.team.name} - ${team.team.country}")
  }
}
```

## Release Process

This project uses automated releases via GitHub Actions:

### Automatic Releases

Releases are automatically triggered when:
1. A tag matching `v*` is pushed (e.g., `v1.0.0`)
2. Changes are pushed to the `main` branch

### Release Steps

1. Update version in `build.sbt` if needed
2. Commit changes: `git commit -m "Release v1.0.0"`
3. Create and push tag: `git tag v1.0.0 && git push origin v1.0.0`
4. GitHub Actions will:
   - Build and test the project
   - Publish to Sonatype Central
   - Create a GitHub release

### Maven Central

Published artifacts are available at:
- [Maven Central](https://search.maven.org/search?q=g:io.github.craig1901%20AND%20a:api-football-scala-client_2.13)

## Dependencies

The client uses the following main dependencies:

- **Cats Effect** (3.3.12) - Functional effects
- **http4s** (0.23.26) - HTTP client
- **Circe** (0.14.1) - JSON handling
- **FS2** - Streaming
- **Cats Retry** (3.1.0) - Retry mechanisms

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## Support

- [API-Football Documentation](https://www.api-football.com/documentation)
- [GitHub Issues](https://github.com/craig1901/api-football-scala-client/issues)
- [API-Football Support](https://www.api-football.com/contact)