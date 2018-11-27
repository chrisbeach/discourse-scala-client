name := "discourse-scala-client"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.12",
  "com.typesafe.play" %% "play-ws-standalone-json" % "1.1.12",
  "com.typesafe.play" %% "play-ws-standalone-xml" % "1.1.12",
)