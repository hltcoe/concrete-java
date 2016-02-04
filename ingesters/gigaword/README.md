# Concrete Gigaword Ingester
Concrete ingester supporting the [Gigaword](https://github.com/hltcoe/concrete) corpus.
This library provides the ability to ingest to ingest LDC SGML documents into Concrete
Communication objects.

## Quick start
From `ingesters/gigaword`, run:
```sh
mvn clean compile assembly:single
```

Run:
```sh
java -cp target/concrete-ingesters-gigaword-4.8.5-jar-with-dependencies.jar \
    edu.jhu.hlt.concrete.ingesters.gigaword.GigawordDocumentConverter \
    /path/to/ldc/sgml/file \
    /path/to/output/file
```
