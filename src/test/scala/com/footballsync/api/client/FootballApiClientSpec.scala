package com.footballsync.api.client

import _root_.com.footballsync.api.client.model.codec.PropertyBasedTestingInstances
import _root_.com.footballsync.model.FootballDataResponses._
import cats.effect._
import cats.effect.unsafe.implicits.global
import io.circe.Encoder
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import FootballApiClientSpec._
import FootballApiClientImpl._

class FootballApiClientSpec extends AnyFlatSpec with Matchers with PropertyBasedTestingInstances with EitherValues {

  behavior of "FootballApiClient"

  it should "fetch multiple fixtures" in {
    testApiClient[MultiFixtureResponse](fixturesPath, _.fetchFixtures("39", "2022"))
  }

  it should "fetch a single fixture" in {
    testApiClient[SingleFixtureResponse](fixturesPath, _.fetchSingleFixture("1"))
  }

  it should "fetch multiple single fixtures by ids" in {
    testApiClient[SingleFixtureResponse](fixturesPath, _.fetchSingleFixtures(List("1", "2", "3")))
  }

  it should "fetch fixture events" in {
    testApiClient[FixtureEventsResponse](fixtureEventsPath, _.fetchFixtureEvents("1"))
  }

  it should "fetch a single team" in {
    testApiClient[TeamResponse](teamsPath, _.fetchTeam("1"))
  }

  it should "fetch multiple teams for a particular league season" in {
    testApiClient[TeamResponse](teamsPath, _.fetchTeams("39", "2022"))
  }
  
  it should "fetch multiple teams for a particular country" in {
    testApiClient[TeamResponse](teamsPath, _.fetchTeamsByCountry("Ireland"))
  }

  it should "fetch leagues" in {
    testApiClient[LeagueResponse](leaguesPath, _.fetchAllLeagues())
  }

  it should "fetch players" in {
    testApiClient[PlayerStatisticsResponse](playersPath, _.fetchPlayersStream("39", "2022").compile.toList.map(_.head))
  }

  it should "retry requests up to a max retry amount" in {
    testApiClient[LeagueResponse](leaguesPath, _.fetchAllLeagues())
    testApiClient[LeagueResponse](leaguesPath, _.fetchAllLeagues())
    testApiClient[LeagueResponse](leaguesPath, _.fetchAllLeagues())
  }

  private def testApiClient[T <: FootballDataResponse: Arbitrary: Encoder](
    endpoint: String,
    fetchFunction: FootballApiClientImpl[IO] => IO[ApiResponse[T]]
  ): Assertion = {
    val response: ApiResponse[T] = PropertyBasedTestingInstances.generate[ApiResponse[T]]
    val apiKey: String = arbitraryApiKey

    val mockHttpApp: HttpApp[IO] = HttpApp[IO] {
      case req @ (GET -> _) if req.uri.renderString.contains(endpoint) =>
        Ok(response.asJson)
    }

    val mockFootballDataServer: Client[IO] = Client.fromHttpApp(mockHttpApp)
    val apiClient: FootballApiClientImpl[IO] = new FootballApiClientImpl[IO](mockFootballDataServer, apiKey)
    val clientResponseAttempt: Either[Throwable, ApiResponse[T]] = fetchFunction(apiClient).attempt.unsafeRunSync()

    val clientResponse = clientResponseAttempt.right.value
    clientResponse.response should have size response.response.size
    clientResponse.response shouldBe response.response
    clientResponse.get shouldBe response.get
    clientResponse.errors shouldBe response.errors
    clientResponse.paging shouldBe response.paging
    clientResponse.parameters shouldBe response.parameters
    clientResponse.results shouldBe response.results

  }
}

object FootballApiClientSpec {

  implicit private def arbitraryApiKey: String = {
    Arbitrary(
      for {
        length <- Gen.choose(32, 128)
        chars <- Gen.listOfN(length, Gen.alphaNumChar)
      } yield chars.mkString
    )
  }.arbitrary.pureApply(Gen.Parameters.default, PropertyBasedTestingInstances.seed)
}
