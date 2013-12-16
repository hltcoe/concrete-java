include "uuid.thrift"
include "metadata.thrift"

namespace java edu.jhu.hlt.concrete
namespace py concrete.language

struct LanguageIdentification {
  1: uuid.UUID uuid
  2: metadata.AnnotationMetadata metadata

  3: map<string,double> languageToProbabilityMap
}
