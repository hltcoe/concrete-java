Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights
reserved.  This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete Java
========

Dependencies
----------

    <dependency>
      <groupId>edu.jhu.hlt</groupId>
      <artifactId>concrete-core</artifactId>
      <version>4.2.3</version>
    </dependency>

    <dependency>
      <groupId>edu.jhu.hlt</groupId>
      <artifactId>concrete-util</artifactId>
      <version>4.2.3</version>
    </dependency>

TLDR Install Guide (*nix)
---------

You will need `thrift` installed and available on your `PATH`, version `0.9.1`.

```bash
git clone git@github.com:hltcoe/concrete.git
git clone git@github.com:hltcoe/concrete-java.git
cd concrete
export THRIFT_SOURCE_DIR=`pwd`
cd ../concrete-java
mvn install -Dthrift.sources="$THRIFT_SOURCE_DIR/thrift"
```

Requirements
------------

Concrete-Java requires the following:
* Java, 1.7 or greater. If you just want to build the thrift classes, however, only 1.6 is required.
* [Apache Maven](http://maven.apache.org/), 3.0.4 or greater
* [Apache Thrift](http://thrift.apache.org/), 0.9.1
