# Concrete webposts ingester
![Maven Badges](https://maven-badges.herokuapp.com/maven-central/edu.jhu.hlt/concrete-ingesters-webposts/badge.svg)
[![javadoc.io](https://javadocio-badges.herokuapp.com/edu.jhu.hlt/concrete-ingesters-webposts/badge.svg)](http://www.javadoc.io/doc/edu.jhu.hlt/concrete-ingesters-webposts/)

Concrete ingester supporting ingesting webposts, such as those found
from `LDC2014E13` corpus from
[TAC '14](http://www.nist.gov/tac/2014/KBP/data.html).

## Quick start
From `ingesters/gigaword`, run:
```sh
mvn clean compile assembly:single
```

### Ingesting many individual .xml documents
``` shell
XML_PATH=/path/to/webpost/.xmls
sh ingest-webposts.sh $XML_PATH output/
```

This ingester creates a single file, `webposts.tar.gz`, that contains
a `Communication` for each webpost in the given path.
