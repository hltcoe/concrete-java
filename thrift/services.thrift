include "stage.thrift"
include "language.thrift"
include "structure.thrift"
include "entities.thrift"
include "situations.thrift"
include "ex.thrift"
include "email.thrift"
include "twitter.thrift"
include "audio.thrift"
include "communication.thrift"

namespace java edu.jhu.hlt.concrete.java.services
namespace py concrete.services
#@namespace scala edu.jhu.hlt.concrete.services

service Ingester {
  void ingest(1: communication.Communication comm) throws (1: ex.RebarThriftException ex)
}

service Annotator {
  void addLanguageId(1: communication.Communication comm, 2: stage.Stage stage, 3: language.LanguageIdentification lid) throws (1: ex.RebarThriftException ex)
  void addSectionSegmentation(1: communication.Communication comm, 2: stage.Stage stage, 3: structure.SectionSegmentation sectionSegmentation) throws (1: ex.RebarThriftException ex)
  void addSentenceSegmentations(1: communication.Communication comm, 2: stage.Stage stage, 3: structure.SentenceSegmentationCollection sentenceSegmentationCollection) throws (1: ex.RebarThriftException ex)
  void addTokenizations(1: communication.Communication comm, 2: stage.Stage stage, 3: structure.TokenizationCollection tokenizations) throws (1: ex.RebarThriftException ex)
  void addEntityMentions(1: communication.Communication comm, 2: stage.Stage stage, 3: entities.EntityMentionSet ems) throws (1: ex.RebarThriftException ex)
  void addEntities(1: communication.Communication comm, 2: stage.Stage stage, 3: entities.EntitySet es) throws (1: ex.RebarThriftException ex)
  
  // void addAnnotation(1: communication.Communication comm, 2: stage.Stage stage) throws (1: ex.RebarThriftException ex)
}

service Reader {
  list<communication.Communication> getAnnotatedCommunications (1: stage.Stage stage) throws (1: ex.RebarThriftException ex)
}

service CorpusHandler {
  void createCorpus(1: string corpusName, 2: list<communication.Communication> commList) throws (1: ex.RebarThriftException ex)

  list<communication.Communication> getCorpusCommunicationSet(1: string corpusName) throws (1: ex.RebarThriftException ex)

  list<string> listCorpora() throws (1: ex.RebarThriftException ex)

  void deleteCorpus(1: string corpusName) throws (1: ex.RebarThriftException ex)

  bool corpusExists(1: string corpusName) throws (1: ex.RebarThriftException ex)
}

