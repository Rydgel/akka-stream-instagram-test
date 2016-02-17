name := "akka-stream-instagram"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions += "-feature"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2-RC3",
  "com.github.Rydgel" %% "scalagram" % "0.2.0",
  "org.scalaz" %% "scalaz-core" % "7.2.0"
)