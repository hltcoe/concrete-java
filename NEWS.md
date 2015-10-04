# News
## Concrete-Java v4.8.3 - 2015-10-4
* Gigaword ingester: fix an issue where zero-length text
spans were being propagated through.
* Util: add some utility predicates to `TextSpanWrapper`
and `SectionWrapper` for easier filtering of these types.
* Util: add a utility, `FilterArchiveByCommunicationType`,
that allows dropping communications of a particular type
from an archive.

## Concrete-Java v4.6.10 - 2015-8-2
* Fix an issue with empty sections in BOLT ingester
* Deprecate `SuperTextSpan` in favor of `TextSpanWrapper`

## Concrete-Java v4.6.7 - 2015-7-10
Bugfix release: truly fix up the `concrete-parent` issue.

## Concrete-Java v4.6.6 - 2015-7-10
Bugfix release: depend upon fixed `concrete-parent`.

## Concrete-Java v4.6.5 - 2015-7-9
Contains [GigawordDocumentBatchConverter](ingesters/gigaword/src/main/java/edu/jhu/hlt/concrete/ingesters/gigaword/GigawordBatchDocumentConverter.java),
capable of taking output from `xargs` for bulk `.sgml` file ingest.

## Concrete-Java v4.6.4 - 2015-7-9
First cut at a web post ingester.

## Concrete-Java v4.6.3 - 2015-7-8
Bug fix release: Switch `bolt` ingester to Woodstox API, fixing a few
underlying issues.

## Concrete-Java v4.6.2 - 2015-7-7
Add an ingester for BOLT forum posts.

## Concrete-Java v4.6.1 - 2015-6-30
Fix up a bad release.

## Concrete-Java v4.6.0 - 2015-6-26
Updates to support concrete v4.6.

## Concrete-Java v4.5.9 - 2015-6-15
Update the `ingesters/gigaword` library to take a `.gz` file from
English Gigaword v5 and create a `.tar.gz` archive of `Communication` objects.

See [this class](ingesters/gigaword/src/main/java/edu/jhu/hlt/concrete/ingesters/gigaword/GigawordGzProcessor.java)
for details.

Misc:
* Updated dependencies for `acute`, `joda-time`, and `gigaword`.
* Improved documentation and added some default implementations
for serialization classes.

## Concrete-Java v4.5.8 - 2015-6-11
Minor release containing a patched Gigaword ingester library. Should provide
additional safety against StackOverflowErrors.

## Concrete-Java v4.5.7 - 2015-6-10
Minor release: add utility factories for creating Parse and DependencyParse
objects; also fix an issue in the TokenTaggingFactory where NPEs could
occasionally fire.

## Concrete-Java v4.5.6 - 2015-6-5
Update the `gigaword` library dependency and rework `ingesters/gigaword` to
use the new API.

Also fix an issue in Tift where Strings were being concatenated naively; now
uses a StringBuilder.

## Concrete-Java v4.5.5 - 2015-6-3
Tiny update to make validation more verbose to track down a downstream bug.

## Concrete-Java v4.5.4 - 2015-5-30
Fix some UTF-8 encoding landmines, make a few inner classes static, and depend
upon the latest acute and utilt dependencies.

## Concrete-Java v4.5.3 - 2015-5-29
Add `NoEmptySentenceListOrTokenizedCommunication`, a `miscommunication` implementation
for analytics that depend upon section objects with either an unset sentence list
or a sentence list with more than zero members. Primarily to support concrete-stanford.

## Concrete-Java v4.5.2 - 2015-5-27
Changes include:
### `miscommunication`
* Add implementations supporting `EntityMention`s and `SituationMention`s.
* Fix an issue where `NonTokenizedSentencedCommunication` did not actually
have anything implemented.
* Add a package for lemmas.

### `analytics`
* Refactor the interface to allow production of generic `WrappedCommunication`
implementations.
* Add an analytic interface, `NonSentencedSectionedCommunicationAnalytic`,
that enforces input Communications have `Section`s, but no `Sentence`s.

### General
* Fixed warnings for deprecated classes across numerous packages.

## Concrete-Java v4.5.1 - 2015-5-26
* Add [NonTokenizedSentencedCommunication](miscommunication/src/main/java/edu/jhu/hlt/concrete/miscommunication/sentenced/NonTokenizedSentencedCommunication.java),
an implementation of `MappedSentenceCommunication` that enforces no `Sentence` objects have `Tokenization`s set.
* Add [NonSentencedSectionedCommunication](miscommunication/src/main/java/edu/jhu/hlt/concrete/miscommunication/sectioned/NonSentencedSectionedCommunication.java),
an implementation of `MappedSectionCommunication` that enforces no `Section` objects have `Sentence`s set.

## Concrete-Java v4.5.0 - 2015-5-25
* Build against concrete v4.5 (ConstituentRef addition)
* Update to the latest annotated-nyt dependency, fixing an ingest issue.

## Concrete-Java v4.4.11 - 2015-5-15
* Fixes an issue with validation code that uses deprecated libraries - now using
the Miscommunication API.

## Concrete-Java v4.4.10 - 2015-5-6

### Introduction of `miscommunication` module
The [miscommunication](miscommunication/) module attempts to add some type
discpline to Concrete Communication objects. Previously this functionality
was handled in an uber-object, `SuperCommunication`. This cleaner API allows
for more modular implementations (e.g., aggresively cached vs. not).

See more in the `miscommunication` directory.

### `analytics` library uses `miscommunication`
Various interfaces have been added to the `analytics-base` library that utilize
`miscommunication` interfaces for more safe annotation.

For example, if an `Analytic` implementation produces a `SectionedCommunication` object,
there exists [an interface](miscommunication/src/main/java/edu/jhu/hlt/concrete/miscommunication/sectioned/SectionedCommunication.java)
that allows for type-safe `Communication` objects to be produced. As a result, interfaces in
`analytics-base` have been updated.

Additionally, the `ingesters-simple` and `analytics-simple` now have example implementations
of these more strongly typed `Communication`s.

### Miscellaneous
* Small improvements to `validation` module when working with `Tokenizations` and their children
* Fix a few bugs with respect to `analytics-simple` analytics not correctly validating their inputs

## Concrete-Java v4.4.9 - 2015-5-3
* Fix a bug with the `SingleSectioningAnalytic`'s validity check
* Add `TokenizationFactory` and `TextSpanFactory` utility classes

## Concrete-Java v4.4.8 - 2015-4-28
Contains a bug fix for the Annotated NYT Concrete ingester.

## Concrete-Java v4.4.7 - 2015-4-28
Contains additional methods in [SuperCommunication](util/src/main/java/edu/jhu/hlt/concrete/communications/SuperCommunication.java)
to support entity linking tasks.

## Concrete-Java v4.4.6 - 2015-4-27
Notes coming soon

## Concrete-Java v4.4.5 - 2015-4-22

### `annotated-nyt` ingester added
An ingester for the [Annotated NYT Corpus](https://catalog.ldc.upenn.edu/LDC2008T19)
has been added. See the `ingesters/annotated-nyt` package.

## Concrete-Java v4.4.4 - 2015-3-20

### `mvn site` enabled
Documentation is now build alongside the project and can be
accessed [here](http://hltcoe.github.io/concrete-java).

Javadocs can be found by clicking on a module, then looking under the
`Project Reports` section.

### `gigaword` and `alnc` ingesters added
Ingesters for [English Gigaword v5](https://catalog.ldc.upenn.edu/LDC2011T07) and
the ALNC corpus are now available. They can be found in the `ingesters` folder.

### Improved tool names
Tool names for tools have been improved to include the class, project, and
version.

### Improvements to `safe` library
The `safe` module now has support for `Communication` objects via
`SafeCommunication`.

### Additional `ingesters-base` interfaces
Consumers can now implement Stream-based ingesters via the
`edu.jhu.hlt.concrete.ingesters.base.stream` package interfaces.

### Other updates

* Small update to the `validation` library to include testing a facet of
`Tokenization` objects.
* Began adding more `package-info.java` to various packages.
* Use latest `acute` library

## Concrete-Java v4.4.3 - 2015-3-4

This update contains the latest `edu.jhu.hlt/acute` library.

## Concrete-Java v4.4.2 - 2015-2-24

### `utilt` project
Utility IO classes that were originally in the `base` module
have been moved to a different Maven project.

## Concrete-Java v4.4.1 - 2015-2-19

### `safe` module

A new module, `safe`, has been added. This project will attempt to map
required Concrete fields to Java interfaces, allowing consumers to
use these implementations without fear of write-time errors due to
missing fields.

### `MetadataTool` interface

The `MetadataTool` interface supports more detailed and easier-to-parse
strings for `AnnotationMetadata` objects. Consumers can use implementers
to easily parse and read output from tools that are then added to
`AnnotationMetadata` objects.

### Ingester implementation of `safe` and `MetadataTool`

Simple ingesters now implement `MetadataTool` and utilize the safe
code from `SafeAnnotationMetadata`.

## Concrete-Java v4.4.0 - 2015-2-18

### Thrift objects
Thrift fields are no longer public. Code that depends upon a thrift field,
such as `comm.text`, will need to be changed to use the getter methods.

In most cases, accesses can be changed with the addition of `get` or `set`,
followed by camel case. For example, `comm.getText()` or `comm.setText(myText)`.

### Introduction of `ingesters` module
Ingesters for Concrete are being moved into this project. Currently, a simple ingester
is included, as well as a library with common ingester code. The simple ingester
allows consumers to take character-based files and convert them to `Communication`
objects.

Currently, two implementations exist: `CompleteFileIngester`, which ingests
complete text files into a `Communication`, and `DoubleLineBreakFileIngester`,
which creates sections for each double line break (platform independent) in a
character-based file.

In the future, additional ingesters for other corpora will be relocated into
this project.

Consult the [README.md](ingesters/simple/README.md) for information on how
to run the simple ingester utilities.

### Type-bounded Thrift iterators
Iterators for creating and reading archives with generic Thrift-like objects
(e.g., `Clustering` objects) now exist in the `serialization` package. These use
reflection to read and write thrift-like object - any class generated by the
Thrift compiler (e.g., any class in concrete-core) can be used as the type bound.

Consumers working with `Communication` objects should maintain their dependency
on `CommunicationSerializer` and related implementations; these do not reflect.

Consult `BoundedThriftAPITest` for example usage, located
[here](util/src/test/java/edu/jhu/hlt/concrete/serialization/BoundedThriftAPITest.java).

### Additional thrift-based packages
Packages for thrift-specific datatypes (`Communication`, `Section`, etc.) have
been created. These contain utilities for working with these data types. For
example, the `SectionFactory` class allows a consumer to create a `Section`
with a UUID already assigned.

The following `Factory` classes are now in the library:

* `SectionFactory` ([source](util/src/main/java/edu/jhu/hlt/concrete/section/SectionFactory.java))
* `AnnotationMetadataFactory` ([source](util/src/main/java/edu/jhu/hlt/concrete/metadata/AnnotationMetadataFactory.java))
* `CommunicationFactory` ([source](util/src/main/java/edu/jhu/hlt/concrete/communications/CommunicationFactory.java))
* `UUIDFactory` ([source](util/src/main/java/edu/jhu/hlt/concrete/uuid/UUIDFactory.java))

### `edu.jhu.hlt.concrete.random` package
Functionality for generating mock Concrete objects has moved to the
`edu.jhu.hlt.concrete.random` package. The class `RandomConcreteFactory`
contains numerous tools to generate synthetic Concrete objects. It replaces the
now deprecated `ConcreteFactory`.

### `SingleSectionSegmenter` for creating whole-document sections
The `SingleSectionSegmenter` class introduces a method that will convert
an entire `String` of text into a `Section` object, with the correct `TextSpan`,
and the assigned `sectionKind`.

The class can be viewed [here](util/src/main/java/edu/jhu/hlt/concrete/section/SingleSectionSegmenter.java).

### `edu.jhu.hlt.concrete.data` package removed
This package did not belong in `concrete-java` and has been removed. If
consumers need access to this code, it will appear in another library
in the near future.

### Deprecations
The following classes are deprecated and will be removed in a future release.

* `ConcreteFactory` - replaced by `RandomConcreteFactory`
* `ConcreteUUIDFactory` - replaced by `UUIDFactory`
* `Util` - replaced by method in `UUIDFactory`
* `Serialization` - replaced by `ThriftSerializer`
