# Concrete Forum Ingester
Concrete ingester supporting forum data.

## Quick start
From `ingesters/bolt`, run:
```sh
mvn clean compile assembly:single
```

Run:
```sh
java -cp target/concrete-ingesters-bolt-4.8.6-jar-with-dependencies.jar \
    edu.jhu.hlt.concrete.ingesters.bolt.BoltForumPostIngester \
    /path/to/output/folder \
    /path/to/document \
    <path/to/other/document>
```

The ingester minimally takes a path to a forum `.xml` document as the last input.
It can also support any number of `.xml` files afterwards (e.g. via `xargs`).
