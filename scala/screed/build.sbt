name := "screed"

version := "1.0.0-SNAPSHOT"

organization := "edu.jhu.hlt"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.10" % "test",
  "edu.jhu.hlt" %% "concrete-scala" % "2.0.7-SNAPSHOT",
  "edu.jhu.hlt" % "concrete-util" % "2.0.7-SNAPSHOT"
)

resolvers += "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
