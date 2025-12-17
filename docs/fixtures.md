# Fixtures & Matches

This guide covers working with fixtures (matches) using the API Football Scala Client.

## Fixture Basics

A fixture represents a football match with comprehensive information about teams, scores, events, and more.

### Fetching Fixtures

```scala
import com.footballsync.model.FootballDataResponses._

// Get all fixtures for a league and season
apiClient.fetchFixtures("39", "2023").map { response =>
  response.response.map { fixture =>
    s"${fixture.teams.home.name} vs ${fixture.teams.away.name}"
  }
}
```

### Single Fixture

```scala
// Fetch a specific fixture by ID
apiClient.fetchSingleFixture("1034627").flatMap { response =>
  response.response.headOption match {
    case Some(fixture) =>
      IO.println(s"Match: ${fixture.teams.home.name} ${fixture.goals.home} - ${fixture.goals.away} ${fixture.teams.away.name}")
    case None =>
      IO.println("Fixture not found")
  }
}
```

### Multiple Fixtures by IDs

```scala
// Fetch multiple specific fixtures
apiClient.fetchSingleFixtures(List("1034627", "1034628", "1034629")).map { response =>
  response.response.foreach { fixture =>
    println(s"${fixture.fixture.id}: ${fixture.teams.home.name} vs ${fixture.teams.away.name}")
  }
}
```

## Filtering Fixtures

### By Date Range

```scala
import java.time.LocalDate

val today = LocalDate.now()
val nextWeek = today.plusWeeks(1)

// Fetch fixtures within a date range
apiClient.fetchFixtures("39", "2023").map { response =>
  response.response
    .filter { fixture =>
      val fixtureDate = fixture.fixture.date.toLocalDate
      !fixtureDate.isBefore(today) && !fixtureDate.isAfter(nextWeek)
    }
    .map { fixture =>
      s"${fixture.fixture.date}: ${fixture.teams.home.name} vs ${fixture.teams.away.name}"
    }
}
```

### By Status

```scala
// Filter fixtures by status
apiClient.fetchFixtures("39", "2023").map { response =>
  val liveFixtures = response.response.filter(_.fixture.status.short == "LIVE")
  val finishedFixtures = response.response.filter(_.fixture.status.short == "FT")
  val upcomingFixtures = response.response.filter(_.fixture.status.short == "NS")

  (liveFixtures, finishedFixtures, upcomingFixtures)
}
```

### By Team

```scala
// Get fixtures for a specific team
def getTeamFixtures(apiClient: FootballApiClient[IO], teamId: String, season: String): IO[List[Fixture]] =
  apiClient.fetchFixtures("39", season).map { response =>
    response.response.filter { fixture =>
      fixture.teams.home.id == teamId || fixture.teams.away.id == teamId
    }
  }
```

## Fixture Details

### Accessing Basic Information

```scala
apiClient.fetchSingleFixture("1034627").map { response =>
  response.response.headOption.map { fixture =>
    // Basic fixture info
    val id = fixture.fixture.id
    val date = fixture.fixture.date
    val venue = fixture.fixture.venue.name
    val status = fixture.fixture.status.long

    // Teams
    val homeTeam = fixture.teams.home.name
    val awayTeam = fixture.teams.away.name

    // Score
    val homeScore = fixture.goals.home
    val awayScore = fixture.goals.away

    s"$homeTeam $homeScore - $awayScore $awayTeam at $venue ($status)"
  }
}
```

### League and Season Information

```scala
apiClient.fetchSingleFixture("1034627").map { response =>
  response.response.headOption.map { fixture =>
    val league = fixture.league.name
    val season = fixture.league.season
    val round = fixture.league.round
    val country = fixture.league.country

    s"$league $season - Round: $round ($country)"
  }
}
```

## Fixture Events

Events include goals, cards, substitutions, and more:

```scala
// Fetch events for a specific fixture
apiClient.fetchFixtureEvents("1034627").map { response =>
  response.response.foreach { event =>
    val time = event.time.elapsed
    val team = event.team.name
    val player = event.player.name
    val eventType = event.type_ // goal, card, substitution, etc.
    val detail = event.detail.getOrElse("")

    println(s"$time' - $eventType: $player ($team) $detail")
  }
}
```

### Filtering Specific Event Types

```scala
// Get only goals
apiClient.fetchFixtureEvents("1034627").map { response =>
  val goals = response.response.filter(_.type_ == "Goal")
  goals.foreach { goal =>
    val scorer = goal.player.name
    val time = goal.time.elapsed
    val score = goal.detail.getOrElse("")
    println(s"$time' - Goal: $scorer $score")
  }

  val cards = response.response.filter(_.type_ == "Card")
  cards.foreach { card =>
    val player = card.player.name
    val time = card.time.elapsed
    val cardType = card.detail.getOrElse("")
    println(s"$time' - Card: $player $cardType")
  }
}
```

## Fixture Lineups

Lineups provide team formations and starting players:

```scala
apiClient.fetchFixtureLineups("1034627").map { response =>
  response.response.foreach { lineup =>
    val team = lineup.team.name
    val formation = lineup.formation
    val coach = lineup.coach.name

    println(s"$team - Formation: $formation, Coach: $coach")
    println("Start XI:")

    lineup.startXI.foreach { player =>
      println(s"  ${player.player.pos}: ${player.player.name} (${player.player.number})")
    }

    println("\nSubstitutes:")
    lineup.substitutes.foreach { player =>
      println(s"  ${player.player.pos}: ${player.player.name} (${player.player.number})")
    }
  }
}
```

## Fixture Statistics

Detailed statistics for each team:

```scala
apiClient.fetchFixtureStatistics("1034627").map { response =>
  response.response.foreach { stats =>
    val team = stats.team.name
    println(s"\nStatistics for $team:")

    stats.statistics.foreach { stat =>
      println(s"  ${stat.type_}: ${stat.value}")
    }
  }
}
```

### Comparing Statistics

```scala
def compareFixtureStats(apiClient: FootballApiClient[IO], fixtureId: String): IO[Unit] =
  apiClient.fetchFixtureStatistics(fixtureId).map { response =>
    response.response match {
      case List(homeStats, awayStats) =>
        println(s"${homeStats.team.name} vs ${awayStats.team.name}")

        homeStats.statistics.zip(awayStats.statistics).foreach { case (home, away) =>
          println(s"${home.type_}: ${home.value} - ${away.value}")
        }

      case _ =>
        println("Statistics not available")
    }
  }
```

## Player Statistics for Fixture

Individual player performance in a match:

```scala
apiClient.fetchFixturePlayerStatistics("1034627").map { response =>
  response.response.foreach { teamStats =>
    val team = teamStats.team.name
    println(s"\nPlayer statistics for $team:")

    teamStats.players.foreach { player =>
      val name = player.player.name
      val position = player.statistics.find(_.type_ == "Position").map(_.value.getOrElse("N/A")).getOrElse("N/A")
      val rating = player.statistics.find(_.type_ == "rating").map(_.value.getOrElse("N/A")).getOrElse("N/A")

      println(s"  $name ($position) - Rating: $rating")

      // Other statistics
      player.statistics.foreach { stat =>
        if (stat.type_ != "Position" && stat.type_ != "rating") {
          println(s"    ${stat.type_}: ${stat.value.getOrElse("0")}")
        }
      }
    }
  }
}
```

## Head-to-Head Records

```scala
// Get head-to-head fixtures between two teams
apiClient.fetchHeadToHead("33-34").map { response => // Man Utd vs Liverpool
  response.response.take(10).foreach { fixture =>
    val result = if (fixture.teams.home.winner) "Home Win"
    else if (fixture.teams.away.winner) "Away Win"
    else "Draw"

    println(s"${fixture.fixture.date} - ${fixture.teams.home.name} ${fixture.goals.home}-${fixture.goals.away} ${fixture.teams.away.name} ($result)")
  }
}
```

## Common Use Cases

### 1. Get Today's Live Matches

```scala
import java.time.LocalDate

def getLiveMatches(apiClient: FootballApiClient[IO]): IO[List[Fixture]] =
  apiClient.fetchFixtures("39", LocalDate.now().getYear.toString).map { response =>
    response.response.filter { fixture =>
      fixture.fixture.status.short == "LIVE" ||
      fixture.fixture.status.short == "HT" ||
      fixture.fixture.status.short == "PEN"
    }
  }
```

### 2. Find Team's Recent Form

```scala
def getTeamForm(apiClient: FootballApiClient[IO], teamId: String, lastGames: Int = 5): IO[List[String]] =
  apiClient.fetchFixtures("39", "2023").map { response =>
    response.response
      .filter { fixture =>
        (fixture.teams.home.id == teamId || fixture.teams.away.id == teamId) &&
        fixture.fixture.status.short == "FT"
      }
      .takeRight(lastGames)
      .map { fixture =>
        val teamWon = (fixture.teams.home.id == teamId && fixture.teams.home.winner) ||
                     (fixture.teams.away.id == teamId && fixture.teams.away.winner)

        val opponent = if (fixture.teams.home.id == teamId) fixture.teams.away.name else fixture.teams.home.name
        val score = s"${fixture.goals.home}-${fixture.goals.away}"

        if (teamWon) s"W $score vs $opponent"
        else s"L $score vs $opponent"
      }
      .reverse
  }
```

### 3. Upcoming Fixtures Alert

```scala
import java.time.{LocalDate, Duration}

def getUpcomingFixtures(apiClient: FootballApiClient[IO], teamId: String, daysAhead: Int = 7): IO[List[Fixture]] = {
  val today = LocalDate.now()
  val endDate = today.plusDays(daysAhead)

  apiClient.fetchFixtures("39", today.getYear.toString).map { response =>
    response.response.filter { fixture =>
      val fixtureDate = fixture.fixture.date.toLocalDate
      (fixture.teams.home.id == teamId || fixture.teams.away.id == teamId) &&
      !fixtureDate.isBefore(today) &&
      !fixtureDate.isAfter(endDate) &&
      fixture.fixture.status.short == "NS" // Not Started
    }.sortBy(_.fixture.date)
  }
}
```

### 4. Match Predictions

```scala
apiClient.fetchPredictions("1034627").map { response =>
  response.response.headOption.map { prediction =>
    val teams = prediction.teams.getOrElse(null)
    val homeTeam = teams.home.name
    val awayTeam = teams.away.name

    val winner = prediction.predictions.winner.map(_.name).getOrElse("N/A")
    val percent = prediction.predictions.percent.getOrElse(Map.empty)

    println(s"Match: $homeTeam vs $awayTeam")
    println(s"Prediction: Winner: $winner")
    println(s"Percentages: Home: ${percent.getOrElse("home", "N/A")}, Draw: ${percent.getOrElse("draw", "N/A")}, Away: ${percent.getOrElse("away", "N/A")}")
  }
}
```

### 5. Create Match Report

```scala
case class MatchReport(
  fixture: Fixture,
  events: List[Event],
  lineups: List[Lineup],
  statistics: List[FixtureStatistic]
)

def createMatchReport(
  apiClient: FootballApiClient[IO],
  fixtureId: String
): IO[MatchReport] = for {
  fixture <- apiClient.fetchSingleFixture(fixtureId).map(_.response.head)
  events <- apiClient.fetchFixtureEvents(fixtureId).map(_.response)
  lineups <- apiClient.fetchFixtureLineups(fixtureId).map(_.response)
  statistics <- apiClient.fetchFixtureStatistics(fixtureId).map(_.response)
} yield MatchReport(fixture, events, lineups, statistics)

def printMatchReport(report: MatchReport): Unit = {
  val f = report.fixture

  println(s"\n=== MATCH REPORT ===")
  println(s"${f.teams.home.name} ${f.goals.home} - ${f.goals.away} ${f.teams.away.name}")
  println(s"${f.league.name} - ${f.fixture.date}")
  println(s"Venue: ${f.fixture.venue.name}\n")

  val goals = report.events.filter(_.type_ == "Goal")
  if (goals.nonEmpty) {
    println("GOALS:")
    goals.foreach { event =>
      println(s"  ${event.time.elapsed}' ${event.player.name} (${event.team.name})")
    }
    println()
  }

  if (report.statistics.nonEmpty) {
    println("KEY STATISTICS:")
    val stats = report.statistics.flatMap(_.statistics).groupBy(_.type_)

    List("Ball Possession", "Total Shots", "Shots on Goal", "Fouls").foreach { statType =>
      stats.get(statType) match {
        case Some(List(home, away)) =>
          println(s"  $statType: ${home.value} - ${away.value}")
        case _ =>
      }
    }
  }
}
```

## Next Steps

- [Teams & Players](teams-players.md) - Working with team and player data
- [Statistics](statistics.md) - Advanced statistics and analysis
- [Streaming](streaming.md) - Handling large numbers of fixtures
