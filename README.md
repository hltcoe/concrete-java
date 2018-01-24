Copyright 2012-2017 Johns Hopkins University HLTCOE.
All rights reserved. See LICENSE in the project root directory.

Concrete Java
=============
Java libraries for the [Concrete](https://github.com/hltcoe/concrete) HLT data schema.

JavaDoc API documentation is hosted on
[javadoc.io](http://www.javadoc.io/doc/edu.jhu.hlt/concrete-core)

Generating Thrift Java files
----------------------------
Call `generate.sh`, where the first and only argument is the
path to the `thrift` files from `concrete`.

As an example, if the concrete repo and this repo are in the same directory, run:
```shell
./generate.sh ../concrete/thrift
```

Be aware that you'll need Thrift `0.10.0` installed and in your `$PATH`.

Building and Installing
-----------------------
Maven is used to build concrete-java:
```shell
mvn clean package
```

To install the jars into your local maven repository, run:
```shell
mvn clean install
```

Using an IDE
--------------
If you are using an IDE such as Eclipse or IntelliJ, you are likely getting
many build errors because some modules use FreeBuilder.
See the [FreeBuilder readme](https://github.com/google/FreeBuilder#build-tools-and-ides) for instructions on configuring your IDE.

Maven Dependencies
----------
See the pom.xml file for the current version.

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-core</artifactId>
  <version>x.y.z</version>
</dependency>
```

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-safe</artifactId>
  <version>x.y.z</version>
</dependency>
```

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-util</artifactId>
  <version>x.y.z</version>
</dependency>
```

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-validation</artifactId>
  <version>x.y.z</version>
</dependency>
```
