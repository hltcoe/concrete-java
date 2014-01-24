namespace java edu.jhu.hlt.rebar
namespace py rebar.stage
#@namespace scala edu.jhu.hlt.miser

include "ex.thrift"

/**
 * Possible annotation types that Stages can produce.
 */
enum StageType {
  SECTION = 1
  SENTENCE = 2
  TOKENIZATION = 3

  ENTITY_MENTIONS = 10
  ENTITIES = 11

  SITUATION_MENTIONS = 15
  SITUATIONS = 16

  LANG_ID = 20
}

/**
 * A stage. This represents a layer of annotations in Rebar.
 */ 
struct Stage {
  1: string name
  2: string description
  3: i64 createTime
  4: set<string> dependencies
  5: StageType type
}

/**
 * A service definition for manipulating stages.
 */
service StageHandler {
  bool stageExists(1: string stageName) throws (1: ex.RebarThriftException exc)
  void createStage(1: Stage stage) throws (1: ex.RebarThriftException exc)
  list<Stage> getStages() throws (1: ex.RebarThriftException exc)
}
