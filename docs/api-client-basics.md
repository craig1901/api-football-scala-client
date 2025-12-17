---
layout: page
title: API Client Basics
permalink: /api-client-basics/
---

# API Client Basics

This section covers the fundamental usage patterns of the API Football Scala Client.

## The FootballApiClient Trait

The `FootballApiClient[F[_]]` trait defines the interface for all API operations. It uses a higher-kinded type `F[_]` to work with any effect type that has an `Async` instance, typically `cats.effect.IO`.

### Creating the Client

```scala
import com.footballsync.api.client.FootballApiClientImpl
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

val clientResource: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

clientResource.use { client =>
  val apiClient = new FootballApiClientImpl[IO](client, "your-api-key")
  IO.unit
}
```

## Common Examples

### 1. Fetching Basic Information

```scala
apiClient.fetchAllLeagues().map { response =>
  response.response.map(_.league.name)
}

apiClient.fetchCountries().map { response =>
  response.response.map(_.name)
}

apiClient.fetchSeasons().map { response =>
  response.response.map(_.year)
}
```

### 2. Working with Teams

```scala
// Fetch a specific team by ID
apiClient.fetchTeam("33").flatMap { response =>
  response.response.headOption match {
    case Some(teamData) =>
      IO.println(s"Team: ${teamData.team.name}") *>
      IO.println(s"Country: ${teamData.team.country}") *>
      IO.println(s"Founded: ${teamData.team.founded}")
    case None =>
      IO.println("Team not found")
  }
}

// Fetch teams by league and season
apiClient.fetchTeams("39", "2023").flatMap { response =>
  response.response.traverse_ { team =>
    IO.println(s"${team.team.name} - ${team.team.country}")
  }
}

// Search teams by country
apiClient.fetchTeamsByCountry("England").flatMap { response =>
  response.response.traverse_ { team =>
    IO.println(s"${team.team.name} (est. ${team.team.founded})")
  }
}
```

### 3. Working with Fixtures (Matches)

```scala
apiClient.fetchFixtures("39", "2023").flatMap { response =>
  response.response.take(5).traverse_ { fixture =>
    IO.println(s"${fixture.teams.home.name} vs ${fixture.teams.away.name} on ${fixture.fixture.date}")
  }
}

apiClient.fetchSingleFixture("1034627").map { response =>
  response.response.headOption.map { fixture =>
    s"Score: ${fixture.goals.home} - ${fixture.goals.away}"
  }
}
```

### 4. Fetching Statistics

```scala
apiClient.getTeamStatistics("33", "39", "2023").flatMap { response =>
  response.response.headOption match {
    case Some(stats) =>
      IO.println(s"Games Played: ${stats.fixtures.played.total}") *>
      IO.println(s"Wins: ${stats.fixtures.wins.total}") *>
      IO.println(s"Goals For: ${stats.goals.for.total.total}") *>
      IO.println(s"Goals Against: ${stats.goals.against.total.total}") *>
      IO.println(s"Clean Sheets: ${stats.clean_sheet.total}")
    case None =>
      IO.println("Statistics not found")
  }
}

apiClient.fetchStandings("39", "2023").flatMap { response =>
  response.response.traverse_ { league =>
    league.league.standings.traverse_ { standings =>
      standings.take(10).traverse_ { position =>
        IO.println(s"${position.rank}. ${position.team.name} - ${position.points} pts")
      }
    }
  }
}
```

## Working with Parameters

Most API methods accept parameters to filter results:

```scala
apiClient.fetchVenues(
  name = Some("Old Trafford"),
  city = Some("Manchester"),
  country = Some("England"),
  id = None
)

apiClient.fetchSingleFixtures(List("1034627", "1034628", "1034629"))

apiClient.fetchHeadToHead("33-34") // Manchester United vs Liverpool
```

## Handling Optional Parameters

The client uses `Option` for optional parameters:

```scala
import cats.syntax.option._

apiClient.fetchTransfers(
  playerId = Some("123".some),
  teamId = None
)

apiClient.fetchCoaches(
  teamId = Some("33".some),
  coachId = None
)

val teamId: Option[String] = Some("33") // or None
val playerId: Option[String] = None

apiClient.fetchSidelined(playerId, teamId)
```

## Response Patterns

### Single Response vs Multiple Responses

```scala
apiClient.fetchTeam("33")

apiClient.fetchTeams("39", "2023")

val singleTeam = apiClient.fetchTeam("33").map(_.response.head)

val multipleTeams = apiClient.fetchTeams("39", "2023").map(_.response)
```

### Working with the ApiResponse

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

// Access pagination info
apiClient.fetchFixtures("39", "2023").map { response =>
  s"Current page: ${response.paging.current}, Total pages: ${response.paging.total}"
}

// Get parameters that were used
apiClient.fetchFixtures("39", "2023").map { response =>
  s"Query parameters: ${response.parameters}"
}
```

## Error Handling

### Type-Safe Error Handling

```scala
import cats.syntax.either._

def safeGetTeam(apiClient: FootballApiClient[IO], teamId: String): IO[Either[String, TeamResponse]] =
  apiClient.fetchTeam(teamId).map { response =>
    val hasErrors = response.errors match {
      case Right(errorMap) => errorMap.nonEmpty
      case Left(errorList) => errorList.nonEmpty
    }

    if (hasErrors) {
      Left(s"API error: ${response.errors}")
    } else {
      response.response.headOption.toRight("Team not found")
    }
  }
```

### Using EitherT for Composition

```scala
import cats.data.EitherT

def getTeamAndLeague(
  apiClient: FootballApiClient[IO],
  teamId: String,
  leagueId: Int
): EitherT[IO, String, (TeamResponse, LeagueResponse)] = for {
  team <- EitherT.fromEitherF[IO, String, TeamResponse](
    apiClient.fetchTeam(teamId).map(_.response.headOption.toRight("Team not found"))
  )
  league <- EitherT.fromEitherF[IO, String, LeagueResponse](
    apiClient.fetchAllLeagues().map(_.response.find(_.league.id == leagueId).toRight("League not found"))
  )
} yield (team, league)
```

## Best Practices

1. **Always use Resource for HTTP clients** to ensure proper cleanup
2. **Check for errors in ApiResponse** before processing data
3. **Use Option handling** safely for optional parameters
4. **Leverage Cats Effect** for concurrent operations
5. **Add proper logging** for debugging and monitoring

## Next Steps

- [Fixtures & Matches](fixtures.md) - Detailed fixture operations
- [Teams & Players](teams-players.md) - Team and player data
- [Streaming & Pagination](streaming.md) - Handling large datasets
