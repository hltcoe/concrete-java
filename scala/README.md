Concrete Scala
========

Requirements
------------

Concrete-Scala requires the following:
* [sbt](www.scala-sbt.org/), >= 0.13.1
* [Scala](www.scala-lang.org/), >= 2.10.3 (`sbt` will get this for you)
* Current versions of `concrete-core` and `concrete-util`

Installation
------------

`cd concrete-scala
sbt +publish-local
cd ..
cd screed
sbt +publish-local`

or, to install into `~/.m2/repository`, replace `+publish-local` with `+publish` in the above code block.

Adding to your project
----------------------

Via sbt:

```scala
  libraryDependencies += "edu.jhu.hlt" %% "concrete-scala" % "2.0.7-SNAPSHOT"
```
