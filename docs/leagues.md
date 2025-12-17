---
layout: page
title: Leagues & Competitions
permalink: /leagues/
---

# Leagues & Competitions

This guide covers working with league and competition data using the API Football Scala Client.

## League Information

### Fetching All Leagues

```scala
import com.footballsync.model.FootballDataResponses._

// Get all available leagues
apiClient.fetchAllLeagues().map { response =>
  response.response.foreach { league =>
    val country = league.country.name
    val name = league.league.name
    val season = league.seasons.lastOption.map(_.year).getOrElse("N/A")
    val current = league.seasons.lastOption.exists(_.current)

    println(s"$name ($country) - Latest season: $season ${if (current) "(Current)" else ""}")
  }
}
```

### League Details

```scala
apiClient.fetchAllLeagues().map { response =>
  response.response
    .find(_.league.id == 39) // Premier League
    .foreach { premierLeague =>
      println(s"${premierLeague.league.name}")
      println(s"Type: ${premierLeague.league.type_}")
      println(s"Logo: ${premierLeague.league.logo}")
      println(s"Country: ${premierLeague.country.name}")

      println("\nAvailable Seasons:")
      premierLeague.seasons.foreach { season =>
        val status = if (season.current) " (Current)" else ""
        println(s"  ${season.year}: ${season.start} to ${season.end}$status")
        if (season.coverage.nonEmpty) {
          println(s"    Coverage: ${season.coverage.keys.mkString(", ")}")
        }
      }
    }
}
```

### League Standings

```scala
// Get Premier League table for 2023
apiClient.fetchStandings("39", "2023").map { response =>
  response.response.foreach { league =>
    println(s"\n${league.league.name} ${league.season} Standings")

    league.league.standings.foreach { standings =>
      println(s"\n${standings[0].group}") // Group name (e.g., "Premier League")

      standings.foreach { position =>
        val rank = position.rank
        val team = position.team.name
        val points = position.points
        val played = position.all.played
        val won = position.all.win
        val draw = position.all.draw
        val lost = position.all.lose
        val goalsFor = position.all.goals.for
        val goalsAgainst = position.all.goals.against
        val goalDiff = goalsFor - goalsAgainst

        println(f"$rank%2d. $team%-20s $points%3d P:$played W:$won D:$draw L:$lost GF:$goalsFor GA:$goalsAgainst GD:$goalDiff%+3d")

      }
    }
  }
}
```

### League Seasons

```scala
// Get all available seasons
apiClient.fetchSeasons().map { response =>
  val seasons = response.response.map(_.year).sorted
  println(s"Available seasons: ${seasons.mkString(", ")}")

  val currentYear = java.time.LocalDate.now().getYear
  val currentSeasons = response.response.filter(_.year == currentYear.toString)
  if (currentSeasons.nonEmpty) {
    println(s"\nCurrent season data is available")
  }
}
```

### Team Seasons

```scala
apiClient.fetchTeamSeasons("33").map { response =>
  response.response.foreach { season =>
    println(s"${season.year}: ${season.start} to ${season.end}")
    if (season.coverage.nonEmpty) {
      println(s"  Available: ${season.coverage.keys.mkString(", ")}")
    }
  }
}
```

## League Fixtures

### League Fixtures by Season

```scala
// Get all fixtures for a league/season
apiClient.fetchFixtures("39", "2023").map { response =>
  val totalFixtures = response.response.length
  println(s"Total fixtures: $totalFixtures")

  val byRound = response.response.groupBy(_.league.round)

  byRound.toSeq.sortBy(_._1).foreach { case (round, fixtures) =>
    println(s"\n$round (${fixtures.length} matches):")
    fixtures.take(5).foreach { fixture =>
      val home = fixture.teams.home.name
      val away = fixture.teams.away.name
      val score = s"${fixture.goals.home.getOrElse("-")} - ${fixture.goals.away.getOrElse("-")}"
      val status = fixture.fixture.status.short

      println(s"  $home $score $away ($status)")
    }
    if (fixtures.length > 5) {
      println(s"  ... and ${fixtures.length - 5} more")
    }
  }
}
```

### League Rounds

```scala
// Get all rounds for a league/season
apiClient.fetchFixtureRounds("39", "2023").map { response =>
  val rounds = response.response.flatMap(_.response)

  println("Regular Season Rounds:")
  rounds
    .filter(_.matches("Regular Season"))
    .sorted
    .foreach(println)

  println("\nCup Rounds (if any):")
  rounds
    .filterNot(_.matches("Regular Season"))
    .sorted
    .foreach(println)
}
```

## League Statistics

### Top Scorers

```scala
// Get top scorers for Premier League 2023
apiClient.fetchTopScorers("39", "2023").map { response =>
  response.response.take(20).zipWithIndex.foreach { case (player, index) =>
    val rank = index + 1
    val name = player.player.name
    val team = player.statistics.head.team.name
    val goals = player.statistics.head.goals.total
    val assists = player.statistics.head.goals.assists.getOrElse(0)
    val games = player.statistics.head.games.appearances

    println(f"$rank%2d. $name%-20s ($team) - $goals goals, $assists assists in $games games")
  }
}
```

### Top Assists

```scala
apiClient.fetchTopAssists("39", "2023").map { response =>
  response.response.take(10).zipWithIndex.foreach { case (player, index) =>
    val rank = index + 1
    val name = player.player.name
    val team = player.statistics.head.team.name
    val assists = player.statistics.head.goals.assists.getOrElse(0)
    val goals = player.statistics.head.goals.total

    println(f"$rank%2d. $name%-20s ($team) - $assists assists, $goals goals")
  }
}
```

### Disciplinary Records

```scala
// Players with most yellow cards
apiClient.fetchTopYellowCards("39", "2023").map { response =>
  response.response.take(10).zipWithIndex.foreach { case (player, index) =>
    val rank = index + 1
    val name = player.player.name
    val team = player.statistics.head.team.name
    val yellowCards = player.statistics.head.cards.yellow
    val redCards = player.statistics.head.cards.red

    println(f"$rank%2d. $name%-20s ($team) - Yellow: $yellowCards, Red: $redCards")
  }
}
```

## Multi-League Operations

### Compare Across Leagues

```scala
def compareLeagues(
  apiClient: FootballApiClient[IO],
  leagueIds: List[String],
  season: String
): IO[Unit] = for {
  standings <- leagueIds.traverse { leagueId =>
    apiClient.fetchStandings(leagueId, season).map { response =>
      (leagueId, response.response)
    }
  }
  topScorers <- leagueIds.traverse { leagueId =>
    apiClient.fetchTopScorers(leagueId, season).map { response =>
      (leagueId, response.response.take(5))
    }
  }
} yield {
  println("League Comparison:\n")

  standings.foreach { case (leagueId, leagues) =>
    leagues.headOption.foreach { league =>
      println(s"${league.league.name} Table Top 5:")
      league.league.standings.foreach { standings =>
        standings.take(5).foreach { position =>
          println(f"  ${position.rank}. ${position.team.name} - ${position.points} pts")
        }
      }
    }
  }

  println("\nTop Scorers by League:")
  topScorers.foreach { case (leagueId, scorers) =>
    standings.flatMap(_._2)
      .find(_.league.id == leagueId)
      .foreach { league =>
        println(s"\n${league.league.name}:")
        scorers.zipWithIndex.foreach { case (player, index) =>
          val name = player.player.name
          val goals = player.statistics.head.goals.total
          println(f"  ${index + 1}. $name - $goals goals")
        }
      }
  }
}
```

### League Coverage Analysis

```scala
apiClient.fetchAllLeagues().map { response =>
  val leaguesByCountry = response.response.groupBy(_.country.name)

  println("League Coverage by Country:\n")

  leaguesByCountry.toSeq.sortBy(_._1).foreach { case (country, leagues) =>
    println(s"$country:")

    leagues.foreach { league =>
      val currentSeason = league.seasons.find(_.current)
      currentSeason.foreach { season =>
        val hasFixtures = season.coverage.get("fixtures").exists(_ == true)
        val hasStandings = season.coverage.get("standings").exists(_ == true)
        val hasPlayers = season.coverage.get("players").exists(_ == true)
        val hasTopScorers = season.coverage.get("top_scorers").exists(_ == true)
        val hasTopAssists = season.coverage.get("top_assists").exists(_ == true)

        println(s"  ${league.league.name} (${season.year}):")
        println(s"    Fixtures: $hasFixtures")
        println(s"    Standings: $hasStandings")
        println(s"    Players: $hasPlayers")
        println(s"    Top Scorers: $hasTopScorers")
        println(s"    Top Assists: $hasTopAssists")
      }
    }
    println()
  }
}
```

## Special League Types

### International Competitions

```scala
apiClient.fetchAllLeagues().map { response =>
  val internationalLeagues = response.response.filter(_.country.name == "World")

  println("International Competitions:")
  internationalLeagues.foreach { league =>
    println(s"\n${league.league.name}")
    println(s"Type: ${league.league.type_}")

    league.seasons.sortBy(_.year).reverse.take(3).foreach { season =>
      println(s"  ${season.year}: ${season.start} to ${season.end}")
    }
  }
}
```

### Cup Competitions

```scala
apiClient.fetchAllLeagues().map { response =>
  val cupLeagues = response.response.filter(_.league.type_ == "Cup")

  println("Cup Competitions by Country:")
  cupLeagues.groupBy(_.country.name).foreach { case (country, cups) =>
    println(s"\n$country:")
    cups.foreach { cup =>
      val currentSeason = cup.seasons.find(_.current)
      val seasonYear = currentSeason.map(_.year).getOrElse("N/A")
      println(s"  - ${cup.league.name} ($seasonYear)")
    }
  }
}
```

## Common Use Cases

### 1. Find Close Title Races

```scala
def findCloseTitleRaces(
  apiClient: FootballApiClient[IO],
  leagueIds: List[String],
  season: String
): IO[Unit] = for {
  standings <- leagueIds.traverse { leagueId =>
    apiClient.fetchStandings(leagueId, season).map { response =>
      (leagueId, response.response)
    }
  }
} yield {
  standings.foreach { case (leagueId, leagues) =>
    leagues.headOption.foreach { league =>
      league.league.standings.foreach { standings =>
        val top2 = standings.take(2)
        if (top2.length == 2) {
          val pointsDiff = top2.head.points - top2(1).points

          if (pointsDiff <= 6) {
            println(s"${league.league.name} - Close Title Race!")
            println(f"  1. ${top2.head.team.name}: ${top2.head.points} pts")
            println(f"  2. ${top2(1).team.name}: ${top2(1).points} pts")
            println(f"     Gap: $pointsDiff points")
          }
        }
      }
    }
  }
}
```

### 2. Relegation Battle

```scala
def analyzeRelegationBattle(
  apiClient: FootballApiClient[IO],
  leagueId: String,
  season: String
): IO[Unit] = for {
  standings <- apiClient.fetchStandings(leagueId, season)
} yield {
  standings.response.foreach { league =>
    league.league.standings.foreach { standings =>
      // Get bottom 5 teams
      val bottomTeams = standings.takeRight(5).reverse
      val safePosition = standings.length - 5

      println(s"\n${league.league.name} Relegation Battle:")
      println(s"Teams below ${safePosition}th position:")

      bottomTeams.zipWithIndex.foreach { case (position, index) =>
        val status = if (index < 3) "Relegated" else "Play-off"
        val points = position.points
        val gamesRemaining = 38 - position.all.played // Assuming 38-game season
        val maxPossiblePoints = points + (gamesRemaining * 3)

        println(f"${position.rank}. ${position.team.name}: $points pts (Max: $maxPossiblePoints) - $status")
      }
    }
  }
}
```

### 3. League Activity Calendar

```scala
def getLeagueActivity(
  apiClient: FootballApiClient[IO],
  leagueId: String,
  season: String
): IO[Unit] = for {
  fixtures <- apiClient.fetchFixtures(leagueId, season)
} yield {
  val monthlyActivity = fixtures.response.groupBy { fixture =>
    fixture.fixture.date.getMonth.toString
  }

  println(s"League Activity Calendar:")
  monthlyActivity.toSeq.sortBy(_._1).foreach { case (month, matches) =>
    val totalMatches = matches.length
    val completedMatches = matches.count(_.fixture.status.short == "FT")
    val upcomingMatches = matches.filter(_.fixture.status.short == "NS")

    println(s"\n$month:")
    println(f"  Total matches: $totalMatches")
    println(f"  Completed: $completedMatches")
    println(f"  Upcoming: ${upcomingMatches.length}")

    if (upcomingMatches.nonEmpty) {
      val nextMatch = upcomingMatches.minBy(_.fixture.date)
      println(s"  Next match: ${nextMatch.fixture.date}")
    }
  }
}
```

## Next Steps

- [Statistics](statistics.md) - Advanced league statistics
- [Fixtures & Matches](fixtures.md) - Match data and results
- [Teams & Players](teams-players.md) - Team and player information
