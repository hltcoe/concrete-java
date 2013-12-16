Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete
========

Introduction
------------

Concrete is an attempt to map out various NLP data types in a
protocol buffer schema for use in projects across Johns Hopkins University.
This standardized schema allows researchers to use a common, underlying data
model for all NLP tasks, and thus, facilitating integration between projects.

Requirements
------------

Concrete requires the following:
* Java, 1.7 or greater
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

    mvn install

will install the plugin to your local repository.

Adding to your project
----------------------

This plugin creates compiled Java classes that reflect our protocol buffer
definitions. You can use these in your java code by adding the following
dependency to your project's pom.xml file, once installed or deployed:

    <dependency>
      <groupId>edu.jhu.hlt.concrete</groupId>
      <artifactId>concrete-core</artifactId>
      <version>2.0.0-SNAPSHOT</version>
    </dependency>

At this time, we do not have this hosted on a public maven server.

Using the code in your project
------------------------------

Compiled java classes end up in the edu.jhu.hlt.concrete package. The protocol
buffers generate many classes; additional technical documentation can be found
in the comments of the protocol buffer definitions themselves.
