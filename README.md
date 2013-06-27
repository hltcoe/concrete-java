Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete
========
[![Build Status](https://travis-ci.org/hltcoe/concrete.png)](https://travis-ci.org/hltcoe/concrete)

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
* Maven, 3.0.4 or greater
* Google protocol buffers, v. 2.5.0
  https://code.google.com/p/protobuf/

Installation
------------

First, checkout our latest tag:

    git checkout p1.0.8_j1.0.7

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
      <artifactId>concrete-protobufs</artifactId>
      <version>1.0.8</version>
    </dependency>

    <dependency>
      <groupId>edu.jhu.hlt.concrete</groupId>
      <artifactId>concrete-java</artifactId>
      <version>1.0.7</version>
    </dependency>

At this time, we do not have this hosted on a public maven server. 

Using the code in your project
------------------------------

Compiled java classes end up in the edu.jhu.hlt.concrete package. The protocol
buffers generate many classes; additional technical documentation can be found
in the comments of the protocol buffer definitions themselves.
