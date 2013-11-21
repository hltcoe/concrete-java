include "stage.thrift"
include "text.thrift"
include "entities.thrift"
include "ex.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.communication

enum DocType {
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

struct LangId {
  1: string id
  2: string name
  3: string version
  4: map<string,double> languageToProbabilityMap
}

struct LanguagePrediction {
  1: string predictedLanguage
}

struct Communication {
  1: string id,
  2: DocType type
  3: string text
  4: optional i32 time
  
  // annotations
  5: optional LangId lid
  6: optional text.SectionSegmentation sectionSegmentation
  7: optional entities.EntityMentionSet entityMentionSet
  8: optional entities.EntitySet entitySet

  15: optional LanguagePrediction language
  
}

exception IngestException {
  1: string message
  2: optional binary serEx
}

exception AnnotationException {
  1: string message
  2: optional binary serEx
}

service Ingester {
  void ingest(1: Communication comm) throws (1: ex.RebarThriftException ex)
}

service Annotator {
  void addLanguageId(1: Communication comm, 2: stage.Stage stage, 3: LangId lid) throws (1: ex.RebarThriftException ex)
  void addLanguagePrediction(1: Communication comm, 2: stage.Stage stage, 3: LanguagePrediction lp) throws (1: ex.RebarThriftException ex)
}

service Reader {
  set<Communication> getAnnotatedCommunications (1: stage.Stage stage) throws (1: ex.RebarThriftException ex)
}

service CorpusHandler {
  void createCorpus(1: string corpusName, 2: set<Communication> commList) throws (1: ex.RebarThriftException ex)

  set<Communication> getCorpusCommunicationSet(1: string corpusName) throws (1: ex.RebarThriftException ex)

  set<string> listCorpora() throws (1: ex.RebarThriftException ex)

  void deleteCorpus(1: string corpusName) throws (1: ex.RebarThriftException ex)

  bool corpusExists(1: string corpusName) throws (1: ex.RebarThriftException ex)
}

