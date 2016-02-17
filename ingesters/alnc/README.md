# Concrete ALNC Ingester
An ingester developed to support ingest of the yet to be released ALNC corpus.

## Quick start
From `ingesters/bolt`, run:
```sh
mvn clean compile assembly:single
```

### Ingesting the ALNC corpus
``` shell
PATH_TO_ALNC_BZIP=/path/to/alnc/.bz2
sh ingest-alnc.sh $PATH_TO_ALNC_BZIP output/
```

This creates a `.tar.gz` file with a `Communication`
for each JSON object in the given `.bz2` file.
