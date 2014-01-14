include "structure.thrift"
include "metadata.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.situations
#@namespace scala edu.jhu.hlt.miser

typedef string UUID

/** 
 *Enumerated type used to record the relationship between an
 * argument and the situation that owns it. 
 */
enum Role {
  OTHER_ROLE = 1;
  PERSON_ROLE = 2;
  TIME_ROLE = 3;
  PLACE_ROLE = 4;
  AGENT_ROLE = 5;
  VICTIM_ROLE = 6;
  INSTRUMENT_ROLE = 7;
  VEHICLE_ROLE = 8;
  ARTIFACT_ROLE = 9;
  PRICE_ROLE = 10;
  ORIGIN_ROLE = 11;
  DESTINATION_ROLE = 12;
  BUYER_ROLE = 13;
  SELLER_ROLE = 14;
  BENEFICIARY_ROLE = 15;
  GIVER_ROLE = 16;
  RECIPIENT_ROLE = 17;
  MONEY_ROLE = 18;
  ORG_ROLE = 19;
  ATTACKER_ROLE = 20;
  TARGET_ROLE = 21;
  ENTITY_ROLE = 22;
  POSITION_ROLE = 23;
  DEFENDANT_ROLE = 24;
  ADJUDICATOR_ROLE = 25;
  PROSECUTOR_ROLE = 26;
  CRIME_ROLE = 27;
  PLAINTIFF_ROLE = 28;
  SENTENCE_ROLE = 29;
  TIME_WITHIN_ROLE = 30;
  TIME_STARTING_ROLE = 31;
  TIME_ENDING_ROLE = 32;
  TIME_BEFORE_ROLE = 33;
  TIME_AFTER_ROLE = 34;
  TIME_HOLDS_ROLE = 35;
  TIME_AT_BEGINNING_ROLE = 36;
  TIME_AT_END_ROLE = 37;
  RELATION_SOURCE_ROLE = 38;
  RELATION_TARGET_ROLE = 39;
}


/** 
 * A situation argument, consisting of an argument role and a value.
 * Argument values may be Entities or Situations. 
 */
struct Argument {
  /** 
   * The relationship between this argument and the situation that
   * owns it. The roles that a situation's arguments can take
   * depend on the type of the situation (including subtype
   * information, such as event_type). 
   */
  1: optional Role role

  /** 
   *A pointer to the value of this argument, if it is explicitly
   * encoded as an Entity or a Situation. 
   */
  2: optional UUID entityId

  // A pointer to the value of this argument, if it is a situation.
  3: optional UUID situationId

  
  /** 
   * New roles should usually be added to the enum, but for use
   * cases with many varied and possibly dynamic role names, this can be
   * used. Presumably this would only be used in a prototype stage of an
   * analytic, with roles eventually "hardening" and moving to the enum. 
   */
  4: optional string roleLabel
}

/** 
 * The way in which the justification's mention provides evidence
 * for the situation.
 */
enum JustificationType {
  DIRECT_MENTION = 1;
  IMPLICIT = 2;
  // this list will grow over time
}

struct Justification {
  /** 
   * An enumerated value used to describe the way in which the
   * justification's mention provides supporting evidence for the
   * situation. 
   */
  1: optional JustificationType justificationType

  /** 
   * A pointer to the SituationMention itself. 
   */
  2: UUID mentionId

  /** 
   * An optional list of pointers to tokens that are (especially)
   * relevant to the way in which this mention provides
   * justification for the situation. It is left up to individual
   * analytics to decide what tokens (if any) they wish to include
   * in this field. 
   */
  3: optional list<structure.TokenRefSequence> tokens
}

enum EventType {
  OTHER_EVENT = 1;
  //-----------------------------------------------------------------
  // ACE event types:
  //-----------------------------------------------------------------
  BUSINESS_DECLARE_BANKRUPTCY_EVENT = 2;
  BUSINESS_END_ORG_EVENT = 3;
  BUSINESS_MERGE_ORG_EVENT = 4;
  BUSINESS_START_ORG_EVENT = 5;
  CONFLICT_ATTACK_EVENT = 6;
  CONFLICT_DEMONSTRATE_EVENT = 7;
  CONTACT_MEET_EVENT = 8;
  CONTACT_PHONE_WRITE_EVENT = 9;
  JUSTICE_ACQUIT_EVENT = 10;
  JUSTICE_APPEAL_EVENT = 11;
  JUSTICE_ARREST_JAIL_EVENT = 12;
  JUSTICE_CHARGE_INDICT_EVENT = 13;
  JUSTICE_CONVICT_EVENT = 14;
  JUSTICE_EXECUTE_EVENT = 15;
  JUSTICE_EXTRADITE_EVENT = 16;
  JUSTICE_FINE_EVENT = 17;
  JUSTICE_PARDON_EVENT = 18;
  JUSTICE_RELEASE_PAROLE_EVENT = 19;
  JUSTICE_SENTENCE_EVENT = 20;
  JUSTICE_SUE_EVENT = 21;
  JUSTICE_TRIAL_HEARING_EVENT = 22;
  LIFE_BE_BORN_EVENT = 23;
  LIFE_DIE_EVENT = 24;
  LIFE_DIVORCE_EVENT = 25;
  LIFE_INJURE_EVENT = 26;
  LIFE_MARRY_EVENT = 27;
  MOVEMENT_TRANSPORT_EVENT = 28;
  PERSONNEL_ELECT_EVENT = 29;
  PERSONNEL_END_POSITION_EVENT = 30;
  PERSONNEL_NOMINATE_EVENT = 31;
  PERSONNEL_START_POSITION_EVENT = 32;
  QUOTATION_DEFINITE_EVENT = 33;
  QUOTATION_POSSIBLE_EVENT = 34;
  TRANSACTION_TRANSFER_MONEY_EVENT = 35;
  TRANSACTION_TRANSFER_OWNERSHIP_EVENT = 36;
}

/** 
 * An enumerated type used to record event types for Situations
 * and SituationMentions where situation_type=STATE. 
 */
enum StateType {
  OTHER_STATE = 1;
  //-----------------------------------------------------------------
  // ACE 2004 relations:
  //-----------------------------------------------------------------
  ART_INVENTOR_OR_MANUFACTURER_STATE = 37;
  ART_OTHER_STATE = 38;
  ART_USER_OR_OWNER_STATE = 39;
  DISC_STATE = 40;
  PHYS_LOCATED_STATE = 41; // Also in ACE 2005
  PHYS_NEAR_STATE = 42; // Also in ACE 2005
  PHYS_PART_WHOLE_STATE = 43;
  EMP_ORG_EMPLOY_EXECUTIVE_STATE = 44;
  EMP_ORG_EMPLOY_STAFF_STATE = 45;
  EMP_ORG_EMPLOY_UNDETERMINED_STATE = 46;
  EMP_ORG_MEMBER_OF_GROUP_STATE = 47;
  EMP_ORG_OTHER_STATE = 48;
  EMP_ORG_PARTNER_STATE = 49;
  EMP_ORG_SUBSIDIARY_STATE = 50;
  GPE_AFF_BASED_IN_STATE = 51;
  GPE_AFF_CITIZEN_OR_RESIDENT_STATE = 52;
  GPE_AFF_OTHER_STATE = 53;
  OTHER_AFF_ETHNIC_STATE = 54;
  OTHER_AFF_IDEOLOGY_STATE = 55;
  OTHER_AFF_OTHER_STATE = 56;
  PER_SOC_BUSINESS_STATE = 57; // Also in ACE 2005
  PER_SOC_FAMILY_STATE = 58; // Also in ACE 2005
  PER_SOC_OTHER_STATE = 59;
  //-----------------------------------------------------------------
  // ACE 2005 relations:
  //-----------------------------------------------------------------
  ART_USER_OWNER_INVENTOR_MANUFACTURER_STATE = 60;
  GEN_AFF_CITIZEN_RESIDENT_RELIGION_ETHNICITY_STATE = 61;
  GEN_AFF_ORG_LOCATION_STATE = 62;
  ORG_AFF_EMPLOYMENT_STATE = 63;
  ORG_AFF_FOUNDER_STATE = 64;
  ORG_AFF_OWNERSHIP_STATE = 65;
  ORG_AFF_STUDENT_ALUM_STATE = 66;
  ORG_AFF_SPORTS_AFFILIATION_STATE = 67;
  ORG_AFF_INVESTOR_SHAREHOLDER_STATE = 68;
  ORG_AFF_MEMBERSHIP_STATE = 69;
  PART_WHOLE_ARTIFACT_STATE = 70;
  PART_WHOLE_GEOGRAPHICAL_STATE = 71;
  PART_WHOLE_SUBSIDIARY_STATE = 72;
  PER_SOC_LASTING_PERSONAL_STATE = 73;
  //-----------------------------------------------------------------
  // This list is expected to grow over time.
}

/** 
 * An enumerated type used to record event types for Situations
 * and SituationMentions where situation_type=TEMPORAL_FACT. 
 */
enum TemporalFactType {
  BEFORE_TEMPORAL_FACT = 1;
  AFTER_TEMPORAL_FACT = 2;
  SIMULTANEOUS_TEMPORAL_FACT = 3;
  INCLUDES_TEMPORAL_FACT = 4;
  IS_INCLUDED_BY_TEMPORAL_FACT = 5;
  VAGUE_TEMPORAL_FACT = 6;
}

/** 
 * An enumeration used to record the TimeML class of a situation 
 */
enum TimeMLClass {
  OCCURRENCE_CLASS = 1;
  PERCEPTION_CLASS = 2;
  REPORTING_CLASS = 3;
  ASPECTUAL_CLASS = 4;
  STATE_CLASS = 5;
  I_STATE_CLASS = 6;
  I_ACTION_CLASS = 7;
}

/** 
 * An enumeration used to record the TimeML tense of a situation 
 */
enum TimeMLTense {
  FUTURE_TENSE = 1;
  INFINITIVE_TENSE = 2;
  PAST_TENSE = 3;
  PASTPART_TENSE = 4;
  PRESENT_TENSE = 5;
  PRESPART_TENSE = 6;
  NONE_TENSE = 7;
}

/** 
 * An enumeration used to record the TimeML aspect of a situation 
 */
enum TimeMLAspect {
  PROGRESSIVE_ASPECT = 1;
  PERFECTIVE_ASPECT = 2;
  PERFECTIVE_PROGRESSIVE_ASPECT = 3;
  NONE_ASPECT = 4;
}

/** 
 * An enumeration used to record the polarity of a situation.
 * This is primarily intended for use with SENTIMENT situations. 
 */
enum Polarity {
  POSITIVE_POLARITY = 1;
  NEGATIVE_POLARITY = 2;
  NEUTRAL_POLARITY = 3;
  BOTH_POLARITY = 4;
}



//===========================================================================
// Situations
//===========================================================================

/** An enumerated type used to record the core types of situations.
 * These types form a type hierarchy, as follows:
 *
 * * SITUATION
 * * FACT
 * * CAUSAL_FACT
 * * TEMPORAL_FACT
 * * EVENT
 * * STATE (includes ACE-style relations)
 * * PRIVATE_STATE
 * * SENTIMENT
 */
enum SituationType {
  SITUATION = 0;
  FACT = 100; //!< Subtype of SITUATION
  CAUSAL_FACT = 110; //!< Subtype of FACT
  TEMPORAL_FACT = 120; //!< Subtype of FACT
  EVENT = 200; //!< Subtype of SITUATION
  STATE = 300; //!< Subtype of SITUATION
  PRIVATE_STATE = 310; //!< Subtype of STATE
  SENTIMENT = 311; //!< Subtype of PRIVATE_STATE
}

/** 
 * A single situation, along with pointers to situation mentions that
 * provide evidence for the situation. "Situations" include events,
 * relations, facts, sentiments, and beliefs. Each situation has a
 * core type (such as EVENT or SENTIMENT), along with an optional
 * subtype based on its core type (e.g., event_type=CONTACT_MEET), and
 * a set of zero or more unordered arguments. 
 */

struct Situation {
  /** Unique identifier for this situation. 
   */
  1: UUID uuid

  /** The core type of this situation (eg EVENT or SENTIMENT) 
   */
  2: SituationType situationType

  /** The arguments for this situation. Each argument consists of a
   * role and a value. It is possible for an situation to have
   * multiple arguments with the same role. Arguments are
   * unordered. 
   */
  3: optional list<Argument> argumentList

  /** Ids of the mentions of this situation in a communication
   * (type=SituationMention) 
   */
  4: optional list<UUID> mentionIdList

  /** An list of pointers to SituationMentions that provide
   * justification for this situation. These mentions may be either
   * direct mentions of the situation, or indirect evidence. 
   */
  5: optional list<Justification> justificationList

  /** The event type for situations where situation_type=EVENT 
   */
  50: optional EventType eventType

  /** The state type for situations where situation_type=STATE 
   */
  51: optional StateType stateType
  
  /** The temporal fact type for situations where situation_type=TEMPORAL_FACT 
   */
  52: optional TemporalFactType temporalFactType
  
  /** This lemma represents a canonical lemma for the situation kind
   * when the situation kind cannot be specified by a situation type subtype
   * (ex, when using arbitrary verbs or nominalizations as events which do
   * not appear in the event_type enumeration).
   * If this kind is grounded in a token sequence from the original text, the
   * appropriate SituationMention should have a reference to the token sequence.
   */
  53: optional string situationKindLemma
  
  /** The TimeML class for situations representing TimeML events 
   */
  54: optional TimeMLClass timemlClass
  
  /** The TimeML tense for situations representing TimeML events 
   */
  55: optional TimeMLTense timemlTense
  
  /** The TimeML aspect for situations representing TimeML events 
   */
  56: optional TimeMLAspect timemlAspect

  /** An "intensity" rating for this situation, typically ranging from
   * 0-1. In the case of SENTIMENT situations, this is used to record
   * the intensity of the sentiment. 
   */
  100: optional double intensity

  /** The polarity of this situation. In the case of SENTIMENT
   * situations, this is used to record the polarity of the
   * sentiment. 
   */
  101: optional Polarity polarity

  /** A confidence score for this individual situation. You can also
   * set a confidence score for an entire SituationSet using the
   * SituationSet's metadata. 
   */
  200: optional double confidence
}

/** 
 * A theory about the set of situations that are present in a
 * message. See also: Situation 
 */
struct SituationSet {
  /** 
   * Unique identifier for this set. 
   */
  1: UUID uuid

  /** 
   * Information about where this set came from. 
   */
  2: optional metadata.AnnotationMetadata metadata

  /** 
   * List of mentions in this set. 
   */
  3: list<Situation> situationList
}

//===========================================================================
// Situation Mentions
//===========================================================================

/** 
 * A concrete mention of a situation, where "situations" include
 * events, relations, facts, sentiments, and beliefs. Each situation
 * has a core type (such as EVENT or SENTIMENT), along with an
 * optional subtype based on its core type (e.g.,
 * event_type=CONTACT_MEET), and a set of zero or more unordered
 * arguments. 
 */
struct SituationMention {
  /** 
   * Unique identifier for this situation. 
   */
  1: UUID uuid

  /** The text content of this situation mention. This field is
   * often redundant with the 'tokens' field, and may not
   * be generated by all analytics. 
   */
  2: optional string text

  /** The core type of this situation (eg EVENT or SENTIMENT) 
   */
  3: optional SituationType situationType

  /** The arguments for this situation mention. Each argument
   * consists of a role and a value. It is possible for an situation
   * to have multiple arguments with the same role. Arguments are
   * unordered. 
   */
  4: list<Argument> argumentList

  /** The event type for situations where situation_type=EVENT 
   */
  50: optional EventType eventType

  /** The state type for situations where situation_type=STATE 
   */
  51: optional StateType stateType
  
  /** This lemma represents a canonical lemma for the situation kind
   * when the situation kind cannot be specified by a situation type subtype
   * (ex, when using arbitrary verbs or nominalizations as events which do
   * not appear in the event_type enumeration).
   * If this kind is grounded in a token sequence from the original text, the
   * SituationMention should have a reference to the token sequence.
   */
  53: optional string situationKindLemma

  /** An "intensity" rating for the situation, typically ranging from
   * 0-1. In the case of SENTIMENT situations, this is used to record
   * the intensity of the sentiment. 
   */
  100: optional double intensity

  /** The polarity of this situation. In the case of SENTIMENT
   * situations, this is used to record the polarity of the
   * sentiment. 
   */
  101: optional Polarity polarity

  /** An optional pointer to tokens that are (especially)
   * relevant to this situation mention. It is left up to individual
   * analytics to decide what tokens (if any) they wish to include in
   * this field. In particular, it is not specified whether the
   * arguments' tokens should be included. 
   */
  150: structure.TokenRefSequence tokens

  /** A confidence score for this individual situation mention. You
   * can also set a confidence score for an entire SituationMentionSet
   * using the SituationMentionSet's metadata. 
   */
  200: optional double confidence
}

/** A theory about the set of situation mentions that are present in a
 * message. See also: SituationMention 
 */
struct SituationMentionSet {
  /** Unique identifier for this set. 
   */
  1: UUID uuid

  /** Information about where this set came from. 
   */
  2: optional metadata.AnnotationMetadata metadata

  /** List of mentions in this set. 
   */
  3: list<SituationMention> mentionList
}
