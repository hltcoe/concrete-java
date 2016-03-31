package edu.jhu.hlt.concrete.ingesters.kbp2015;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.MentionArgument;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Takes {@link Communication}s representing documents in LDC2015E77 and adds
 * the stand off annotations contained in LDC2015E100:
 *   tac_kbp_2015_english_cold_start_slot_filling_evaluation_queries_v2.xml
 *   tac_kbp_2015_cold_start_evaluation_assessment_results_batches_00-05.tab
 *
 * Works by reading in all of raw communications (with option to add sharding if
 * needed), then add a {@link SituationMention} for every row in the assessment
 * file where both the relation and query entity providence are deemed correct.
 *
 * @see data/LDC2015E100/LDC2015E100_TAC_KBP_2015_English_Cold_Start_Evaluation_Assessment_Results_V3.1/README.txt
 *
 * @author travis
 */
public class SlotFillStandoffData {

/*
 * I'm going to make one Situation per row in:
f=data/LDC2015E100/LDC2015E100_TAC_KBP_2015_English_Cold_Start_Evaluation_Assessment_Results_V3.1/data/tac_kbp_2015_cold_start_evaluation_assessment_results_batches_00-05.tab
awk -F"\t" '{print $6, $7}' <$f | sort | uniq -c | sort -rn
 205885 W W
  66661 C C
  18304 C S
   8396 X C
   4131 C L
   3833 X S
   2189 C W
    858 X W
    334 X L
 * For now I'll just take the C,C rows
 */

/* What are common (correct) slot filler types?
awk -F"\t" '$6=="C" && $7=="C" {print $2}' <$f | awk -F":" '{print $2, $3}' | sort | uniq -c | sort -rn | head -n 30
  26229 per title
   4848 gpe headquarters_in_city
   4444 org employees_or_members
   2234 gpe member_of
   2160 gpe residents_of_country
   2039 org students
   1803 gpe residents_of_city
   1413 gpe employees_or_members
   1391 org founded_by
   1265 gpe births_in_country
   1257 per origin
   1214 org members
   1017 gpe births_in_stateorprovince
    979 org top_members_employees
    952 org alternate_names
    940 per age
    883 per employee_or_member_of
    882 gpe births_in_city
    773 org subsidiaries
    731 per charges
    698 gpe deaths_in_city
    660 org city_of_headquarters
    628 org date_founded
    581 gpe headquarters_in_stateorprovince
    498 per siblings
    488 per city_of_birth
    481 org country_of_headquarters
    474 per organizations_founded
    462 gpe deaths_in_country
    350 per date_of_birth
 */

/* How many supporting mentions are there linking to the queried entity?
awk -F"\t" '$6 == "C" && $7 == "C" {print $3}' $f | awk -F":" '{print NF}' | sort | uniq -c | sort -rn
  38455 2
  15129 3
   8212 4
   4865 5
 */

/* How many supporting mentions are there filling a slot?
awk -F"\t" '$6 == "C" && $7 == "C" {print $5}' $f | awk -F":" '{print NF}' | sort | uniq -c | sort -rn
  64041 2
   2490 3
     73 4
     57 5
 */

  /**
    Column 1: Response ID, formatted as queryId_hopLevel_responseCount

    Column 2: A concatenation of Cold Start query ID and the relevant
              slot name, separated by a colon

    Column 3: Provenance for the relation between the query entity and
              slot filler, consisting of up to 4 triples in the format
              'docid:startoffset-endoffset' separated by a comma.

    Column 4: A slot filler (possibly normalized, e.g., for dates;
              otherwise, should appear in the provenance document)
              [column 5 from the submission files]

    Column 5: Provenance for the slot filler string. This is either a
              single span (docid:startoffset-endoffset) from the
              document where the canonical slot filler string was
              extracted, or (in the case when the slot filler string
              has been normalized) a set of up to two
              docid:startoffset-endoffset spans, separated by a comma,
              that indicate the base strings that were used to
              generate the normalized slot filler string. [column 6
              from the submissions files]

    Column 6: Assessment of slot filler (Column 5 of the submission
              file) with respect to the text regions defined by the
              relation provenance and filler provenance (Columns 4 and
              6 of the submission file, respectively).  '0' in the
              input files [generated by NIST/LDC; not in submissions
              files] Values can be:

                C - Correct
                X - Inexact
                W - Wrong

    Column 7: Assessment of relation provenance (Column 4 of the
              submission file).  Values can be:

                C - Correct
                L - Inexact (Long)
                S - Inexact (Short)
                W - Wrong 

    Column 8: NIST Equivalence class ID; Query ID concatenated
              with LDC's equivalence class ID, separated by a colon,
              for responses in which column 6 is Correct or Inexact.
              Note that equivalence classes are not cross-query,therefore
              there are some cases of duplicate strings that have unique
              equivalence class IDS.
   * e.g.
   * CSSF15_ENG_11632007cb_0_001     CSSF15_ENG_11632007cb:gpe:births_in_country     ENG_NW_001278_20130214_F00011JDX:1448-1584      Agriculture     ENG_NW_001278_20130214_F00011JDX:1505-1515      W       W       0
   */
  public static class AssessmentFileLine {
    String line;
    String queryId;
    String slot;
    String[] queryEntityProvidence;   // values are providence strings, i.e. <docId>:<start>-<end>
    String normalizedFiller;
    String[] slotFillerProvidence;    // values are providence strings, i.e. <docId>:<start>-<end>
    char labelSlotFill;
    char labelRelationProvidence;
    String nistEquivClassId;

    public AssessmentFileLine(String line) {
      this.line = line;
      String[] toks = line.split("\t");
      assert toks.length == 8;
      String[] queryAndSlot = toks[1].split(":", 2);
      queryId = queryAndSlot[0];
      slot = queryAndSlot[1];
      queryEntityProvidence = toks[2].split(",");
      normalizedFiller = toks[3];
      slotFillerProvidence = toks[4].split(",");
      assert toks[5].length() == 1;
      labelSlotFill = toks[5].charAt(0);
      assert toks[6].length() == 1;
      labelRelationProvidence = toks[6].charAt(0);
      nistEquivClassId = toks[7];
    }

    /**
     * returns a set of indices (i,j) such that queryEntityProvidence[i] and
     * slotFillerProvidence[j] appear in the same document.
     */
    public List<Pair<Integer, Integer>> commonDocumentProvidence() {
      List<Pair<Integer, Integer>> lp = new ArrayList<>();
      for (int i = 0; i < queryEntityProvidence.length; i++) {
        String di = getDocFromProvidence(queryEntityProvidence[i]);
        for (int j = 0; j < slotFillerProvidence.length; j++) {
          String dj = getDocFromProvidence(slotFillerProvidence[j]);
          if (di.equals(dj))
            lp.add(Pair.of(i, j));
        }
      }
      return lp;
    }
    public static String getDocFromProvidence(String providence) {
      String[] idAndOffsets = providence.split(":");
      assert idAndOffsets.length == 2;
      assert idAndOffsets[1].split("-").length == 2;
      return idAndOffsets[0];
    }
    public static TextSpan getLocationFromProvidence(String providence) {
      String[] idAndOffsets = providence.split(":");
      assert idAndOffsets.length == 2;
      String[] startEnd = idAndOffsets[1].split("-");
      assert startEnd.length == 2;
      return new TextSpan(Integer.parseInt(startEnd[0]), Integer.parseInt(startEnd[1]));
    }
  }


  /**
   * Holds a {@link Communication} and related information.
   */
  private class AnnotationHolder {
    Communication comm;
    AnalyticUUIDGenerator uuidGen;
    SituationMentionSet situations;
    /** Doesn't modify given {@link Communication} */
    public AnnotationHolder(Communication c, long timestamp) {
      this.comm = c;
      this.uuidGen = new AnalyticUUIDGeneratorFactory(c).create();
      this.situations = new SituationMentionSet();
      this.situations.setUuid(uuidGen.next());
      AnnotationMetadata meta = new AnnotationMetadata();
      meta.setTimestamp(timestamp);
      meta.setTool("TAC KBP 2015 Cold Start Slot Filling");
      this.situations.setMetadata(meta);
    }
    public int getNumMentions() {
      return situations.getMentionListSize();
    }
    public void add(SituationMention s) {
      situations.addToMentionList(s);
    }
    /** returns a new {@link Communication} with a new {@link SituationMentionSet} */
    public Communication buildCommunication() {
      Communication c = new Communication(comm);
      c.addToSituationMentionSetList(situations);
      return c;
    }
  }

  private Map<String, AnnotationHolder> id2comm;
  private Args cliArgs;

  public SlotFillStandoffData(Args cliArgs) {
    this.cliArgs = cliArgs;
    this.id2comm = new HashMap<>();
  }

  /**
   * @param comms is a set of ingested {@link Communication}s comprising the
   * LDC2015E77 corpus.
   */
  public void addRawCommunications(Iterator<Communication> comms) {
    long timestamp = System.currentTimeMillis() / 1000;
    this.id2comm = new HashMap<>();
    while (comms.hasNext()) {
      Communication c = comms.next();
      String id = c.getId();
      AnnotationHolder a = new AnnotationHolder(c, timestamp);
      AnnotationHolder old = id2comm.put(id, a);
      assert old == null;
    }
  }
  public void readRawCommunications(File f) throws FileNotFoundException, IOException {
    System.out.println("reading raw Communications (expected *.tgz) from " + f.getPath());
    try (FileInputStream fis = new FileInputStream(f)) {
      TarGzArchiveEntryCommunicationIterator itr = new TarGzArchiveEntryCommunicationIterator(fis);
      addRawCommunications(itr);
    }
  }

  public void addAnnotations(File assessmentFile) throws IOException {
    System.out.println("reading assessment annotations from " + assessmentFile.getPath()
        + " skipOverMissingCommunications=" + cliArgs.skipMissingCommunications);
    if (!assessmentFile.isFile())
      throw new IllegalArgumentException("not a file: " + assessmentFile.getPath());
    try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(assessmentFile)))) {
      for (String line = r.readLine(); line != null; line = r.readLine()) {
        addAnnotations(line);
      }
    }
  }

  /**
   * Finds query and slot evidence which are in the same document and adds them
   * to the corresponding {@link Communication} (provided at construction) as
   * a new {@link SituationMention}.
   */
  public void addAnnotations(String line) {
    AssessmentFileLine afl = new AssessmentFileLine(line);
    if (afl.labelRelationProvidence != 'C' || afl.labelSlotFill != 'C')
      return;
    for (Pair<Integer, Integer> ij : afl.commonDocumentProvidence()) {
      String queryEntProv = afl.queryEntityProvidence[ij.getLeft()];
      String slotFillProv = afl.slotFillerProvidence[ij.getRight()];
      String docId = AssessmentFileLine.getDocFromProvidence(queryEntProv);
      AnnotationHolder a = id2comm.get(docId);
      if (a == null) {
        if (cliArgs.skipMissingCommunications) {
          continue;
        } else {
          throw new RuntimeException("found docId=" + docId
              + " with no corresponding Communication, source: " + line);
        }
      }
      SituationMention sm = new SituationMention();
      sm.setUuid(a.uuidGen.next());
      MentionArgument queryEntArg = makeMentionArgument(queryEntProv, "queryEntity", sm, a.comm);
      MentionArgument slotFillArg = makeMentionArgument(slotFillProv, "slotFiller", sm, a.comm);
      sm.setSituationType("kbp2015-coldstart-slotfill");
      sm.setSituationKind(afl.slot);
      sm.addToArgumentList(queryEntArg);
      sm.addToArgumentList(slotFillArg);
      a.add(sm);
    }
  }

  private MentionArgument makeMentionArgument(String providence, String role, SituationMention sm, Communication c) {
    TextSpan loc = AssessmentFileLine.getLocationFromProvidence(providence);
    TokenRefSequence trs;
    if (cliArgs.inputIsTokenized) {
      trs = TextSpanToTokens.resolve(loc, c);
    } else {
      trs = new TokenRefSequence();
      trs.setTokenIndexList(Collections.emptyList());
      trs.setTokenizationId(new UUID("invalid"));
    }
    trs.setTextSpan(loc);
    MentionArgument arg = new MentionArgument();
    arg.setRole(role);
    arg.setTokens(trs);
    arg.setSituationMentionId(sm.getUuid());
    return arg;
  }

  public void writeCommunications(File f) throws Exception {
    long start = System.currentTimeMillis();
    System.out.println("writing " + id2comm.size() + " Communications to " + f.getPath());
    int noAnno = 0, succ = 0;
    try (OutputStream os = new FileOutputStream(f);
        GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
        TarArchiver arch = new TarArchiver(gout)) {
      for (AnnotationHolder a : id2comm.values()) {
        if (a.getNumMentions() > 0) {
          succ++;
          Communication withAnnos = a.buildCommunication();
          arch.addEntry(new ArchivableCommunication(withAnnos));
        } else {
          noAnno++;
          System.err.println("didn't observe any annotations in: " + a.comm.getId());
        }
      }
    }
    double time = (System.currentTimeMillis() - start) / 1000d;
    System.out.printf("wrote out %d communications, %d had no annotations and weren't output, took %.1f seconds\n", succ, noAnno, time);
  }

  static class Args {
    @Parameter(names={"--assessment"}, description="TSV with query assesments", required=true)
    String inputAssessmentFile;
    @Parameter(names={"--input"}, description="Input file containing all raw Communications", required=true)
    String inputCommunicationFile;
    @Parameter(names={"--inputIsTokenized"}, description="If true, resolve TextSpans to TokenRefSequence")
    boolean inputIsTokenized = false;
    @Parameter(names={"--output"}, description="File to write all annotated Communications to", required=true)
    String outputCommunicationFile;
    @Parameter(names={"--skipMissing"}, description="Skip annotations whose document can't be found")
    boolean skipMissingCommunications = false;
  }

  public static void main(String[] args) throws Exception {
    Args a = new Args();
    new JCommander(a, args);
    SlotFillStandoffData sfsd = new SlotFillStandoffData(a);
    sfsd.readRawCommunications(new File(a.inputCommunicationFile));
    sfsd.addAnnotations(new File(a.inputAssessmentFile));
    sfsd.writeCommunications(new File(a.outputCommunicationFile));
  }
}
