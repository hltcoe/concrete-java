Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights
reserved.  This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete Java
========

Requirements
------------

Concrete-Java requires the following:
* Java, 1.6 or greater
* [Apache Maven](http://maven.apache.org/), 3.0.4 or greater
* [Apache Thrift](http://thrift.apache.org/)

Installation
------------

Note: by default, the thrift plugin uses:

    /usr/local/bin/thrift

to locate the thrift executable. If you have installed this in a different location, update

    <thrift.exe>

in the properties of core/pom.xml.

First, checkout our latest code:

    git clone git@github.com:hltcoe/concrete.git

Running

    cd java
    mvn install

will install the plugin to your local repository.

Adding to your project
----------------------

This plugin creates compiled Java classes that reflect our Thrift
definitions. You can use these in your java code by adding the following
dependency to your project's pom.xml file, once installed or deployed:

    <dependency>
      <groupId>edu.jhu.hlt</groupId>
      <artifactId>concrete-core</artifactId>
      <version>2.0.3-SNAPSHOT</version>
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
