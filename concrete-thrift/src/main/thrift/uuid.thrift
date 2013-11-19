namespace java edu.jhu.hlt.concrete

//===========================================================================
// Universally Unique Identifiers
//===========================================================================

//
// A 16-byte UUID identifier.
//
// @see http://en.wikipedia.org/wiki/Universally_unique_identifier
//
struct UUID {
  1: i64 high //!< The 8 most significant bytes
  2: i64 low //!< The 8 least significant bytes
}