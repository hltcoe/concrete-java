include "uuid.thrift"
include "metadata.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.discourse
/**
 * A reference to an Entity in a Communication.
 */
struct EntityRef {
  1: uuid.UUID entityId                // type=Entity
  2: uuid.UUID communicationId        // type=Communication
}

/**
 * A reference to a Situation in a Communication.
 */
struct SituationRef {
  1: uuid.UUID situationId                // type=Situation
  2: uuid.UUID communicationId        // type=Communication
}

/**
 * represents one Entity in a cross-doc situation coref/alignment
 */
struct DiscourseEntity {
  1: uuid.UUID id
  2: list<EntityRef> entityRefList                        // all mentions of this entity
  3: optional double confidence
}

/**
 * represents one Situation in a cross-doc situation coref/alignment
 */
struct DiscourseSituation {
  1: uuid.UUID id
  2: list<SituationRef> situationRefList                // all mentions of this situation
  3: optional double confidence
}

/**
 * a theory of cross-doc Entity/Situtation coreference
 */
struct DiscourseAnnotation {        // come in gold and synthetic varieties
  // The ID associated with this DiscourseAnnotation.
  1: uuid.UUID id

  // The metadata associated with the tool responsible for suggesting this DiscourseAnnotation.
  2: metadata.AnnotationMetadata metadata

  // A set of DiscourseEntities suggested by this DiscourseAnnotation object.
  3: list<DiscourseEntity> discourseEntityList                        // all entities mentioned in this Discourse
  
  // A set of DiscourseSituations suggested by this DiscourseAnnotation object.
  4: list<DiscourseSituation> discourseSituationList                // all situations mentioned in this Discourse
}


/**
 * a meaniningful set of Communications, possibly
 * with some coreference annotations.
 */
struct Discourse {        

  // the ID associated with this Discourse object.
  1: uuid.UUID id

  /**
   * the tool that identified this set of Communications
   * (not the tool that determined any coreference assessments)
   */
  2: metadata.AnnotationMetadata metadata

  /**
   * theories about the coreference relationships between
   * Entity or Situtation discussed in this set of Communications.
   */
  3: list<DiscourseAnnotation> annotationList

}

