# Simple Accumulo
This module is a minimal implementation of key-value Communication storage
through accumulo which supports user-specified namespaces.
Some [concrete-services](https://gitlab.hltcoe.jhu.edu/concrete/concrete-services)
like `FetchCommunicationService` and `StoreCommunicationService` are
implemented. `SimpleAccumuloIngester` reads tar.gz Communication archives and
puts them into accumulo is also included. To use the ingester you will need
credentials for an account which has write permissions (not included).

### Schema
The schema is a homogenouse key-value store using one table (see
`SimpleAccumuloConfig.DEFAULT_TABLE`).  Values are thrift-serialized
Communications (compact protocol).
[Recall from accumulo](https://accumulo.apache.org/1.8/accumulo_user_manual.html#_data_model),
keys are comosed of rows and columns.  Rows are Communication ids.  Columns are
comprised of a family and qualifier, but this module only uses one hard-coded
qualifier (in principle you could add others if you want to store other things about
a communication other than the thrift-serialized Communication itself).

### Namespaces and Isolation
Column families are user-specified strings, referred to as *namespaces*, which allow isolation.
For instance, if I want to have my own collection of gigaword documents
and ensure that I won't overwrite someone elses data (remember we would have
to be very careful to ensure that `NYT_ENG_20090525.0007` only shows up once
anywhere in accumulo@COE, less we get possibly un-intended over-writes),
I can use the column family `twolfe-cag`
(this is just like having a folder named `twolfe-cag`).
Writes to `(NYT_ENG_20090525.0007, twolfe-cag)` will not conflict with
writes/reads to/from `(NYT_ENG_20090525.0007, vandurme-class-project)`.
Advanced users can look into setting permissions per-column,
but currently accumulo@COE is only setup with a root and read-only account.

Currently the module is setup to map namespaces to column families,
but it is a trivial change to map them to table names.
The immediate reason not to do this is that permissions to create tables (for new namespaces)
in accumulo is greater than permissions to create new key-value entries (for new column families).

### Duplication
Isolation and namespacing means the possibility of duplication.
We do not want to have 35 copies of concretely-annotated-gigaword in accumulo.
Ingesting takes time and disk space.
So be cautious about what you put in. Ask others what is in there already.
Think of accumulo@COE like a home directory: it is fast and space limited.

### Merging Annotations and Other Features
This library does not handle merging annotations and takes a very naive view of
storing annotations: it just stores the same bytes which would be stored on
disk. If you have a taks where you have annotations `A,B,C` for some communications
and would like to merge in annotations `D,E`, this is not your tool.
If accumulo has annotations `A,B,C` and you want to go faster by only sending
annotations `B` over the wire, this is not your tool.
I believe these are orthogonal and non-trivial problems which should not be
a part of simple accumulo, which serves a simple and common need:
read and write communications by id.
