package com.footballsync.api.client.model.codec

import com.footballsync.model.FootballDataResponses._
import com.footballsync.model.model.eitherDecoder
import com.footballsync.model.model.eitherEncoder
import io.circe.testing.CodecTests
import org.scalactic.anyvals.PosInt
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class FootballDataResponsesCodecSpec
    extends AnyFunSuite
    with FunSuiteDiscipline
    with Checkers
    with PropertyBasedTestingInstances {

  implicit val configuration: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = PosInt(30))

  checkAll(
    "Check[Either[List[Map[String, String]], Map[String, String]]]",
    CodecTests[Either[List[Map[String, String]], Map[String, String]]].codec
  )
  checkAll("Check[SingleFixtureResponse]", CodecTests[SingleFixtureResponse].codec)
  checkAll("Check[MultiFixtureResponse]", CodecTests[MultiFixtureResponse].codec)
  checkAll("Check[TeamResponse]", CodecTests[TeamResponse].codec)
  checkAll("Check[LeagueResponse]", CodecTests[LeagueResponse].codec)
  checkAll("Check[Paging]", CodecTests[Paging].codec)
  checkAll("Check[FootballDataResponse]", CodecTests[FootballDataResponse].codec)
  checkAll("Check[ApiResponse[SingleFixtureResponse]]", CodecTests[ApiResponse[SingleFixtureResponse]].codec)
  checkAll("Check[ApiResponse[MultiFixtureResponse]]", CodecTests[ApiResponse[MultiFixtureResponse]].codec)
  checkAll("Check[ApiResponse[TeamResponse]]", CodecTests[ApiResponse[TeamResponse]].codec)
  checkAll("Check[ApiResponse[LeagueResponse]]", CodecTests[ApiResponse[LeagueResponse]].codec)
  checkAll("Check[ApiResponse[FixtureEventsResponse]]", CodecTests[ApiResponse[FixtureEventsResponse]].codec)
  checkAll("Check[ApiResponse[PlayerStatisticsResponse]]", CodecTests[ApiResponse[PlayerStatisticsResponse]].codec)
}
