---
layout: default
title: Home
---

# API Football Scala Client

Welcome to the comprehensive documentation for the API Football Scala Client. This library provides a functional, type-safe interface to the [API-Football](https://www.api-football.com/) service.

## Overview

The API Football Scala Client is built with functional programming principles using:

- **Cats Effect** for handling side effects
- **http4s** for HTTP communication
- **Circe** for JSON serialization
- **FS2** for streaming large datasets
- **ScalaTest** for comprehensive testing

### Key Features

- **Type Safety**: Compile-time guarantees for API responses
- **Functional**: Pure functions and effect types
- **Streaming**: Efficient handling of large datasets
- **Comprehensive**: Access to all API-Football v3 endpoints
- **Well Tested**: Extensive test suite with property-based testing

## Quick Start

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "io.github.craig1901" %% "api-football-scala-client" % "1.0.0"
)
```

Make your first API call:

```scala
import cats.effect._
import com.footballsync.api.client.FootballApiClientImpl
import org.http4s.ember.client.EmberClientBuilder

object MyFirstApiCall extends IOApp.Simple {
  def run: IO[Unit] =
    EmberClientBuilder.default[IO].build.use { client =>
      val apiClient = new FootballApiClientImpl[IO](client, "your-api-key-here")

      apiClient.fetchAllLeagues().flatMap { response =>
        IO.println(s"Found ${response.results} leagues")
      }
    }
}
```

## Documentation

### Core Guides

1. [Getting Started](getting-started.html) - Installation and first API call
2. [API Client Basics](api-client-basics.html) - Learn about the client interface
3. [Fixtures & Matches](fixtures.html) - Working with match data
4. [Teams & Players](teams-players.html) - Team and player information
5. [Leagues & Competitions](leagues.html) - League and competition data

### Advanced Topics

6. [Statistics](statistics.html) - Accessing match and team statistics
7. [Streaming & Pagination](streaming.html) - Efficiently handling large datasets
8. [Error Handling](error-handling.html) - Advanced error handling strategies
9. [API Reference](api-reference.html) - Complete API reference

## Quick Links

- [Installation](getting-started.html#installation)
- [First API Call](getting-started.html#first-api-call)
- [Rate Limiting](getting-started.html#rate-limiting)
- [Common Examples](api-client-basics.html#common-examples)

## API Version

This client uses API-Football **v3**. Make sure your API subscription supports v3 endpoints.

## Support

- GitHub Issues: [Report a bug or request a feature](https://github.com/craig1901/api-football-scala-client/issues)
- API-Football Documentation: [Official API docs](https://www.api-football.com/documentation-v3)
