val libName = "frontend-bootstrap"

lazy val library = Project(libName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    majorVersion := 11,
    makePublicallyAvailableOnBintray := true,
    scalaVersion := "2.11.11",
    libraryDependencies ++= LibDependencies.compile ++ LibDependencies.test,
    crossScalaVersions := Seq("2.11.11"),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    )
  )
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
