# Concrete Conversion Information #

## General changes ##

* No nested types - all nested types have been replaced by a more descriptive root type. For example, Section.Kind has now been replaced by SectionKind.
* Native list/map support - many structures now utilize Thrift's native support for lists and maps. E.g., the KeyValues data structure has been replaced by a map<string, string>.
* Constituents - Constituents have been refactored. Because Thrift does not support recursive mappings, Constituents and Parses now are represented as follows:
  * Constituent - contains an integer ID, and a list of integers that represent pointers to other Constituent IDs in the Parse's Constituent list.
  * Parse - contains a list of Constituents.

In other words, if one wanted to find all constituents that were children of constituent __k__, one could write code like so:

```java
Parse p = getParse();
List<Constituent> constituentList = p.getConsituentList();
Constituent cK = constituentList.get(k);
List<Integer> cKChildren = cK.getChildList();
for (Integer i : cKChildren) {
    Constituent cKChildConst = constituentList.get(i);
}
```

## Detailed changelog ##
* Communications
  * CommunicationGUID has been removed. APIs should use the 'id' field of Communication to represent a corpus-specific identifier, and 'uuid' to represent a unique identifier.
  * Communication-level annotations (e.g., SectionSegmentation) are now optional instead of lists. This is to better support downstream tasks where users want a specific SectionSegmentation run, with a specific Tokenization, etc. instead of packing all theories into one massive Communication object.
  * SerifXML field has been removed.
  * Communication.Kind is now CommunicationKind.
* LanguageIdentification
  * LanguageProb has been replaced by a map<string, double>, which accurately captures the same data structure used to record language ID to probability mappings.
* SerifXML
  * Removed.
* TextSpan / AudioSpan
  * Fields are now required.
* KeyValues
  * This has been replaced by a map<string, string> structure.
* Sections
  * Section.Kind is now SectionKind, with the same enumerated values.
  * Sections now carry only one SentenceSegmentation.
* Sentences
  * Sentences now carry only one Tokenization.
* Tokenization
  * Tokenization.Kind has been replaced by TokenizationKind, with the same enumerated values.
  * Previously nested structures inside Tokenization (e.g., Parse, Constituent) are now top-level structures.
* Parses
  * Parses now contain a list of Constituent objects. Constituent objects' id and children should map directly to this list, so that a constituent with id = 0 should be the first element in this list.
* Constituents
  * No longer contain a list of constituents as children, but instead contain a list of integer ids that map back into the parse's constituent list.
* Entities
  * Entity.Type has been replaced with EntityType, with the same enumerated values.
* EntityMentions
  * PhraseType has been moved to a top-level object.
* Situations
  * All nested structures (Argument, Justification, EventType, StateType, TemporalFactType, TimeMLClass, TimeMLTense, TimeMLAspect, Polarity) have been moved to top-level objects.
  * Situation.Type is now SituationType.
