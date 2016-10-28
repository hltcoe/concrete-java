# miscommunication, adding typedness to Concrete Communications
![Maven Badges](https://maven-badges.herokuapp.com/maven-central/edu.jhu.hlt/miscommunication/badge.svg)

### Javadoc
[![javadoc.io](https://javadocio-badges.herokuapp.com/edu.jhu.hlt/miscommunication/badge.svg)](http://www.javadoc.io/doc/edu.jhu.hlt/miscommunication/)

Concrete `Communication`s are huge and can be unwieldy. Accessing deeply nested
`Tokenization`s, for example, requires a lot of duplicated code to be written to
loop across nested structures.

Miscommunication is a library providing a set of utility classes wrapping Concrete
`Communication`s. This allows for type safety to be added to Communication objects,
providing a cleaner API for Java consumers of Concrete Communications.

Basic wrappers can be found in the [miscommunication](src/main/java/edu/jhu/hlt/concrete/miscommunication)
package, which include types for many popular use cases.

# Quick start
Suppose you have a `Communication` object that you are certain pre-hoc contains `Sentence` objects.
Normally, to access these, you write a nested loop, iterating over each `Section` and then each
`Sentence`.

Miscommunication allows you to wrap this in an implementation of, e.g.,
[MappedSentenceCommunication](src/main/java/edu/jhu/hlt/concrete/miscommunication/sentenced/MappedSentenceCommunication.java),
such as [CachedSentencedCommunication](src/main/java/edu/jhu/hlt/concrete/miscommunication/sentenced/CachedSentencedCommunication.java):
```java
Communication hasSents = ...
// throws MiscommunicationException if it does not actually contain Sentences
CachedSentencedCommunication csc = new CachedSentencedCommunication(hasSents);
List<Sentence> allSents = csc.getSentences(); // way nicer than before!
```

Similar patterns are available for `Section`ed and `Tokenization`ed communications. See
the listed package above for more examples.
