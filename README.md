Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights
reserved.  This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete Java
========

TLDR Install Guide
---------
```bash
git clone ssh://git@gitlab.hltcoe.jhu.edu:12321/concrete/concrete-java.git
git clone ssh://git@gitlab.hltcoe.jhu.edu:12321/concrete/concrete.git
cd concrete
export THRIFT_SOURCE_DIR=`pwd`
cd ../concrete-java
mvn install -Dthrift.sources="$THRIFT_SOURCE_DIR/thrift"
```

Requirements
------------

Concrete-Java requires the following:
* Java, 1.8 or greater. If you just want to build the thrift classes, however, only 1.6 is required.
* [Apache Maven](http://maven.apache.org/), 3.0.4 or greater
* [Apache Thrift](http://thrift.apache.org/), 0.9.1

Installation
------------

You will need `thrift` installed and available on your `PATH`.

Additionally, you will need a copy of `concrete-thrift` checked
out. This can be obtained by the following commands (which may/not
work based on your directory setup):

```bash
git clone ssh://git@gitlab.hltcoe.jhu.edu:12321/concrete/concrete-java.git
git clone ssh://git@gitlab.hltcoe.jhu.edu:12321/concrete/concrete.git
cd concrete
export THRIFT_SOURCE_DIR=`pwd`
cd ../concrete-java
mvn install -Dthrift.sources="$THRIFT_SOURCE_DIR/thrift"
```

will install the plugin to your local `mvn` repository.

Adding to your project
----------------------

This plugin creates compiled Java classes that reflect our Thrift
definitions. You can use these in your java code by adding the following
dependency to your project's pom.xml file, once installed or deployed:

    <dependency>
      <groupId>edu.jhu.hlt</groupId>
      <artifactId>concrete-core</artifactId>
      <version>4.2.1</version>
    </dependency>

    <dependency>
      <groupId>edu.jhu.hlt</groupId>
      <artifactId>concrete-util</artifactId>
      <version>4.2.1</version>
    </dependency>

At this time, we do not have this hosted on a public maven server.

Using the code in your project
------------------------------

Compiled java classes end up in the edu.jhu.hlt.concrete package. The
Thrift structures generate many classes; additional technical
documentation can be found in the comments of the thrift definitions
themselves.

Additionally, you can browse the javadocs located in the
`target/apidocs` directory once the `mvn install` command has been
run.
