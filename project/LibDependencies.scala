import sbt._

object LibDependencies {

  import play.sbt.PlayImport._
  import play.core.PlayVersion

  val akkaVersion = "2.5.18"
  
  val compile = Seq(
    filters,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "uk.gov.hmrc"                    %% "crypto"                   % "4.4.0",
    "uk.gov.hmrc"                    %% "play-auditing"            % "3.14.0-play-25-SNAPSHOT",
    "uk.gov.hmrc"                    %% "http-verbs"               % "8.10.0-play-25-SNAPSHOT",
    "uk.gov.hmrc"                    %% "play-graphite"            % "4.4.0-SNAPSHOT",
    "uk.gov.hmrc"                    %% "play-authorised-frontend" % "7.0.0-7-g0219a51",
    "ch.qos.logback"                 % "logback-classic"           % "1.2.3",
    "uk.gov.hmrc"                    %% "logback-json-logger"      % "3.1.0",
    "uk.gov.hmrc"                    %% "govuk-template"           % "5.3.0",
    "uk.gov.hmrc"                    %% "play-config"              % "7.0.0-1-gbc31795",
    "uk.gov.hmrc"                    %% "play-health"              % "2.1.0",
    "uk.gov.hmrc"                    %% "play-ui"                  % "7.21.0",
    "com.typesafe.play"              %% "play"                     % PlayVersion.current,
    "com.ning"                       % "async-http-client"         % "1.8.15",
    "de.threedimensions"             %% "metrics-play"             % "2.5.13",
    "com.fasterxml.jackson.core"     % "jackson-core"              % "2.9.7",
    "com.fasterxml.jackson.core"     % "jackson-databind"          % "2.9.7",
    "com.fasterxml.jackson.core"     % "jackson-annotations"       % "2.9.7",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8"     % "2.9.7",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310"   % "2.9.7"
  )

  val test = Seq(
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current % "test",
    "com.typesafe.play"      %% "play-specs2"        % PlayVersion.current % "test",
    "org.scalatest"          %% "scalatest"          % "2.2.4"             % "test",
    "org.pegdown"            % "pegdown"             % "1.5.0"             % "test",
    "org.mockito"            % "mockito-all"         % "1.9.5"             % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1"             % "test"
  )

}
