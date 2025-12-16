package com.footballsync.api.client

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class FootballApiClientExtendedSpec extends AsyncWordSpec with AsyncIOSpec with Matchers {

  "FootballApiClientExtended" should {

    "fetch timezones" in {
      val responseJson =
        """
          |{
          |  "get": "timezone",
          |  "parameters": [],
          |  "errors": [],
          |  "results": 1,
          |  "paging": { "current": 1, "total": 1 },
          |  "response": [ "Europe/London" ]
          |}
          |""".stripMargin

      val client = Client.fromHttpApp[IO](HttpApp { case GET -> Root / "timezone" =>
        Ok(responseJson)
      })

      val apiClient = new FootballApiClientImpl[IO](client, "test-key")

      apiClient.fetchTimezones().map { response =>
        response.response should have size 1
        response.response.head.value shouldBe "Europe/London"
      }
    }

    "fetch countries" in {
      val responseJson =
        """
          |{
          |  "get": "countries",
          |  "parameters": [],
          |  "errors": [],
          |  "results": 1,
          |  "paging": { "current": 1, "total": 1 },
          |  "response": [
          |    {
          |      "name": "England",
          |      "code": "GB",
          |      "flag": "https://media.api-sports.io/flags/gb.svg"
          |    }
          |  ]
          |}
          |""".stripMargin

      val client = Client.fromHttpApp[IO](HttpApp { case GET -> Root / "countries" =>
        Ok(responseJson)
      })

      val apiClient = new FootballApiClientImpl[IO](client, "test-key")

      apiClient.fetchCountries().map { response =>
        response.response should have size 1
        response.response.head.name shouldBe "England"
        response.response.head.code shouldBe Some("GB")
      }
    }

    "fetch seasons" in {
      val responseJson =
        """
          |{
          |  "get": "leagues/seasons",
          |  "parameters": [],
          |  "errors": [],
          |  "results": 1,
          |  "paging": { "current": 1, "total": 1 },
          |  "response": [ 2021 ]
          |}
          |""".stripMargin

      val client = Client.fromHttpApp[IO](HttpApp { case GET -> Root / "leagues" / "seasons" =>
        Ok(responseJson)
      })

      val apiClient = new FootballApiClientImpl[IO](client, "test-key")

      apiClient.fetchSeasons().map { response =>
        response.response should have size 1
        response.response.head.year shouldBe 2021
      }
    }

    "fetch team seasons" in {
      val responseJson =
        """
          |{
          |  "get": "teams/seasons",
          |  "parameters": { "team": "33" },
          |  "errors": [],
          |  "results": 1,
          |  "paging": { "current": 1, "total": 1 },
          |  "response": [ 2021 ]
          |}
          |""".stripMargin

      val client = Client.fromHttpApp[IO](HttpApp { case req @ GET -> Root / "teams" / "seasons" =>
        req.params.get("team") shouldBe Some("33")
        Ok(responseJson)
      })

      val apiClient = new FootballApiClientImpl[IO](client, "test-key")

      apiClient.fetchTeamSeasons("33").map { response =>
        response.response should have size 1
        response.response.head.year shouldBe 2021
      }
    }

    "fetch standings" in {
      val responseJson =
        """
          |{
          |  "get": "standings",
          |  "parameters": { "league": "39", "season": "2021" },
          |  "errors": [],
          |  "results": 1,
          |  "paging": { "current": 1, "total": 1 },
          |  "response": [
          |    {
          |      "league": {
          |        "id": 39,
          |        "name": "Premier League",
          |        "country": "England",
          |        "logo": "https://media.api-sports.io/football/leagues/39.png",
          |        "flag": "https://media.api-sports.io/flags/gb.svg",
          |        "season": 2021,
          |        "standings": [
          |          [
          |            {
          |              "rank": 1,
          |              "team": {
          |                "id": 50,
          |                "name": "Manchester City",
          |                "logo": "https://media.api-sports.io/football/teams/50.png"
          |              },
          |              "points": 93,
          |              "goalsDiff": 73,
          |              "group": "Premier League",
          |              "form": "WDWWW",
          |              "status": "same",
          |              "description": "Promotion - Champions League (Group Stage)",
          |              "all": {
          |                "played": 38,
          |                "win": 29,
          |                "draw": 6,
          |                "lose": 3,
          |                "goals": {
          |                  "for": 99,
          |                  "against": 26
          |                }
          |              },
          |              "home": {
          |                "played": 19,
          |                "win": 15,
          |                "draw": 2,
          |                "lose": 2,
          |                "goals": {
          |                  "for": 58,
          |                  "against": 15
          |                }
          |              },
          |              "away": {
          |                "played": 19,
          |                "win": 14,
          |                "draw": 4,
          |                "lose": 1,
          |                "goals": {
          |                  "for": 41,
          |                  "against": 11
          |                }
          |              },
          |              "update": "2022-05-22T00:00:00+00:00"
          |            }
          |          ]
          |        ]
          |      }
          |    }
          |  ]
          |}
          |""".stripMargin

      val client = Client.fromHttpApp[IO](HttpApp { case req @ GET -> Root / "standings" =>
        req.params.get("league") shouldBe Some("39")
        req.params.get("season") shouldBe Some("2021")
        Ok(responseJson)
      })

      val apiClient = new FootballApiClientImpl[IO](client, "test-key")

      apiClient.fetchStandings("39", "2021").map { response =>
        response.response should have size 1
        response.response.head.league.id shouldBe 39
        response.response.head.league.standings.head.head.team.name shouldBe Some("Manchester City")
      }
    }
  }
}
