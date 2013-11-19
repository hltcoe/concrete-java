namespace java edu.jhu.hlt.concrete

struct Stage {
  1: string name
  2: string desc
  3: i32 createTime
  4: set<string> dependencies
}

service StageHandler {
  bool stageExists(1: string stageName)
  void createStage(1: Stage stage)
  set<Stage> getStages()
  //TODO: delete
}
