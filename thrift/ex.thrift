namespace java edu.jhu.hlt.rebar
namespace py rebar.ex
#@namespace scala edu.jhu.hlt.miser

exception RebarThriftException {
  1: string message
  2: optional binary serEx
}
