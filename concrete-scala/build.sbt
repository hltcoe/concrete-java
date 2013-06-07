name := "Concrete Scala Code"

version := "1.0.0-SNAPSHOT"

organization := "edu.jhu.hltcoe"

organizationHomepage := Some(url("http://hltcoe.jhu.edu"))

scalaVersion := "2.10.1"

scalacOptions := Seq("-encoding", "utf8",
  "-target:jvm-1.7")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "edu.jhu.hlt.concrete" % "concrete-protobufs" % "1.0.6",
  "edu.jhu.hlt.concrete" % "concrete-java" % "1.0.4",
  "com.google.protobuf" % "protobuf-java" % "2.5.0",
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "com.typesafe.akka" %% "akka-kernel" % "2.1.4",
  "com.typesafe.akka" %% "akka-remote" % "2.1.4",
  "com.typesafe" % "config" % "1.0.1",
  "com.basho.riak" % "riak-client" % "1.1.1"
)

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Spray Repository" at "http://repo.spray.io/",
  "COE Lib Releases" at "http://test1:8081/artifactory/libs-release-local",
  "COE Lib Snapshots" at "http://test1:8081/artifactory/libs-snapshot-local",
  "COE External Releases" at "http://test1:8081/artifactory/ext-release-local"
)

initialCommands in console += {
  List("import edu.jhu.hlt.concrete._").mkString("\n")
}

licenses := Seq("BSD 2-Clause License" -> url("https://raw.github.com/hltcoe/concrete/master/LICENSE.md"))

homepage := Some(url("https://github.com/hltcoe/concrete"))


