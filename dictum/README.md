# concrete-dictum

Small utility wrapper around Concrete, providing some validation.

## Introduction

This project has 2 purposes:

1. (largely replaced by thrift 0.9.3, not available at the time)
   Create a set of Thrift objects wrapped by `Optional` fields.
   Since COE developers would likely riot at such a suggestion,
   this library was created as an addition, instead of switching
   the thrift compiler flags to use the `--optional` option.
2. Provide a little validation around the structs.

## API

[This class](src/main/java/edu/jhu/hlt/concrete/dictum/conversion/FromConcrete.java) provides
utility to go from Concrete Communication objects to dictum
Communication objects.

Included this is validation of ISO 639-3 LIDs, reasonable timestamps,
token index validation, etc. Interested parties can explore the source
code for more details.
