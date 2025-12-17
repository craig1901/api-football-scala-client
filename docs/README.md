# API Football Scala Client Documentation

Welcome to the comprehensive documentation for the API Football Scala Client. This library provides a functional, type-safe interface to the [API-Football](https://www.api-football.com/) service.

## Table of Contents

1. [Getting Started](getting-started.md)
2. [API Client Basics](api-client-basics.md)
3. [Fixtures & Matches](fixtures.md)
4. [Teams & Players](teams-players.md)
5. [Leagues & Competitions](leagues.md)
6. [Statistics](statistics.md)
7. [Streaming & Pagination](streaming.md)
8. [Error Handling](error-handling.md)
9. [API Reference](api-reference.md)

## Quick Links

- [Installation](getting-started.md#installation)
- [First API Call](getting-started.md#first-api-call)
- [Rate Limiting](getting-started.md#rate-limiting)
- [Common Examples](api-client-basics.md#common-examples)

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

## API Version

This client uses API-Football **v3**. Make sure your API subscription supports v3 endpoints.