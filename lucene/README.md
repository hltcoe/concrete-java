Concrete Lucene
===

Provides library and main methods for creating and searching
a Lucene index over Communication objects.

## As an executable project

### Build

``` shell
mvn clean compile assembly:single
```

### Create an index

Given a `.tar.gz` file of Communication objects, run the following:

``` shell
java -cp target/*.jar \
    edu.jhu.hlt.concrete.lucene.TarGzCommunicationIndexer \
    --input-path /your/comms.tar.gz \
    --output-folder /a/folder/for/the/index
```

Use the `--help` flag for all parameters.

### Search over the index

Given a folder with a Lucene index built, run the following:

``` shell
java -cp target/*.jar \
    edu.jhu.hlt.concrete.lucene.ConcreteLuceneSearcher \
    --index-path /a/folder/for/the/index \
    "your query terms"
```

Use the `--help` flag for all parameters.

## As a library

Coming soon. For now, look at the source code of the
[naive implementation](src/main/java/edu/jhu/hlt/concrete/lucene/NaiveConcreteLuceneIndexer.java),
as well as the code for
[querying the index](src/main/java/edu/jhu/hlt/concrete/lucene/ConcreteLuceneSearcher.java).
