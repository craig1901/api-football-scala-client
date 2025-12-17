# Getting Started

This guide will help you get up and running with the API Football Scala Client quickly.

## Prerequisites

1. **Scala 2.13.15** or later
2. **Java 17** or later
3. **SBT 1.x** (for building)
4. **API-Football API Key** - Sign up at [api-football.com](https://www.api-football.com/)

## Installation

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "io.github.craig1901" %% "api-football-scala-client" % "1.0.0" // Check for latest version
)
```

## First API Call

Here's a complete example of making your first API call:

```scala
import cats.effect._
import com.footballsync.api.client.FootballApiClientImpl
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import com.footballsync.model.FootballDataResponses._

object MyFirstApiCall extends IOApp.Simple {
  def run: IO[Unit] =
    EmberClientBuilder.default[IO].build.use { client =>
      // Create the client with your API key
      val apiClient = new FootballApiClientImpl[IO](client, "your-api-key-here")

      // Fetch all available leagues
      apiClient.fetchAllLeagues().flatMap { response =>
        IO.println(s"Found ${response.results} leagues")
        IO.println(s"First league: ${response.response.head.league.name}")
      }
    }
}
```

### Running the Example

Save the code to a file and run with:

```bash
sbt run
```

## Creating the Client

### Using Ember Client (Recommended)

```scala
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

EmberClientBuilder.default[IO].build.use { client =>
  val apiClient = new FootballApiClientImpl[IO](client, apiKey)
  // Use the client...
}
```

### Using a Custom HTTP Client

```scala
import org.http4s.blaze.client.BlazeClientBuilder

BlazeClientBuilder[IO].resource.use { client =>
  val apiClient = new FootballApiClientImpl[IO](client, apiKey)
  // Use the client...
}
```

### Dependency Injection Pattern

For applications using dependency injection:

```scala
trait FootballService[F[_]] {
  def getTodayFixtures(): F[List[Fixture]]
}

class FootballServiceImpl[F[_]: Async](client: Client[F], apiKey: String)
  extends FootballService[F] {

  private val apiClient = new FootballApiClientImpl[F](client, apiKey)

  def getTodayFixtures(): F[List[Fixture]] = {
    // Implementation using apiClient
    ???
  }
}
```

## Understanding Responses

All API responses are wrapped in `ApiResponse[T]`:

```scala
case class ApiResponse[T](
  get: String,                    // The endpoint that was called
  parameters: Map[String, String], // Parameters sent with the request
  errors: List[String],           // Any errors from the API
  results: Int,                   // Number of results returned
  paging: Paging,                 // Pagination information
  response: List[T]               // The actual data
)
```

### Example: Handling Response

```scala
apiClient.fetchTeamsByCountry("England").flatMap { response =>
  if (response.errors.nonEmpty) {
    IO.raiseError(new RuntimeException(s"API Error: ${response.errors.mkString(", ")}"))
  } else {
    response.response.traverse { team =>
      IO.println(s"${team.team.name} - Founded: ${team.team.founded}")
    }
  }
}
```

## Common Setup Patterns

### Configuration from Application Properties

```scala
import pureconfig._
import pureconfig.generic.auto._

case class AppConfig(apiFootball: ApiFootballConfig)
case class ApiFootballConfig(apiKey: String, baseUrl: String = "https://v3.football.api-sports.io")

object Main extends IOApp.Simple {
  def run: IO[Unit] = for {
    config <- ConfigSource.default.load[Config].liftTo[IO]
    _ <- EmberClientBuilder.default[IO].build.use { client =>
      val apiClient = new FootballApiClientImpl[IO](client, config.apiFootball.apiKey)
      // Use apiClient...
    }
  } yield ()
}
```

### Using Environment Variables

```scala
import scala.util.Properties

object Main extends IOApp.Simple {
  def run: IO[Unit] = {
    val apiKey = Properties.envOrNone("API_FOOTBALL_KEY")
      .getOrElse(sys.error("API_FOOTBALL_KEY environment variable not set"))

    EmberClientBuilder.default[IO].build.use { client =>
      val apiClient = new FootballApiClientImpl[IO](client, apiKey)
      // Use apiClient...
    }
  }
}
```

## Testing Your Setup

### Unit Tests with Mock Client

```scala
import cats.effect.IO
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.http4s.implicits._

class MyServiceTest extends AnyFlatSpec with Matchers {
  val mockClient = Client[IO] { req =>
    req match {
      case GET -> Root "/leagues" =>
        Ok("""{
          "get": "/leagues",
          "parameters": {},
          "errors": [],
          "results": 1,
          "paging": {"current": 1, "total": 1},
          "response": [{"league": {"id": 39, "name": "Premier League"}}]
        }""")
      case _ =>
        NotFound()
    }
  }

  val apiClient = new FootballApiClientImpl[IO](mockClient, "test-key")

  "API Client" should "fetch leagues" in {
    val result = apiClient.fetchAllLeagues().unsafeRunSync()
    result.results shouldBe 1
    result.response.head.league.name shouldBe "Premier League"
  }
}
```

## Next Steps

- [API Client Basics](api-client-basics.md) - Learn about the client interface
- [Fixtures & Matches](fixtures.md) - Working with match data
- [Teams & Players](teams-players.md) - Team and player information
- [Error Handling](error-handling.md) - Advanced error handling strategies