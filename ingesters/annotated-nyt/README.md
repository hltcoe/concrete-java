# Concrete Annotated New York Times Ingester
Library used to convert the
[Annotated New York Times corpus](https://catalog.ldc.upenn.edu/LDC2008T19)
to Concrete.

## Quick start
From `ingesters/bolt`, run:
```sh
mvn clean compile assembly:single
```

### Ingesting the entire ANYT corpus
To convert the corpus to concrete, ensure you have the `LDC2008T19` corpus
untarred to a location on disk.

``` shell
LDC2008T19=/path/to/your/LDC2008T19/data
sh ingest-anyt.sh $LDC2008T19 output/
```

This creates a `.tar.gz` file for each Annotated NYT file. The
filenames contain both the year and the day, unlike the original
corpus.
