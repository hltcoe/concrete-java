# Concrete Forum Ingester
Concrete ingester supporting forum data, such as those found from
`LDC2014E13` corpus from
[TAC '14](http://www.nist.gov/tac/2014/KBP/data.html).

## Quick start
From `ingesters/bolt`, run:
```sh
mvn clean compile assembly:single
```

### Ingesting many BOLT forum posts
``` shell
BOLT_POSTS=/path/to/many/bolt/.xml
sh ingest-bolt.sh $BOLT_POSTS output/
```

The ingester minimally takes a path to a forum `.xml` document as the
last inputs.  It can also support any number of `.xml` files
afterwards (e.g. via `xargs`).

This ingester creates a single `.tar.gz` that contains all
BOLT forum posts that are found in the given path.
