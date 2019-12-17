name := "discourse-scala-client"

version := "0.2"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.3",
  "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.3",
  "com.typesafe.play" %% "play-ws-standalone-xml" % "2.0.3",
  "com.typesafe" % "config" % "1.3.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-effect" % "2.0.0"
)