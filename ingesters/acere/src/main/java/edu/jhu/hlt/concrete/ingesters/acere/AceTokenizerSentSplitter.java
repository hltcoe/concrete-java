package edu.jhu.hlt.concrete.ingesters.acere;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceCharSeq;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceDocument;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceEntityMention;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceRelationMention;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceRelationMentionArgument;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceToken;
import edu.stanford.nlp.ie.machinereading.domains.ace.reader.RobustTokenizer.WordToken;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

/**
 * The methods in this class were copied with modifications from
 * edu.stanford.nlp.ie.machinereading.domains.ace.reader.AceDocument.
 */
public class AceTokenizerSentSplitter {

  private static final Logger log = LoggerFactory.getLogger(AceTokenizerSentSplitter.class);

  // list of tokens which mark sentence boundaries
  private final static String[] sentenceFinalPunc = new String[] { ".", "!", "?" };
  private static final Set<String> sentenceFinalPuncSet = new HashSet<String>();

  static {
    // set up sentenceFinalPuncSet
    for (int i = 0; i < sentenceFinalPunc.length; i++)
      sentenceFinalPuncSet.add(sentenceFinalPunc[i]);
  }

  public static List<List<WordToken>> tokenizeAndSentenceSegment(AceDocument apfDoc, Communication comm) {
    List<List<WordToken>> sentences = new ArrayList<List<WordToken>>();

    // Create the initial tokenization and sentence segmentation using Stanford
    // tools.
    for (Section section : comm.getSectionList()) {
      TextSpan secSpan = section.getTextSpan();
      String input = comm.getText().substring(secSpan.getStart(), secSpan.getEnding());
      tokenizeAndSentenceSegment(input, secSpan.getStart(), sentences);
    }

    // NOTE: We used to call this when our tokenization was non-destructive.
    // Now, we expand out the entity mentions to encapsulate the tokens instead.
    //
    // Split any token which has an internal entity boundary.
    // sentences = splitTokensForEntities(apfDoc, sentences);

    // Merge sentences such that each relation spans only one sentence.
    sentences = mergeSentencesForRelations(apfDoc, sentences);

    // Log sentences with offsets.
    for (int i = 0; i < sentences.size(); i++) {
      List<WordToken> sent = sentences.get(i);
      log.trace("Sentence i=" + i + ": " + sent);
    }
    // Log sentences as tokens only.
    for (int i = 0; i < sentences.size(); i++) {
      List<WordToken> sent = sentences.get(i);
      StringBuilder sb = new StringBuilder();
      for (WordToken tok : sent) {
        sb.append(tok.getWord());
        sb.append(" ");
      }
      log.trace("Sentence i=" + i + ": " + sb.toString());
    }

    return sentences;
  }

  private static List<List<WordToken>> mergeSentencesForRelations(AceDocument apfDoc, List<List<WordToken>> sentences) {
    // If any relation span contains a sentence boundary, merge the two
    // sentences on either side of that sentence boundary.
    int[] sentBreaks = getSentBoundaries(sentences);
    for (AceRelationMention aRm : apfDoc.getRelationMentions().values()) {
      int start = Integer.MAX_VALUE;
      int end = Integer.MIN_VALUE;
      for (AceRelationMentionArgument aRmArg : aRm.getArgs()) {
        AceCharSeq ext = aRmArg.getContent().getExtent();
        start = Math.min(ext.getByteStart(), start);
        // Note: the entity mention extents in ACE are inclusive, so add one.
        end = Math.max(ext.getByteEnd() + 1, end);
      }
      sentBreaks = mergeIfSpanCrossesSents(sentences, sentBreaks, start, end, aRm.toString());
    }
    // If an entity cross a sentence boundary, merge the two sentences.
    for (AceEntityMention aEm : apfDoc.getEntityMentions().values()) {
      int start = aEm.getExtent().getByteStart();
      // Note: the entity mention extents in ACE are inclusive, so add one.
      int end = aEm.getExtent().getByteEnd() + 1;
      sentBreaks = mergeIfSpanCrossesSents(sentences, sentBreaks, start, end, aEm.toString());
    }
    return sentences;
  }

  /**
   * @param start
   *          Beginning of the span (inclusive).
   * @param end
   *          End of the span (exclusive).
   */
  private static int[] mergeIfSpanCrossesSents(List<List<WordToken>> sentences, int[] sentBreaks, int start, int end,
      String descr) {
    boolean fixing = false;
    do {
      fixing = false;
      for (int i = 0; i < sentBreaks.length; i++) {
        if (start < sentBreaks[i] && sentBreaks[i] < end) {
          // Merge the two sentences.
          List<WordToken> sent1 = sentences.get(i - 1);
          List<WordToken> sent2 = sentences.get(i);

          // Union operation.
          List<WordToken> merged = Stream.concat(sent1.stream(), sent2.stream()).distinct()
              .collect(Collectors.toList());
          sentences.set(i - 1, merged);
          sentences.remove(i);
          log.warn(
              String.format("Merged sents: reason=%s\n\tsent1=%s\n\tsent2=%s", descr, sentStr(sent1), sentStr(sent2)));
          // Reset the sentence boundaries.
          sentBreaks = getSentBoundaries(sentences);
          // Try again.
          fixing = true;
          break; // TODO: could be i--.
        }
      }
    } while (fixing);
    return sentBreaks;
  }

  private static String sentStr(List<WordToken> sent) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < sent.size(); i++) {
      WordToken tok = sent.get(i);
      if (i != 0) {
        sb.append(" ");
      }
      sb.append(tok.getWord());
    }
    return sb.toString();
  }

  private static int[] getSentBoundaries(List<List<WordToken>> sentences) {
    List<Integer> blist = new ArrayList<>();
    for (List<WordToken> sent : sentences) {
      blist.add(sent.get(0).getStart());
    }
    int[] barray = new int[blist.size()];
    for (int i = 0; i < barray.length; i++)
      barray[i] = blist.get(i);
    return barray;
  }

  /**
   * This method is similar to
   * AceSentenceSegmenter.tokenizeAndSegmentSentences(), however, this one uses
   * the PTBTokenizer instead of the RobustTokenizer. In addition, the SGML tags
   * have already divided the text into segments, so this method doesn't need to
   * worry about them when sentence splitting. Lastly, the given offset is added
   * to each of the tokens to account for the section's position in the whole
   * document.
   *
   * @param input
   * @param offset
   * @param outSents
   */
  private static void tokenizeAndSentenceSegment(String input, int offset, List<List<WordToken>> outSents) {
    // now we can split the text into tokens

    StringReader r = new StringReader(input);
    // We use the invertible option:
    //
    // The keys used in it are: TextAnnotation for the tokenized form,
    // OriginalTextAnnotation
    // for the original string, BeforeAnnotation and AfterAnnotation for the
    // whitespace before
    // and after a token, and perhaps CharacterOffsetBeginAnnotation and
    // CharacterOffsetEndAnnotation to record token begin/after end character
    // offsets, if they
    // were specified to be recorded in TokenFactory construction. (Like the
    // String class, begin
    // and end are done so end - begin gives the token length.) Default is
    // false.
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(r, new CoreLabelTokenFactory(),
        "invertible,ptb3Escaping=true");
    List<CoreLabel> coreLabelList = tokenizer.tokenize();
    r.close();

    // Add the offset and create WordTokens
    List<WordToken> tokenList = new ArrayList<>();
    for (CoreLabel cl : coreLabelList) {
      String whitespaceBefore = cl.get(CoreAnnotations.BeforeAnnotation.class);
      int newLineCount = 0;
      for (int i = 0; i < whitespaceBefore.length(); i++) {
        if (whitespaceBefore.charAt(i) == '\n') {
          newLineCount++;
        }
      }
      WordToken tok = new WordToken(cl.get(CoreAnnotations.TextAnnotation.class),
          cl.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
          cl.get(CoreAnnotations.CharacterOffsetEndAnnotation.class), newLineCount);
      tok.setStart(tok.getStart() + offset);
      tok.setEnd(tok.getEnd() + offset);
      tokenList.add(tok);
    }

    // and group the tokens into sentences
    List<List<WordToken>> sentences = new ArrayList<List<WordToken>>();
    ArrayList<WordToken> currentSentence = new ArrayList<WordToken>();
    int quoteCount = 0;
    for (int i = 0; i < tokenList.size(); i++) {
      WordToken token = tokenList.get(i);
      String tokenText = token.getWord();

      // start a new sentence if we skipped 2+ lines (after datelines, etc.)
      // or we hit some SGML
      if (token.getNewLineCount() > 1 || AceToken.isSgml(tokenText)) {
        // if (AceToken.isSgml(tokenText)) {
        if (currentSentence.size() > 0)
          sentences.add(currentSentence);
        currentSentence = new ArrayList<WordToken>();
        quoteCount = 0;
      }

      currentSentence.add(token);
      if (tokenText.equals("\""))
        quoteCount++;

      // start a new sentence whenever we hit sentence-final punctuation
      if (sentenceFinalPuncSet.contains(tokenText)) {
        // include quotes after EOS
        if (i < tokenList.size() - 1 && quoteCount % 2 == 1 && tokenList.get(i + 1).getWord().equals("\"")) {
          WordToken quoteToken = tokenList.get(i + 1);
          currentSentence.add(quoteToken);
          quoteCount++;
          i++;
        }
        if (currentSentence.size() > 0)
          sentences.add(currentSentence);
        currentSentence = new ArrayList<WordToken>();
        quoteCount = 0;
      }

      // start a new sentence when we hit an SGML tag
      else if (AceToken.isSgml(tokenText)) {
        if (currentSentence.size() > 0)
          sentences.add(currentSentence);
        currentSentence = new ArrayList<WordToken>();
        quoteCount = 0;
      }
    }
    if (currentSentence.size() > 0)
      sentences.add(currentSentence);

    outSents.addAll(sentences);
    int numSentToks = 0;
    for (List<WordToken> sent : sentences) {
      numSentToks += sent.size();
    }
    log.trace(String.format("# orig tokens=%d # sent tokens=%d", tokenList.size(), numSentToks));
  }

}
