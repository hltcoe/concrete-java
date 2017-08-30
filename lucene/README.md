Concrete Lucene
===

Provides library and main methods for creating and searching
a Lucene index over Communication objects.

## Communication requirements

`Communication` objects must have the following:

* a set `text` field
* `Section`s
* `Sentence`s with valid `TextSpan`s

If using the components that support pretokenization, the `Sentence` objects must
additionally have `Tokenization` set.

## As a library

To build an index using Lucene's default tokenization, use `LuceneCommunicationIndexer`:

```java
try (LuceneCommunicationIndexer indexer = new NaiveConcreteLuceneIndexer(directoryPath)) {
    indexer.add(comm1);
    indexer.add(comm2);
}
```

To search over the index using `ConcreteLuceneSearcher`:
```java
try (ConcreteLuceneSearcher search = new ConcreteLuceneSearcher(directoryPath)) {
    List<Document> docs = search.searchDocuments("Canada", 50);
    System.out.println(docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));
}
```

### With pretokenized communications

Use `TokenizedCommunicationIndexer` and `TokenizedCommunicationSearcher`.
The searcher uses a whitespace tokenizer on the query strings.


## As an executable project

This assumes you are using the standard lucene analyzer with its tokenization.

### Build

``` shell
mvn clean compile assembly:single
```

### Create an index

The `NaiveConcreteLuceneIndexer` requires 
Given a `.tar.gz` file of Communication objects that satisfy the
above, run the following:

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

## What you need to know about Lucene
Lucene modifies the text to be indexed and the query text.
These modifications are performed with an `Analyzer`.
Compatible analyzers need to be used for index or search.
The default analyzer is StandardAnalyzer which has its own stop word list, lower cases the text,
and tokenizes using an implementation of the [Unicode text segmentation](http://unicode.org/reports/tr29/).
This tokenizer treats Chinese text as unigrams.
Lucene provides many different tokenizers and filters for building analyzers.
Explore their [repo](https://github.com/apache/lucene-solr/tree/master/lucene/analysis/common/src/java/org/apache/lucene/analysis) for more information.

The pre-tokenization code lower cases the text and removes a small set of English stop words.
