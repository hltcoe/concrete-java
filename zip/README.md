### Concrete-zip

Concrete-zip provides IO routines and FetchCommunicationService utilities
when working with zip archives.

In order to support random access of Communications by their
Communication IDs, Concrete-zip assumes that any Communication files
in the zip archive:

- follow the naming convention `[COMMUNICATION_ID].comm` or
  `[COMMUNICATION_ID].concrete`
- are in the root directory of the zip archive

These naming conventions allow zip archives of Communications (unlike
tar.gz archives) to support random access by Communication ID.
This makes zip archives a suitable backend for file-backed
FetchCommunicationService providers. (No need to do a linear sweep of
communications to get one, as in tar.gz archives, and also ideal for 
people who don't want to meddle with Accumulo fetch services!)

#### Usage

The current version of Concrete-zip provides 3 different ways to access
a Communication zip archive:

* Sequentially as a stream of Communications

```java
 Iterable<Communication> comms = ConcreteZipIO.read("/Users/tongfei/my/data/LDC2014E13filtered/test.zip")
 // Don't worry about memory leaks: the returned collection is lazy!
 
 Stream<Communication> stream = ConcreteZipIO.readAsStream("/Users/tongfei/my/data/LDC2014E13filtered/test.zip")
```

* As a lazy map of Communication IDs to the actual corresponding Communications

```java
  Map<String, Communication> comms = ConcreteZipIO.openAsMap("/Users/tongfei/my/data/LDC2014E13filtered/test.zip")
  // Again, don't worry about memory leaks: the returned map is lazy and immutable.
  // You can use get(commId) to get a specific communication, 
  // or use keySet(), values() or entrySet(). These are all lazy.
```

* Run as a FetchCommunicationService

```sh
  java -cp $PACKED_JAR edu.jhu.hlt.concrete.zip.ConcreteZipArchiveFetchServiceLauncher $PATH_TO_ZIP $PORT
```
Starts a zip-file-based `FetchCommunicationService` on the given port.

Concrete-zip also provides utilities for writing Communications to a zip
archive.

```java
  ConcreteZipIO.write(filename, comms);
```
or, use a `ConcreteZipArchiveWriter` manually.

