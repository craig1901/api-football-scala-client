ThisBuild / scalaVersion := "2.13.15"

inThisBuild(
  List(
    homepage := Some(url("https://github.com/craig1901/api-football-scala-client")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "craig1901",
        "Craig Nolan",
        "craig.nolan@example.com",
        url("https://github.com/craig1901")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/craig1901/api-football-scala-client"),
        "scm:git:git@github.com:craig1901/api-football-scala-client.git"
      )
    )
  )
)

val circeVersion = "0.14.1"
val http4sVersion = "0.23.26"
val catsEffectVersion = "3.3.12"
val log4catsVersion = "2.7.0"
val scalatestVersion = "3.2.18"
val disciplineScalatestVersion = "2.3.0"
val scalatestPlusScalaCheckVersion = "3.2.19.0"
val catsEffectTestingVersion = "1.5.0"
val catsRetryVersion = "3.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "api-football-scala-client",
    organization := "io.github.craig1901",
    sonatypeCredentialHost := "central.sonatype.com",
    sonatypeRepository := "https://central.sonatype.com/api/v1/publisher/deployments/maven2",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-testing" % circeVersion,
      "org.typelevel" %% "log4cats-core" % log4catsVersion,
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "com.github.cb372" %% "cats-retry" % catsRetryVersion,

      // Test dependencies
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.typelevel" %% "discipline-scalatest" % disciplineScalatestVersion % Test,
      "org.scalatestplus" %% "scalacheck-1-18" % scalatestPlusScalaCheckVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestingVersion % Test,
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.18" % "1.3.2" % Test,
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    ),
    scalacOptions ++= Seq(
      "-Wunused"
    )
  )
