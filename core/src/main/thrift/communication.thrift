include "language.thrift"
include "structure.thrift"
include "entities.thrift"
include "situations.thrift"
include "uuid.thrift"
include "email.thrift"
include "twitter.thrift"
include "audio.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.communication

enum CommunicationType {
  OTHER = 0
  EMAIL = 1
  NEWS = 2
  WIKIPEDIA = 3
  TWEET = 4
  PHONE_CALL = 5
  // JCM
  USENET = 6
  BLOG = 7
}

struct Communication {
  1: string id,
  2: uuid.UUID uuid
  3: CommunicationType type
  4: string text
  5: optional i64 startTime
  6: optional i64 endTime
  
  
  // annotations
  10: optional language.LanguageIdentification lid
  11: optional structure.SectionSegmentation sectionSegmentation
  12: optional entities.EntityMentionSet entityMentionSet
  13: optional entities.EntitySet entitySet
  14: optional situations.SituationMentionSet situationMentionSet
  15: optional situations.SituationSet situationSet

  20: optional audio.Sound sound
  21: optional twitter.TweetInfo tweetInfo
  22: optional email.EmailCommunicationInfo emailInfo
  
  30: map<string,string> keyValueMap
}