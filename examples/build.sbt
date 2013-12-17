name := "Concrete Scala Examples"

version := "2.0.0-SNAPSHOT"

organization := "edu.jhu.hlt"

libraryDependencies ++= Seq(
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "edu.jhu.hlt.concrete" % "concrete-core" % "2.0.0-SNAPSHOT",
  "edu.jhu.hlt.concrete" % "concrete-util" % "2.0.0-SNAPSHOT",
  "edu.jhu.hlt.tift" % "tift" % "2.0.0-SNAPSHOT"
)

resolvers += "Local Maven Repository" at "file:///"+Path.userHome+"/.m2/repository"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
