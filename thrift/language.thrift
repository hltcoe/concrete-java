namespace java edu.jhu.hlt.concrete
namespace py concrete.language
#@namespace scala edu.jhu.hlt.miser

include "metadata.thrift"

struct LanguageIdentification {
  1: string uuid
  2: metadata.AnnotationMetadata metadata

  3: map<string,double> languageToProbabilityMap
}
