ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / nativeLinkStubs := true

lazy val root = (project in file("."))
  .settings(
    name := "canary-scala",
    libraryDependencies += "com.softwaremill.sttp.client3" %%% "core" % "3.5.1"
  )
  .enablePlugins(ScalaNativePlugin)
