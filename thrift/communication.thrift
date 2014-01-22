namespace java edu.jhu.hlt.concrete
namespace py concrete.communication
#@namespace scala edu.jhu.hlt.miser

include "language.thrift"
include "structure.thrift"
include "entities.thrift"
include "situations.thrift"
include "email.thrift"
include "twitter.thrift"
include "audio.thrift"

enum CommunicationType {
  OTHER = 0
  EMAIL = 1
  NEWS = 2
  WIKIPEDIA = 3
  TWEET = 4
  PHONE_CALL = 5
  USENET = 6
  BLOG = 7
}

/**
 * A communication.
 */
struct Communication {
  /** 
   * Stable identifier for this communication, identifying both the
   * name of the source corpus and the document that it corresponds to
   * in that corpus. 
   */
  1: required string id

  /** 
   * Universally unique identifier for this communication instance.
   * This is generated randomly, and can *not* be mapped back to the
   * source corpus. It is used as a target for symbolic "pointers".
   */
  2: required string uuid

  /** 
   * An enumeration used to indicate what type of communication this
   * is. The optional fields named "<i>kind</i>Info" can be used to
   * store extra fields that are specific to the communication
   * type. 
   */
  3: required CommunicationType type

  /** 
   * The full text contents of this communication in its original
   * form, or in the least-processed form available, if the original
   * is not available. 
   */
  4: required string text

  5: optional i64 startTime
  6: optional i64 endTime
  
  // annotations
  10: optional list<language.LanguageIdentification> lids
  11: optional list<structure.SectionSegmentation> sectionSegmentations
  12: optional list<entities.EntityMentionSet> entityMentionSets
  13: optional list<entities.EntitySet> entitySets
  14: optional list<situations.SituationMentionSet> situationMentionSets
  15: optional list<situations.SituationSet> situationSets

  20: optional audio.Sound sound
  21: optional twitter.TweetInfo tweetInfo
  22: optional email.EmailCommunicationInfo emailInfo
  
  // 30: map<string,string> keyValueMap
}