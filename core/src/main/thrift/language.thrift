include "uuid.thrift"
include "metadata.thrift"

namespace java edu.jhu.hlt.concrete.java
namespace py concrete.language
#@namespace scala edu.jhu.hlt.concrete

struct LanguageIdentification {
  1: uuid.UUID uuid
  2: metadata.AnnotationMetadata metadata

  3: map<string,double> languageToProbabilityMap
}
