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

### Creation and ingest

``` xtend
Iterable<Communication> comms = ...
val path = Paths.get("/path/to/your/db")
val db = new SQLiteImpl(path)

val pathToData = Paths.get("/my/.tar/.gz/of/comms.tar.gz")
db.ingest(pathToData)
db.execute
db.close
```

### Query

``` xtend
val path = Paths.get("/path/to/your/db")
val db = new SQLiteImpl(path)

val id = "my_comm_111"
Communication c = db.get(id)
```
