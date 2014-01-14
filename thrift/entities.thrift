namespace java edu.jhu.hlt.concrete
namespace py concrete.entities
#@namespace scala edu.jhu.hlt.miser

include "structure.thrift"
include "metadata.thrift"

typedef string UUID
typedef i64 DateTime

/** 
 * A span of text with a specific referent, such as a person,
 * organization, or time. Things that can be referred to by a mention
 * are called "entities."
 *
 * It is left up to individual EntityMention taggers to decide which
 * referent types and phrase types to identify. For example, some
 * EntityMention taggers may only identify proper nouns, or may only
 * identify EntityMentions that refer to people.
 *
 * Each EntityMention consists of a sequence of tokens. This sequence
 * is usually annotated with information about the referent type
 * (e.g., is it a person, or a location, or an organization, etc) as
 * well as the phrase type (is it a name, pronoun, common noun, etc.).
 *
 * EntityMentions typically consist of a single noun phrase; however,
 * other phrase types may also be marked as mentions. For
 * example, in the phrase "French hotel," the adjective "French" might
 * be marked as a mention for France.
 */

enum EntityType {
  PERSON = 1;
  ORGANIZATION = 2
  GPE = 3
  OTHER = 4
  DATE = 5
  FACILITY = 6
  VEHICLE = 7
  WEAPON = 8
  LOCATION = 9
  TIME = 10
  URL = 11
  EMAIL = 12
  MONEY = 13
  PERCENTAGE = 14 /** XX is this different from PERCENT?? */
  PHONE_NUMBER = 15
  OCCUPATION = 16
  CHEMICAL = 17
  AGE = 18
  PERCENT = 19
  PERSON_NN = 20
  GPE_ITE = 21
  ORGANIZATION_ITE = 22
  JOB_TITLE = 23
  UNKNOWN = 24
  SET = 25                                 // From TimeML Timex
  DURATION = 26                         // From TimeML Timex
  // This list is expected to grow over time.
}

/**
 * A single referent (or "entity") that is referred to at least once
 * in a given communication, along with pointers to all of the
 * references to that referent. The referent's type (e.g., is it a
 * person, or a location, or an organization, etc) is also recorded.
 *
 * Because each Entity contains pointers to all references to a
 * referent with a given communication, an Entity can be
 * thought of as a coreference set.
 */
struct Entity {
  1: UUID uuid
  2: list<UUID> mentionIdList
  3: EntityType type
  4: optional double confidence
  5: optional string canonicalName
}

/** 
 * A theory about the set of entities that are present in a
 * message. See also: Entity.
 */
struct EntitySet {
  /** 
   * Unique identifier for this set. 
   */
  1: UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<Entity> entityList
}

enum PhraseType {
  NAME = 1 //!< aka "proper noun"
  PRONOUN = 2
  COMMON_NOUN = 3
  OTHER = 4
  APPOSITIVE = 5
  LIST = 6
}

//===========================================================================
// Entity Mentions
//===========================================================================

/** 
 * A span of text with a specific referent, such as a person,
 * organization, or time. Things that can be referred to by a mention
 * are called "entities."
 *
 * It is left up to individual EntityMention taggers to decide which
 * referent types and phrase types to identify. For example, some
 * EntityMention taggers may only identify proper nouns, or may only
 * identify EntityMentions that refer to people.
 *
 * Each EntityMention consists of a sequence of tokens. This sequence
 * is usually annotated with information about the referent type
 * (e.g., is it a person, or a location, or an organization, etc) as
 * well as the phrase type (is it a name, pronoun, common noun, etc.).
 *
 * EntityMentions typically consist of a single noun phrase; however,
 * other phrase types may also be marked as mentions. For
 * example, in the phrase "French hotel," the adjective "French" might
 * be marked as a mention for France.
 */
struct EntityMention {
  /*
   * A unique idenifier for this entity mention.
   */
  1: UUID uuid
  2: structure.TokenRefSequence tokens
  3: EntityType entityType
  4: PhraseType phraseType
  
  5: optional double confidence
  6: optional string text
}


/**
 * A theory about the set of entity mentions that are present in a
 * message. See also: EntityMention
 *
 * This type does not represent a coreference relationship, which is handled by Entity.
 * This type is meant to represent the output of a entity-mention-identifier,
 * which is often a part of an in-doc coreference system.
 */
struct EntityMentionSet {
  /** 
   * Unique identifier for this set. 
   */
  1: UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<EntityMention> mentionSet
}
