include "ex.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.stage

enum StageType {
  LANG_ID = 1
  SECTION = 2
  SENTENCE = 3
  TOKENIZATION = 4

  ENTITY_MENTIONS = 5
  ENTITIES = 6

  LANG_PRED = 99
}

struct Stage {
  1: string name
  2: string desc
  3: i32 createTime
  4: set<string> dependencies
  5: StageType type
}

service StageHandler {
  bool stageExists(1: string stageName) throws (1: ex.RebarThriftException exc)
  void createStage(1: Stage stage) throws (1: ex.RebarThriftException exc)
  set<Stage> getStages() throws (1: ex.RebarThriftException exc)
  i32 getAnnotatedDocumentCount(1: Stage stage) throws (1: ex.RebarThriftException exc)
  //TODO: delete
}
