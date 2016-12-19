# Simple Accumulo

Simpleaccumulo is a library implementing key-value Communication
storage with user-specified namespaces in accumulo.
Some [concrete services](https://gitlab.hltcoe.jhu.edu/concrete/concrete-services)
like `FetchCommunicationService` and `StoreCommunicationService` are
implemented.  Other tools, such as a tar.gz-to-accumulo ingester,
are also provided.

Simpleaccumulo does not aim to be a one-stop-shop for concrete in
accumulo.  Rather, it handles common problems like authentication,
implements basic services, provides examples, and lays out a simple
schema such that users needing more functionality can readily extend
simpleaccumulo to accomplish their individual needs.

## Dependencies

Simpleaccumulo requires Java 1.8 and Maven 3.2 or later.

## Installation

To build simpleaccumulo into an ``uberjar,'' do:

```bash
mvn clean compile assembly:single
```

## Usage

Note: command-line usage is subject to change.

### Configuration

Java Properties are used for all configuration, including accumulo
connection parameters as well as ingest paths, fetch/store service
ports, etc.  Properties can be written to a configuration file or
specified individually on the command-line; we
note the former (configuration file) is much more
secure, as the command line of a running process can be seen by all
users of a shared system.

A barebones configuration file is provided at
`simpleaccumulo.properties.example`.  Copy it to
`simpleaccumulo.properties` and replace the user, password, and
namespace with the real values.

### Ingest

`SimpleAccumuloIngester` reads tar.gz Communication archives and
puts them into accumulo.  If you want to ingest all tar.gz files in
`/mnt/data`, for example, add the following two lines to
`simpleaccumulo.properties`:

```
communications.root=/mnt/data
communications.glob=glob:/mnt/data/*.tar.gz
```

now run `SimpleAccumuloIngester`:

```bash
java -Dconfig.file=simpleaccumulo.properties -cp 'target/*' \
    edu.jhu.hlt.concrete.simpleaccumulo.SimpleAccumuloIngester
```

### Fetch service

`SimpleAccumuloFetch` implements the concrete fetch service.
To run fetch on port 39997, for example, add the following line to
`simpleaccumulo.properties`:

```
port=39997
```

now run `SimpleAccumuloFetch`:

```bash
java -Dconfig.file=simpleaccumulo.properties -cp 'target/*' \
    edu.jhu.hlt.concrete.simpleaccumulo.SimpleAccumuloFetch
```

### Store service

`SimpleAccumuloStore` implements the concrete fetch service.
To run fetch on port 39998, for example, add the following line to
`simpleaccumulo.properties`:

```
port=39998
```

now run `SimpleAccumuloStore`:

```bash
java -Dconfig.file=simpleaccumulo.properties -cp 'target/*' \
    edu.jhu.hlt.concrete.simpleaccumulo.SimpleAccumuloStore
```

## More advanced usage

### Schema

The schema is a homogenous key-value store in the `simple_accumulo_dev`
table.  The value is a thrift-serialized Communication
(serialized using the compact protocol).
As explained in the [accumulo user manual](https://accumulo.apache.org/1.8/accumulo_user_manual.html#_data_model),
a key is comosed of a row and a column.  In simpleaccumulo the row
is a Communication id.  An accumulo column is composed of a family and
qualifier; simpleaccumulo uses user-specified column families as
namespaces (see next section) and a single hard-coded qualifier.
(You can add other qualifiers yourself,
using the Accumulo API, if you want to store communication information
separate from the blob.)

### Namespaces

Column families are user-specified strings, referred to in
simpleaccumulo as *namespaces* (note these are distinct from *accumulo*
namespaces), which provide isolation.
For instance, if I want to have my own collection of gigaword documents
and ensure that I won't overwrite someone elses data (remember we would have
to be very careful to ensure that `NYT_ENG_20090525.0007` only shows up once
anywhere in accumulo, lest we get possibly un-intended over-writes),
I can use the column family `twolfe-cag`
(this is just like having a folder named `twolfe-cag`).
Writes to `(NYT_ENG_20090525.0007, twolfe-cag)` will not conflict with
writes/reads to/from `(NYT_ENG_20090525.0007, vandurme-class-project)`.

Namespacing introduces the possibility of duplication.
We do not want to have 35 copies of concretely-annotated gigaword in
accumulo, or even 3 copies of twitter.
Before adding a dataset, ask others what already exists; note accumulo
is fast but space-constrained.

## What's currently in accumulo on the mini-grid

The column family/namespace `twolfe-cag1` contains all 8,739,092 Communications in
`/export/projects/fferraro/cag-4.6.10/processing/from-marcc/20161012-083257/gigaword-merged/tgz`

The column family/namespace `twolfe-cawiki-en1` contains ??? Communications in
`/export/projects/fferraro/cag-4.6.10/processing/wiki/semafor-processing/wiki-merged-fixed/tgz/en-wiki-articles.tar.gz`

