include "metadata.thrift"

namespace java edu.jhu.hlt.concrete.java
namespace py concrete.language
#@namespace scala edu.jhu.hlt.concrete

typedef string UUID

struct LanguageIdentification {
  1: UUID uuid
  2: metadata.AnnotationMetadata metadata

  3: map<string,double> languageToProbabilityMap
}
