name := "packt publishing downloader"

organization := "io.github.morgaroth"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-client" % "1.3.3",
  "joda-time" % "joda-time" % "2.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.11",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)
