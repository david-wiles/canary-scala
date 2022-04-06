ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "canary-scala",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.5.1",
    assembly / assemblyJarName := "canary.jar",
  )
