---
layout: page
title: Error Handling
permalink: /error-handling/
---

# Error Handling

This guide covers error handling strategies and best practices when using the API Football Scala Client.

## Types of Errors

### 1. API Response Errors

API responses include an `errors` field that indicates API-level issues:

```scala
case class ApiResponse[T](
  get: String,
  parameters: Either[List[Map[String, String]], Map[String, String]],
  errors: Either[List[Map[String, String]], Map[String, String]],  // API errors
  results: Int,
  paging: Paging,
  response: List[T]
)
```

The `errors` field uses an `Either` type to handle different response formats from the API.

Example of handling API errors:

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

### 2. HTTP Errors

HTTP errors are handled by the client's retry mechanism:

- **4xx errors** (client errors): Usually not retried
- **5xx errors** (server errors): Automatically retried with backoff

### 3. Network Errors

Network issues are automatically retried:
- Connection timeouts
- DNS resolution failures
- SSL/TLS errors
- Read timeouts

## Error Handling Patterns

### 1. Type-Safe Error Handling with Either

```scala
import cats.data.EitherT
import cats.syntax.either._

def safeGetTeam(apiClient: FootballApiClient[IO], teamId: String): EitherT[IO, ApiError, TeamData] =
  EitherT[IO, ApiError, TeamData] {
    apiClient.fetchTeam(teamId).map { response =>
      val hasErrors = response.errors match {
        case Right(errorMap) => errorMap.nonEmpty
        case Left(errorList) => errorList.nonEmpty
      }

      if (hasErrors) {
        Left(ApiError.ApiResponse(response.errors.toString))
      } else {
        response.response.headOption
          .toRight(ApiError.NotFound(s"Team $teamId not found"))
      }
    }
  }

sealed trait ApiError
object ApiError {
  case class NotFound(message: String) extends ApiError
  case class ApiResponse(errors: String) extends ApiError
  case class NetworkError(cause: Throwable) extends ApiError
}
```

### 2. Validating Parameters

```scala
import cats.data.ValidatedNel

case class TeamId private (value: String)

object TeamId {
  def fromString(id: String): ValidatedNel[String, TeamId] = {
    if (id.matches("\\d+") && id.nonEmpty) {
      TeamId(id).validNel
    } else {
      "Team ID must be a non-empty numeric string".invalidNel
    }
  }
}

def fetchTeamWithValidation(
  apiClient: FootballApiClient[IO],
  teamId: String
): IO[Either[String, TeamData]] = {
  TeamId.fromString(teamId).toEither match {
    case Right(validId) =>
      apiClient.fetchTeam(validId.value).map { response =>
        response.response.headOption.toRight("Team not found")
      }
    case Left(errors) =>
      IO.pure(Left(errors.mkString(", ")))
  }
}
```

## Next Steps

- [API Reference](api-reference.md) - Complete method documentation
- [Getting Started](getting-started.md) - Return to basics if needed