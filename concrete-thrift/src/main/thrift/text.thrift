namespace java edu.jhu.hlt.concrete

include "uuid.thrift"
include "metadata.thrift"

//===========================================================================
// Spans in Text/Audio
//===========================================================================

/** 
 * A span of text within a single communication, identified by a pair
 * of character offsets. In this context, a "character offset" is a
 * zero-based count of UTF-16 codepoints. I.e., if you are using
 * Java, or are using a Python build where sys.maxunicode==0xffff,
 * then the "character offset" is an offset into the standard
 * (unicode) string data type. If you are using a Python build where
 * sys.maxunicode==0xffffffff, then you would need to encode the
 * unicode string using UTF-16 before using the character offsets. 
 */
struct TextSpan {
  /** Start character, inclusive. */
  1: i32 start

  /** End character, exclusive */
  2: i32 ending
}

//===========================================================================
// Tokens & Tokenizations
//===========================================================================

/** 
 * A single token (typically a word) in a communication. The exact
 * definition of what counts as a token is left up to the tools that
 * generate token sequences.
 *
 * Usually, each token will include at least a text string.
 */
struct Token {
  /** A tokenization-relative identifier for this token. Together
    * with the UUID for a Tokenization, this can be used to define
    * pointers to specific tokens. If a Tokenization object contains
    * multiple Token objects with the same id (e.g., in different
    * n-best lists), then all of their other fields *must* be
    * identical as well. */
  // A 0-based index that represents the order that this token appears in the sentence.
  1: i32 tokenIndex

  // The text associated with this token.
  // Note - we may have a destructive tokenizer (e.g., Stanford rewriting)
  // and as a result, we want to maintain this field.
  2: string text

  /** Location of this token in the original text. In cases where
   * this token does not correspond directly with any text span in
   * the original text (such as word insertion during MT), this field
   * may be given a value indicating "approximately" where the token
   * comes from. A span covering the entire sentence may be used if
   * no more precise value seems appropriate. */
  3: optional TextSpan textSpan

  /** Location of this token in the original audio. */
  // optional AudioSpan audio_span = 5;
}

/** A list of pointers to tokens that all belong to the same
  * tokenization. */
struct TokenRefSequence {

  /** The tokenization-relative identifiers for each token that is
    * included in this sequence. */
  1: list<i32> tokenIndexList

  /** An optional field that can be used to describe
   * the root of a sentence (if this sequence is a full sentence),
   * the head of a constituent (if this sequence is a constituent),
   * or some other form of "canonical" token in this sequence if,
   * for instance, it is not easy to map this sequence to a another
   * annotation that has a head */
  2: optional i32 anchorTokenIndex = -1

  /** The UUID of the tokenization that contains the tokens. */
  3: required uuid.UUID tokenizationId

  // The text span associated with this TokenRefSequence.
  4: optional TextSpan textSpan

  // The audio span associated with this TokenRefSequence.
  // optional AudioSpan audio_span
}

struct TaggedToken {
  /* A pointer to the token being tagged. */
  1: optional i32 tokenIndex

    /** A string containing the annotation.
         * If the tag set you are using is not case sensitive,
         * then all part of speech tags should be normalized to upper case. */
  2: optional string tag

    /** Confidence of the annotation. */
  3: optional double confidence
}

/** 
 * A theory about some token-level annotation.
 * The TokenTagging consists of a mapping from tokens
 * (using token ids) to string tags (e.g. part-of-speech tags or lemmas).
 *
 * The mapping defined by a TokenTagging may be partial --
 * i.e., some tokens may not be assigned any part of speech tags.
 *
 * For lattice tokenizations, you may need to create multiple
 * part-of-speech taggings (for different paths through the lattice),
 * since the appropriate tag for a given token may depend on the path
 * taken. For example, you might define a separate
 * TokenTagging for each of the top K paths, which leaves all
 * tokens that are not part of the path unlabeled.
 *
 * Currently, we use strings to encode annotations. In
 * the future, we may add fields for encoding specific tag sets
 * (eg treebank tags), or for adding compound tags.
 */
struct TokenTagging {
  1: uuid.UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<TaggedToken> taggedTokenList
}

struct Dependency {
  1: optional i32 gov        // will be null for ROOT token (only)
  2: i32 dep
  3: optional string edgeType
}

struct DependencyParse {
  1: uuid.UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<Dependency> dependencyList
}

//===========================================================================
// Parse Trees
//===========================================================================

/** 
 * A single parse constituent (or "phrase"). 
 */
struct Constituent {
  /** 
   * A parse-relative identifier for this consistuent. Together
   * with the UUID for a Parse, this can be used to define
   * pointers to specific constituents. 
   */
  1: i32 id
  2: optional string tag

  /*
   * The list of parse constituents that are directly dominated by
   * this constituent. 
   */
  3: list<i32> childList

  /** 
   * The list of pointers to the tokens dominated by this
   * constituent. Typically, this field will only be defined for
   * leaf constituents (i.e., constituents with no children). For
   * many parsers, len(tokens) will always be either 1 (for leaf
   * constituents) or 0 (for non-leaf constituents). 
   */
  4: optional TokenRefSequence tokenSequence

  /** 
   * The index of the head child of this constituent. I.e., the
   * head child of constituent <tt>c</tt> is
   * <tt>c.children[c.head_child_index]</tt>. A value of -1
   * indicates that no child head was identified. 
   */
  5: optional i32 headChildIndex = -1
}

/** A theory about the syntactic parse of a sentence.
 *
 * \note If we add support for parse forests in the future, then it
 * will most likely be done by adding a new field (e.g.
 * "<tt>forest_root</tt>") that uses a new struct type to encode the
 * forest. A "<tt>kind</tt>" field might also be added (analogous to
 * <tt>Tokenization.kind</tt>) to indicate whether a parse is encoded
 * using a simple tree or a parse forest.
 */
struct Parse {
  1: uuid.UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<Constituent> root
}




struct LatticePath {
  1: optional double weight
  2: list<Token> tokenList
}

/*
 * Type for arcs. For epsilon edges, leave 'token' blank. 
 */
struct Arc {
  1: optional i32 src //!< state identifier
  2: optional i32 dst //!< state identifier
  3: optional Token token //!< leave empty for epsilon edge
  4: optional double weight //!< additive weight; lower is better
}

struct TokenLattice {
  /*
   * Start state for this token lattice. 
   */
  1: optional i32 startState = 0
  
  /*
   * End state for this token lattice. 
   */
  2: optional i32 endState = 0

  /*
   * The set of arcs that make up this lattice (order is
   * unspecified). 
   */
  3: list<Arc> arcList

  /*
   * A cached copy of the one-best path through the token lattice.
   * This field must always be kept consistent with the arc-based
   * lattice: if you edit the lattice, then you must either delete
   * this field or ensure that it is up-to-date. 
   */
  4: optional LatticePath cachedBestPath
}

enum TokenizationKind {
  TOKEN_LIST = 1
  TOKEN_LATTICE = 2
}

/** 
 * A theory (or set of alternative theories) about the sequence of
 * tokens that make up a sentence.
 *
 * This message type is used to record the output of not just for
 * tokenizers, but also for a wide variety of other tools, including
 * machine translation systems, text normalizers, part-of-speech
 * taggers, and stemmers.
 *
 * Each Tokenization is encoded using either a single list of tokens,
 * or a TokenLattice. (If you want to encode an n-best list, then
 * you should store it as n separate Tokenization objects.) The
 * "kind" field is used to indicate whether this Tokenization contains
 * a list of tokens or a TokenLattice.
 *
 * The confidence value for each sequence is determined by combining
 * the confidence from the "metadata" field with confidence
 * information from individual token sequences as follows:
 *
 * <ul>
 * <li> For n-best lists:
 * metadata.confidence </li>
 * <li> For lattices:
 * metadata.confidence * exp(-sum(arc.weight)) </li>
 * </ul>
 *
 * Note: in some cases (such as the output of a machine translation
 * tool), the order of the tokens in a token sequence may not
 * correspond with the order of their original text span offsets.
 */
struct Tokenization {
  /*
   * Unique identifier for this tokenization. 
   */ 
  1: uuid.UUID uuid  
  2: optional metadata.AnnotationMetadata metadata
  3: list<Token> tokenList
  4: optional TokenLattice lattice
  5: TokenizationKind kind
  
  6: optional TokenTagging posTagList
  7: optional TokenTagging nerTagList
  8: optional TokenTagging lemmaList
  9: optional TokenTagging langIdList

  10: optional Parse parse
  11: optional list<DependencyParse> dependencyParseList
}

//===========================================================================
// Sentences
//===========================================================================
/*
 * A single sentence or utterance in a communication. 
 */
struct Sentence {
  1: uuid.UUID uuid
  2: Tokenization tokenization
  3: optional TextSpan textSpan
}

/** 
 * A theory about how a section of a communication is broken down
 * into sentences (or utterances). The sentences in a
 * SentenceSegmentation should be ordered and non-overlapping. 
 */
struct SentenceSegmentation {
  1: uuid.UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<Sentence> sentenceList
}

enum SectionKind {
  OTHER = 0
  // E.g., one or more paragraphs, or the full text of a tweet
  PASSAGE = 1
  // E.g., the header text of an email from Enron
  METADATA = 2
  // a bulleted list that is formatted such that we expect NLP tools to choke
  LIST = 3
  // an embedded table that will almost certainly cause NLP tools to choke
  TABLE = 4
  // TODO, include embedded image support when actually needed
    IMAGE = 5
  // etc..
}

//===========================================================================
// Sections (aka "Regions" or "Zones")
//===========================================================================

/**
 * A single "section" of a communication, such as a paragraph. Each
 * section is defined using a text or audio span, and can optionally
 * contain a list of sentences. 
 */
struct Section { 
  1: uuid.UUID uuid
  2: SentenceSegmentation sentenceSegmentation
  3: optional TextSpan textSpan
  4: SectionKind kind
  5: optional string label

  // Position within the communication with respect to other Sections:
  // The section number, E.g., 3, or 3.1, or 3.1.2, etc. Aimed at
  // Communications with content organized in a hierarchy, such as a Book
  // with multiple chapters, then sections, then paragraphs. Or even a
  // dense Wikipedia page with subsections. Sections should still be
  // arranged linearly, where reading these numbers should not be required
  // to get a start-to-finish enumeration of the Communication's content.
  6: optional list<i32> number
}

/** 
 * A theory about how a communication is broken down into smaller
 * sections (such as paragraphs). The sections should be ordered
 * and non-overlapping. 
 */
struct SectionSegmentation {
  1: uuid.UUID uuid
  2: optional metadata.AnnotationMetadata metadata
  3: list<Section> sectionList
}
