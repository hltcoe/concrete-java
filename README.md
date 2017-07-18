Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights
reserved. See LICENSE in the project root directory.

Concrete Java
========
Java libraries for the [Concrete](https://github.com/hltcoe/concrete) HLT data schema.

JavaDoc API documentation is hosted on
[javadoc.io](http://www.javadoc.io/doc/edu.jhu.hlt/concrete-core)

## How do I regenerate the thrift java files?

Call `generate.sh`, where the first and only argument is the
path to the `thrift` files from `concrete`.

In other words, if you have just ran:

``` shell
git clone ...concrete.git
```

the command would be (in the same directory)

``` shell
./generate.sh concrete/thrift
```

Be aware that you'll need Thrift `0.9.3` installed
and in your `$PATH`.

## How do I run the simple ingesters?
Consult the [README.md](ingesters/simple/README.md) for the simple ingesters project.

## Building
Ensure that you run maven commands with 'clean', otherwise things may fail.

Maven Dependencies
----------

See subprojects for latest versions.

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

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-ingesters-simple</artifactId>
  <version>x.y.z</version>
</dependency>
```

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-ingesters-gigaword</artifactId>
  <version>x.y.z</version>
</dependency>
```

```xml
<dependency>
  <groupId>edu.jhu.hlt</groupId>
  <artifactId>concrete-ingesters-alnc</artifactId>
  <version>x.y.z</version>
</dependency>
```
