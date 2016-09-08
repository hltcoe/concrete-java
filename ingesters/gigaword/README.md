# Concrete Gigaword Ingester
![Maven Badges](https://maven-badges.herokuapp.com/maven-central/edu.jhu.hlt/concrete-ingesters-gigaword/badge.svg)
[![javadoc.io](https://javadocio-badges.herokuapp.com/edu.jhu.hlt/concrete-ingesters-gigaword/badge.svg)](http://www.javadoc.io/doc/edu.jhu.hlt/concrete-ingesters-gigaword/)

Concrete ingester supporting the
[Gigaword](https://catalog.ldc.upenn.edu/LDC2003T05) corpus.  This
library provides the ability to ingest LDC SGML/XML
documents into Concrete Communication objects.

## Quick start
From `ingesters/gigaword`, run:
```sh
mvn clean compile assembly:single
```

### Ingest all LDC .gz files
See [this script](ingest-gw.sh). Ensure the Gigaword corpus
is available on disk.

The second argument is an output directory.

``` shell
LDC2003T05=/path/to/your/LDC2003T05
sh ingest-gw.sh $LDC2003T05 output/
```

This creates a single `.tar.gz` file for each `.gz` file
in the Gigaword corpus.

### Ingest individual .sgml files

```sh
java -cp target/*.jar \
    edu.jhu.hlt.concrete.ingesters.gigaword.GigawordDocumentConverter \
    /path/to/ldc/sgml/file \
    /path/to/output/file
```

### Ingest many .sgml files
There is an ingester capable of taking in many `.sgml` paths
from `xargs`. e.g.,

``` shell
find /your/.sgml/files | xargs \
    java -cp target/*.jar \
        edu.jhu.hlt.concrete.ingesters.gigaword.GigawordBatchDocumentConverter \
        $F \
        output/
```
