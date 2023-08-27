ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.12"

lazy val root = (project in file("."))
  .settings(
    name := "ny-taxi-weather-influence",
    version := "1.0.0",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "2.4.5",
      "org.apache.spark" %% "spark-sql" % "2.4.5",
      "org.apache.spark" %% "spark-mllib" % "2.4.5",
      "org.apache.spark" %% "spark-streaming" % "2.4.5"
    )
  )
