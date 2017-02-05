name := "akka-http-backend"

version := "1.0"

scalaVersion := "2.11.8"

Revolver.settings

fork in reStart := true

resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
)

libraryDependencies ++= {
  val akkaVersion = "2.4.16"
  val akkaHttpVersion = "10.0.3"
  val Json4sVersion = "3.5.0"
  val scalaTestVersion = "3.0.1"

  Seq(
    "org.scalatest"     %% "scalatest" % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % "1.6.0",
    "org.json4s" %% "json4s-native" % Json4sVersion,
    "org.json4s" %% "json4s-ext" % Json4sVersion,
    "ch.qos.logback"    %  "logback-classic"  % "1.1.8",
    "joda-time"         % "joda-time" % "2.9.7",
    "ch.megard" %% "akka-http-cors" % "0.1.11",
    "com.googlecode.concurrentlinkedhashmap"  %   "concurrentlinkedhashmap-lru" % "1.4.2"
  )
}

//fork in run := true