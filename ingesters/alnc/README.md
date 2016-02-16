# Concrete ALNC Ingester
An ingester developed to support ingest of the yet to be released ALNC corpus.

## Quick start
Currently this library is only consumed from other projects. As a
result, there is no `main` method to execute.

The following Java code shows how to consume ALNC concrete objects:
``` java
Path bz2Path = Paths.get("alnc/file.bz2");
ALNCIngester ing = new ALNCIngester(bz2Path);
Iterator<Communication> citer = ing.iterator();
while (citer.hasNext()) {
  Communication c = citer.next();
  // process c
}
```
