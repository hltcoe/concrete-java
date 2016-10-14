concrete-sql
===

This project allows one to load a `.tar.gz` (or many) files of
`Communication` objects into a SQL database. It provides an API for
ingesting a single Communication, a `.tar.gz` of Communications, and a
way to retrieve by `id`.

## Build

``` shell
mvn clean compile assembly:single
```

## Example code

### An an executable program

#### Creation of db

Use [this script](src/main/scripts/ingest-tar-gzs.sh).

It takes 3 arguments: the path to a directory of `.tar.gz` files of
`Communication` objects, the path to where the database file will be
written, and the path to the built jar file.

#### Querying against the DB

This main method allows a user to provide a text file of communication IDs,
one per line.

It retrieves these IDs (if they exist in the database), and writes them
to a `.tar.gz` file.

Could be useful to a user with a target set of IDs that wants to subselect
without iterating over a huge `.tar.gz` file.

``` shell
java -cp $PATH_TO_BUILT_JAR \
    edu.jhu.hlt.concrete.sql.CommunicationIDListRetriever \
    $PATH_TO_DB_FILE \
    $PATH_TO_OUTPUT_TAR_GZ \
    $PATH_TO_IDS_FILE
```

### API

#### Creation and ingest

``` xtend
Iterable<Communication> comms = ...
val path = Paths.get("/path/to/your/db")
val db = new SQLiteImpl(path)

val pathToData = Paths.get("/my/.tar/.gz/of/comms.tar.gz")
db.ingest(pathToData)
db.execute
db.close
```

#### Query

``` xtend
val path = Paths.get("/path/to/your/db")
val db = new SQLiteImpl(path)

val id = "my_comm_111"
Communication c = db.get(id)
```
