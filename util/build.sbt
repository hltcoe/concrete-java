name := "concrete-util"

version := "2.0.3-SNAPSHOT"

organization := "edu.jhu.hlt"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "edu.jhu.hlt" % "concrete-core" % "2.0.3-SNAPSHOT"
)

resolvers += "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
