import sbt.Keys.{version, _}
import sbt._

object HmrcBuild extends Build {

  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.versioning.SbtGitVersioning

  val appName = "frontend-bootstrap"

  val appDependencies = Dependencies.compile ++ Dependencies.test


  lazy val library = Project(appName, file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      scalaVersion := "2.11.7",
      libraryDependencies ++= appDependencies,
      crossScalaVersions := Seq("2.11.7"),
      resolvers := Seq(
        Resolver.bintrayRepo("hmrc", "releases"),
        Resolver.bintrayRepo("hmrc", "release-candidates"),
        Resolver.typesafeRepo("releases"),
        Resolver.jcenterRepo
      ),
      version := "100.1-SNAPSHOT"
      
  ).disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

}

object Dependencies {

  import play.sbt.PlayImport._
  import play.core.PlayVersion

  val compile = Seq(
    filters,
    "uk.gov.hmrc" %% "crypto" % "4.4.0",
    "uk.gov.hmrc" %% "play-auditing" % "100.0-SNAPSHOT",
    "uk.gov.hmrc" %% "http-verbs" % "7.0.0",
    "uk.gov.hmrc" %% "http-verbs-play-25" % "0.6.0",
    "uk.gov.hmrc" %% "play-graphite" % "3.2.0",
    "ch.qos.logback" % "logback-core" % "1.1.7",
    "uk.gov.hmrc" %% "logback-json-logger" % "3.1.0",
    "uk.gov.hmrc" %% "govuk-template" % "5.3.0",
    "uk.gov.hmrc" %% "play-config" % "4.3.0",
    "uk.gov.hmrc" %% "play-health" % "2.1.0",
    "uk.gov.hmrc" %% "play-ui" % "7.4.0",
    "com.typesafe.play" %% "play" % PlayVersion.current,
    "de.threedimensions" %% "metrics-play" % "2.5.13"
  )

  val test = Seq(
    "com.typesafe.play" %% "play-test" % PlayVersion.current % "test",
    "com.typesafe.play" %% "play-specs2" % PlayVersion.current % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.pegdown" % "pegdown" % "1.5.0" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
  )

}
