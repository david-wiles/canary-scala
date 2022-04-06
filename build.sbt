ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val envVars =
  sys.props.get("CANARY_REPO")

lazy val root = (project in file("."))
  .settings(
    name := "canary-scala",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.5.1",
    libraryDependencies += "org.apache.commons" % "commons-compress" % "1.21",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test",
    assembly / assemblyJarName := "canary.jar",
  )
