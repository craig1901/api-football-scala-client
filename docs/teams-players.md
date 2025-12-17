# Teams & Players

This guide covers working with team and player data using the API Football Scala Client.

## Teams

### Fetching Team Information

```scala
import com.footballsync.model.FootballDataResponses._

// Get a specific team by ID
apiClient.fetchTeam("33").map { response =>
  response.headOption.map { team =>
    println(s"Team: ${team.team.name}")
    println(s"Country: ${team.team.country}")
    println(s"Founded: ${team.team.founded}")
    println(s"National: ${team.team.national}")
    println(s"Logo: ${team.team.logo}")
  }
}
```

### Team Details

```scala
apiClient.fetchTeam("33").map { response =>
  response.headOption.map { team =>
    // Basic information
    val name = team.team.name
    val code = team.team.code.getOrElse("N/A")
    val country = team.team.country
    val founded = team.team.founded.getOrElse(0)
    val national = team.team.national
    val logo = team.team.logo

    // Venue information
    val venue = team.venue
    venue.foreach { v =>
      println(s"Venue: ${v.name}")
      println(s"Address: ${v.address.getOrElse("N/A")}")
      println(s"City: ${v.city}")
      println(s"Capacity: ${v.capacity.getOrElse(0)}")
      println(s"Surface: ${v.surface.getOrElse("N/A")}")
      println(s"Image: ${v.image}")
    }
  }
}
```

### Teams by League and Season

```scala
// Get all teams in Premier League 2023
apiClient.fetchTeams("39", "2023").map { response =>
  response.response.foreach { team =>
    println(s"${team.team.name} (${team.team.country}) - Founded: ${team.team.founded.getOrElse("Unknown")}")
  }
}
```

### Teams by Country

```scala
// Get all teams from England
apiClient.fetchTeamsByCountry("England").map { response =>
  response.response
    .groupBy(_.team.country)
    .foreach { case (country, teams) =>
      println(s"\n$country (${teams.length} teams):")
      teams.sortBy(_.team.name).foreach { team =>
        println(s"  - ${team.team.name}")
      }
    }
}
```

### Team Squad

```scala
apiClient.fetchSquad("33").map { response =>
  response.response.foreach { squad =>
    println(s"\n${squad.team.name} Squad:")

    squad.players.groupBy(_.position).foreach { case (position, players) =>
      println(s"\n$position:")
      players.foreach { player =>
        val age = player.age.map(a => s" ($a)").getOrElse("")
        println(s"  - ${player.number.getOrElse("N/A")}. ${player.name}$age")
      }
    }
  }
}
```

### Team Statistics

```scala
// Get detailed statistics for Manchester United in Premier League 2023
apiClient.getTeamStatistics("33", "39", "2023").map { response =>
  response.headOption.map { stats =>
    println(s"${stats.league.name} ${stats.league.season} Statistics")
    println(s"\nForm: ${stats.form.mkString(", ")}")

    // Fixtures
    println("\nFixtures:")
    println(s"  Played: ${stats.fixtures.played.total}")
    println(s"  Wins: ${stats.fixtures.wins.total}")
    println(s"  Draws: ${stats.fixtures.draws.total}")
    println(s"  Losses: ${stats.fixtures.loses.total}")

    // Goals
    println("\nGoals:")
    println(s"  For: ${stats.goals.for.total.total}")
    println(s"  Against: ${stats.goals.against.total.total}")
    println(s"  Clean Sheets: ${stats.clean_sheets.total}")

    // Cards
    println("\nCards:")
    println(s"  Yellow: ${stats.cards.yellow}")
    println(s"  Red: ${stats.cards.red}")

    // Other stats
    println("\nOther:")
    println(s"  Ball Possession: ${stats.ball_possession}%")
    println(s"  Passes: ${stats.passes.total}")
    println(s"  Pass Accuracy: ${stats.passes.accuracy}%")
    println(s"  Shots: ${stats.shots.total}")
    println(s"  Shots on Goal: ${stats.shots.on}")
  }
}
```

## Players

### Player Teams History

```scala
apiClient.fetchPlayerTeams("276").map { response =>
  response.response.foreach { playerTeam =>
    println(s"\n${playerTeam.player.name}")
    println(s"Age: ${playerTeam.player.age}")
    println(s"Photo: ${playerTeam.player.photo}")
    println(s"\nTeams:")

    playerTeam.statistics.foreach { stat =>
      println(s"  ${stat.team.name} (${stat.league.name} ${stat.season})")
      println(s"    - Games: ${stat.games.appearances}")
      println(s"    - Goals: ${stat.goals.total}")
      println(s"    - Assists: ${stat.goals.assists}")
      if (stat.cards.nonEmpty) {
        println(s"    - Cards: Yellow ${stat.cards.yellow}, Red ${stat.cards.red}")
      }
    }
  }
}
```

### Player Transfers

```scala
// Get transfers for a specific player
apiClient.fetchTransfers(playerId = Some("276")).map { response =>
  response.response.foreach { transfer =>
    println(s"${transfer.player.name} Transfers:")

    transfer.transfers.sortBy(_.date).foreach { t =>
      println(s"  ${t.date}: ${t.teams.from.name} → ${t.teams.to.name}")
      t.type_ match {
        case "Loan" => println(s"    Type: Loan End: ${t.date.substring(0, 4).toInt + 1}")
        case "Free" => println(s"    Type: Free Transfer")
        case _ => println(s"    Type: ${t.type_} (${t.amount.getOrElse("N/A")})")
      }
    }
  }
}
```

### Team Transfers

```scala
// Get all transfers for a team
apiClient.fetchTransfers(teamId = Some("33")).map { response =>
  response.response
    .groupBy(_.transfer.date.substring(0, 4)) // Group by year
    .toSeq
    .sortBy(_._1)
    .foreach { case (year, transfers) =>
      println(s"\nTransfers $year:")
      transfers.foreach { transfer =>
        val playerName = transfer.player.name
        val fromTeam = transfer.transfer.teams.from.name
        val toTeam = transfer.transfer.teams.to.name
        val transferType = transfer.transfer.type_

        if (toTeam == "Manchester United") {
          println(s"  IN:  $playerName from $fromTeam ($transferType)")
        } else {
          println(s"  OUT: $playerName to $toTeam ($transferType)")
        }
      }
    }
}
```

### Player Trophies

```scala
apiClient.fetchTrophies(playerId = Some("276")).map { response =>
  response.response.foreach { player =>
    println(s"\n${player.player.name} Trophies:")

    player.trophies
      .groupBy(_.league)
      .foreach { case (competition, trophies) =>
        println(s"\n$competition:")
        trophies
          .groupBy(_.season)
          .toSeq
          .sortBy(_._1)
          .foreach { case (season, seasonTrophies) =>
            println(s"  $season: ${seasonTrophies.length} trophy(ies)")
            seasonTrophies.foreach { trophy =>
              println(s"    - ${trophy.place.getOrElse("")}")
            }
          }
      }
  }
}
```

### Injuries and Sidelined

```scala
// Get current injuries for a league
apiClient.fetchInjuries("39", "2023").map { response =>
  response.response
    .filter { injury =>
      // Filter for recent injuries
      injury.fixture.date.toLocalDate.isAfter(LocalDate.now().minusDays(30))
    }
    .groupBy(_.team.name)
    .foreach { case (team, injuries) =>
      println(s"\n$team:")
      injuries.foreach { injury =>
        println(s"  - ${injury.player.name}: ${injury.reason}")
        println(s"    Since: ${injury.fixture.date}")
      }
    }
}
```

### Player Statistics Analysis

```scala
// Compare two players' statistics
def comparePlayers(
  apiClient: FootballApiClient[IO],
  player1Id: String,
  player2Id: String,
  leagueId: String,
  season: String
): IO[Unit] = for {
  player1Stats <- apiClient.fetchPlayerTeams(player1Id)
  player2Stats <- apiClient.fetchPlayerTeams(player2Id)
} yield {
  val p1SeasonStats = player1Stats.response.flatMap(_.statistics)
    .find(s => s.league.id == leagueId && s.season == season)

  val p2SeasonStats = player2Stats.response.flatMap(_.statistics)
    .find(s => s.league.id == leagueId && s.season == season)

  (p1SeasonStats, p2SeasonStats) match {
    case (Some(p1), Some(p2)) =>
      println(s"${p1.player.name} vs ${p2.player.name}")
      println(s"League: ${p1.league.name} $season")

      println("\nAppearances:")
      println(s"  ${p1.player.name}: ${p1.games.appearances}")
      println(s"  ${p2.player.name}: ${p2.games.appearances}")

      println("\nGoals:")
      println(s"  ${p1.player.name}: ${p1.goals.total}")
      println(s"  ${p2.player.name}: ${p2.goals.total}")

      println("\nAssists:")
      println(s"  ${p1.player.name}: ${p1.goals.assists}")
      println(s"  ${p2.player.name}: ${p2.goals.assists}")

      println("\nShots:")
      println(s"  ${p1.player.name}: ${p1.shots.total} (${p1.shots.on} on target)")
      println(s"  ${p2.player.name}: ${p2.shots.total} (${p2.shots.on} on target)")

    case _ =>
      println("Could not find statistics for comparison")
  }
}
```

## Common Use Cases

### 1. Find Team's Top Scorers

```scala
def getTeamTopScorers(
  apiClient: FootballApiClient[IO],
  teamId: String,
  leagueId: String,
  season: String
): IO[List[PlayerStatistic]] = {
  apiClient.fetchTopScorers(leagueId, season).map { response =>
    response.response
      .filter(_.statistics.exists(_.team.id == teamId))
      .sortBy(_.statistics.head.goals.total)
      .reverse
  }
}
```

### 2. Calculate Average Team Age

```scala
def calculateAverageTeamAge(
  apiClient: FootballApiClient[IO],
  teamId: String
): IO[Option[Double]] = {
  apiClient.fetchSquad(teamId).map { response =>
    response.headOption.map { squad =>
      val ages = squad.players.flatMap(_.age)
      if (ages.nonEmpty) ages.sum.toDouble / ages.length else 0.0
    }
  }
}
```

### 3. Find Players by Nationality in League

```scala
def findPlayersByNationality(
  apiClient: FootballApiClient[IO],
  leagueId: String,
  season: String,
  nationality: String
): IO[List[(Player, String)]] = {
  apiClient.fetchPlayersStream(leagueId, season)
    .flatMap { response =>
      Stream.emits(response.response)
    }
    .filter { player =>
      player.player.nationality.contains(nationality)
    }
    .map { player =>
      val teamName = player.statistics.head.team.name
      (player.player, teamName)
    }
    .compile
    .toList
}
```

## Next Steps

- [Statistics](statistics.md) - Advanced statistics and analytics
- [Fixtures & Matches](fixtures.md) - Match data and results
- [Streaming](streaming.md) - Efficiently handle large datasets