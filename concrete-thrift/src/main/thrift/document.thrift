include "stage.thrift"
include "text.thrift"

namespace java edu.jhu.hlt.concrete

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



struct Document {
  1: string id,
  2: DocType t
  3: string text
  4: optional i32 time
  5: optional LangId lid
  6: optional text.SectionSegmentation sectionSegmentation
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
  void ingest(1: Document d) throws (1: IngestException ser)
}

service Annotator {
  void addLanguageId(1: Document document, 2: stage.Stage stage, 3: LangId lid) throws (1: AnnotationException ex)
}

exception RebarThriftException {
  1: string message
  2: optional binary serEx
}

service CorpusHandler {
  void createCorpus(1: string corpusName, 2: set<Document> docList) throws (1: RebarThriftException ex)

  set<Document> getCorpusDocumentSet(1: string corpusName) throws (1: RebarThriftException ex)

  set<string> listCorpora() throws (1: RebarThriftException ex)

  void deleteCorpus(1: string corpusName) throws (1: RebarThriftException ex)

  bool corpusExists(1: string corpusName)
}

