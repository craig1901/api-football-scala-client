# Statistics

This guide covers working with statistical data using the API Football Scala Client, including team statistics, player performance metrics, and advanced analytics.

## Team Statistics

### Complete Team Statistics

```scala
import com.footballsync.model.FootballDataResponses._

// Get comprehensive statistics for Manchester United in Premier League 2023
apiClient.getTeamStatistics("33", "39", "2023").map { response =>
  response.headOption.map { stats =>
    println(s"${stats.league.name} ${stats.league.season}")
    println(s"Team: ${stats.team.name}")

    // Basic record
    println("\n--- RECORD ---")
    println(f"Games Played: ${stats.fixtures.played.total}")
    println(f"Wins: ${stats.fixtures.wins.total} (${stats.fixtures.wins.home.get} home, ${stats.fixtures.wins.away.get} away)")
    println(f"Draws: ${stats.fixtures.draws.total} (${stats.fixtures.draws.home.get} home, ${stats.fixtures.draws.away.get} away)")
    println(f"Losses: ${stats.fixtures.loses.total} (${stats.fixtures.loses.home.get} home, ${stats.fixtures.loses.away.get} away)")

    // Form
    println("\n--- RECENT FORM ---")
    println(s"Last 5: ${stats.form.take(5).mkString(" ")}")

    // Goals
    println("\n--- GOALS ---")
    println(f"Goals For: ${stats.goals.for.total.total} (${stats.goals.for.total.home.get} home, ${stats.goals.for.total.away.get} away)")
    println(f"Goals Against: ${stats.goals.against.total.total} (${stats.goals.against.total.home.get} home, ${stats.goals.against.total.away.get} away)")
    println(f"Clean Sheets: ${stats.clean_sheets.total} (${stats.clean_sheets.home.get} home, ${stats.clean_sheets.away.get} away)")
    println(f"Failed to Score: ${stats.failed_to_score.total} (${stats.failed_to_score.home.get} home, ${stats.failed_to_score.away.get} away)")

    // Cards
    println("\n--- DISCIPLINARY ---")
    println(s"Yellow Cards: ${stats.cards.yellow}")
    println(s"Red Cards: ${stats.cards.red}")
    println(s"Yellow/Red Cards: ${stats.cards.yellowred}")

    // Advanced stats
    println("\n--- ADVANCED ---")
    if (stats.ball_possession.nonEmpty) println(f"Ball Possession: ${stats.ball_possession}%")
    if (stats.passes.nonEmpty) println(f"Passes: ${stats.passes.total} (${stats.passes.accuracy}% accuracy)")
    if (stats.shots.nonEmpty) println(f"Shots: ${stats.shots.total} (${stats.shots.on} on target)")
    if (stats.corners.nonEmpty) println(f"Corners: ${stats.corners.total}")
    if (stats.fouls.nonEmpty) println(f"Fouls: ${stats.fouls.total}")
    if (stats.offsides.nonEmpty) println(f"Offsides: ${stats.offsides.total}")
  }
}
```

### Comparing Team Statistics

```scala
def compareTeams(
  apiClient: FootballApiClient[IO],
  team1Id: String,
  team2Id: String,
  leagueId: String,
  season: String
): IO[Unit] = for {
  team1Stats <- apiClient.getTeamStatistics(team1Id, leagueId, season)
  team2Stats <- apiClient.getTeamStatistics(team2Id, leagueId, season)
} yield {
  (team1Stats.response.headOption, team2Stats.response.headOption) match {
    case (Some(stats1), Some(stats2)) =>
      println(s"Comparison: ${stats1.team.name} vs ${stats2.team.name}")
      println(s"League: ${stats1.league.name} $season\n")

      // Points calculation (assuming 3 pts for win)
      val team1Points = stats1.fixtures.wins.total * 3 + stats1.fixtures.draws.total
      val team2Points = stats2.fixtures.wins.total * 3 + stats2.fixtures.draws.total

      println(f"Points: ${stats1.team.name} $team1Points - $team2Points ${stats2.team.name}")

      println(f"\nRecord:")
      println(f"  ${stats1.team.name}: ${stats1.fixtures.wins.total}W ${stats1.fixtures.draws.total}D ${stats1.fixtures.loses.total}L")
      println(f"  ${stats2.team.name}: ${stats2.fixtures.wins.total}W ${stats2.fixtures.draws.total}D ${stats2.fixtures.loses.total}L")

      println(f"\nGoals:")
      println(f"  ${stats1.team.name}: For ${stats1.goals.for.total.total} - Against ${stats1.goals.against.total.total}")
      println(f"  ${stats2.team.name}: For ${stats2.goals.for.total.total} - Against ${stats2.goals.against.total.total}")

      // Goal difference
      val team1GD = stats1.goals.for.total.total - stats1.goals.against.total.total
      val team2GD = stats2.goals.for.total.total - stats2.goals.against.total.total
      println(f"\nGoal Difference:")
      println(f"  ${stats1.team.name}: $team1GD")
      println(f"  ${stats2.team.name}: $team2GD")

      println(f"\nClean Sheets:")
      println(f"  ${stats1.team.name}: ${stats1.clean_sheets.total}")
      println(f"  ${stats2.team.name}: ${stats2.clean_sheets.total}")

    case _ =>
      println("Could not retrieve statistics for comparison")
  }
}
```

## Player Statistics

### League Top Scorers

```scala
apiClient.fetchTopScorers("39", "2023").map { response =>
  println("Premier League Top Scorers 2023:\n")

  response.response.zipWithIndex.foreach { case (player, index) =>
    val rank = index + 1
    val stats = player.statistics.head
    val name = player.player.name
    val team = stats.team.name
    val goals = stats.goals.total
    val assists = stats.goals.assists.getOrElse(0)
    val games = stats.games.appearances
    val minutes = stats.games.minutes.getOrElse(0)
    val goalsPer90 = if (minutes > 0) (goals.toDouble / (minutes / 90.0)) else 0.0

    println(f"$rank%2d. $name%-20s ($team)")
    println(f"     Goals: $goals, Assists: $assists, Games: $games, G/90: $goalsPer90%.2f")

  }
}
```

### Player Performance Analysis

```scala
def analyzePlayer(
  apiClient: FootballApiClient[IO],
  playerId: String,
  leagueId: String,
  season: String
): IO[Unit] = for {
  playerTeams <- apiClient.fetchPlayerTeams(playerId)
} yield {
  playerTeams.response.headOption.foreach { playerData =>
    val seasonStats = playerData.statistics.find(s => s.league.id == leagueId && s.season == season)

    seasonStats match {
      case Some(stats) =>
        println(s"Player Analysis: ${playerData.player.name}")
        println(s"Team: ${stats.team.name}")
        println(s"Position: ${playerData.statistics.head.games.position.getOrElse("Unknown")}")
        println(s"Age: ${playerData.player.age.getOrElse("Unknown")}")

        println("\n--- BASIC STATS ---")
        println(f"Appearances: ${stats.games.appearances}")
        println(f"Minutes Played: ${stats.games.minutes.getOrElse(0)}")
        println(f"Lineups: ${stats.games.lineups}")
        println(f"Substitute In: ${stats.substitutes.in.getOrElse(0)}")
        println(f"Substitute Out: ${stats.substitutes.out.getOrElse(0)}")

        println("\n--- ATTACKING STATS ---")
        println(f"Goals: ${stats.goals.total}")
        println(f"Assists: ${stats.goals.assists.getOrElse(0)}")
        if (stats.goals.total.nonEmpty && stats.shots.nonEmpty) {
          val conversionRate = (stats.goals.total.toDouble / stats.shots.total) * 100
          println(f"Shot Conversion: $conversionRate%.1f%")
        }

        println("\n--- SHOOTING ---")
        if (stats.shots.nonEmpty) {
          println(f"Total Shots: ${stats.shots.total}")
          println(f"On Target: ${stats.shots.on}")
          val accuracy = if (stats.shots.total > 0) (stats.shots.on.toDouble / stats.shots.total) * 100 else 0
          println(f"Accuracy: $accuracy%.1f%")
        }

        println("\n--- DISCIPLINARY ---")
        println(f"Yellow Cards: ${stats.cards.yellow}")
        println(f"Red Cards: ${stats.cards.red}")

        // Per 90 calculations
        if (stats.games.minutes.getOrElse(0) > 0) {
          val minutes = stats.games.minutes.getOrElse(0)
          val minutesPer90 = 90.0
          val ninetyMins = minutes / minutesPer90

          println("\n--- PER 90 MINUTES ---")
          println(f"Goals per 90: ${(stats.goals.total / ninetyMins).formatted("%.2f")}")
          println(f"Assists per 90: ${(stats.goals.assists.getOrElse(0) / ninetyMins).formatted("%.2f")}")
        }

      case None =>
        println(s"No statistics found for this player in league $leagueId, season $season")
    }
  }
}
```


## Next Steps

- [Teams & Players](teams-players.md) - More team and player operations
- [Leagues](leagues.md) - League-specific statistics
- [Advanced Usage](advanced-usage.md) - Complex statistical models