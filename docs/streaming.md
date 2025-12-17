# Streaming & Pagination

This guide covers how to handle large datasets efficiently using the streaming capabilities of the API Football Scala Client.

## Understanding Pagination

API-Football returns paginated responses for large datasets. Each response includes pagination information:

```scala
case class Paging(
  current: Int,    // Current page number
  total: Int,      // Total number of pages
  totalResults: Int // Total number of results
)
```

### Regular Paginated Methods

Most methods return a single page of results:

```scala
// Returns only the first page (default page size: usually 50-100 items)
apiClient.fetchFixtures("39", "2023").map { response =>
  response.paging.current  // 1
  response.paging.total    // e.g., 20
  response.results         // 50
}
```

## Streaming Methods

For large datasets, the client provides streaming methods that automatically handle pagination:

```scala
// Stream all players in a league/season
val playerStream: Stream[IO, ApiResponse[PlayerStatisticsResponse]] =
  apiClient.fetchPlayersStream("39", "2023")
```

## Working with Streams

### Basic Stream Processing

```scala
import fs2.Stream

// Count all players in Premier League 2023
apiClient.fetchPlayersStream("39", "2023")
  .evalMap { response =>
    IO.println(s"Processing page ${response.paging.current} of ${response.paging.total}")
  }
  .compile
  .drain

// Get total count of all players
apiClient.fetchPlayersStream("39", "2023")
  .evalMap { response =>
    IO.pure(response.response.length)
  }
  .compile
  .foldMonoid // Sum all page lengths
  .flatMap { total =>
    IO.println(s"Total players: $total")
  }
```

### Extracting Data from Streams

```scala
// Collect all player names
apiClient.fetchPlayersStream("39", "2023")
  .flatMap { response =>
    Stream.emits(response.response)
  }
  .map(_.player.name)
  .compile
  .toList

// Process players in batches
apiClient.fetchPlayersStream("39", "2023")
  .groupWithin(1000, 5.seconds) // Group up to 1000 players or within 5 seconds
  .evalMap { batch =>
    // Process batch of 1000 players
    IO.println(s"Processing batch of ${batch.size} players")
  }
  .compile
  .drain
```

### Filtering Streams

```scala
// Find all players over 30 years old
apiClient.fetchPlayersStream("39", "2023")
  .flatMap { response =>
    Stream.emits(response.response)
  }
  .filter { player =>
    player.player.age.exists(_ > 30)
  }
  .map { player =>
    s"${player.player.name} - Age: ${player.player.age}"
  }
  .take(20) // Limit to first 20 matches
  .compile
  .toList
  .flatMap { players =>
    players.traverse_(IO.println)
  }
```

## Next Steps

- [Error Handling](error-handling.md) - Advanced error handling strategies
- [API Reference](api-reference.md) - Complete method documentation
