package edu.jhu.hlt.concrete.ingesters.conll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Dependency;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.serialization.CommunicationTarGzSerializer;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * NOTE: This description is taken from the Google Syntaxnet (aka parsey mcparseface)
 * documentation: bazel-syntaxnet/syntaxnet/text_formats.cc
 *
 * CoNLL document format reader for dependency annotated corpora.
 * The expected format is described e.g. at http://ilk.uvt.nl/conll/#dataformat
 *
 * Data should adhere to the following rules:
 *   - Data files contain sentences separated by a blank line.
 *   - A sentence consists of one or tokens, each one starting on a new line.
 *   - A token consists of ten fields described in the table below.
 *   - Fields are separated by a single tab character.
 *   - All data files will contains these ten fields, although only the ID
 *     column is required to contain non-dummy (i.e. non-underscore) values.
 * Data files should be UTF-8 encoded (Unicode).
 *
 * Fields:
 * 1  ID:      Token counter, starting at 1 for each new sentence and increasing
 *             by 1 for every new token.
 * 2  FORM:    Word form or punctuation symbol.
 * 3  LEMMA:   Lemma or stem.
 * 4  CPOSTAG: Coarse-grained part-of-speech tag or category.
 * 5  POSTAG:  Fine-grained part-of-speech tag. Note that the same POS tag
 *             cannot appear with multiple coarse-grained POS tags.
 * 6  FEATS:   Unordered set of syntactic and/or morphological features.
 * 7  HEAD:    Head of the current token, which is either a value of ID or '0'.
 * 8  DEPREL:  Dependency relation to the HEAD.
 * 9  PHEAD:   Projective head of current token.
 * 10 PDEPREL: Dependency relation to the PHEAD.
 *
 * This CoNLL reader is compatible with the CoNLL-U format described at
 *   http://universaldependencies.org/format.html
 * Note that this reader skips CoNLL-U multiword tokens and ignores the last two
 * fields of every line, which are PHEAD and PDEPREL in CoNLL format, but are
 * replaced by DEPS and MISC in CoNLL-U.
 *
 * @author travis
 */
public class CoNLLX {

	public String rawTool;

	// You can set any of these to null in order to disable ingesting this form.
	// Otherwise it will ingest each if there is a single token which isn't "-".
	public String lemmaTool;
	public String cposTool;
	public String posTool;
	public String featsTool;
	public String depTool;		// HEAD and DEPREL
	public String pdepTool;		// PHEAD and PDEPREL

	private long timestamp;

	// Needs a reference to a Communication whose UUID is already set to work.
    private AnalyticUUIDGenerator uuidGen;

    public void setCommunicationId(Communication c) {
    	this.uuidGen = new AnalyticUUIDGeneratorFactory(c).create();
    }

	public CoNLLX(String toolName) {
		if (toolName == null)
			throw new IllegalArgumentException();
		this.rawTool = toolName;
		this.lemmaTool = toolName;
		this.cposTool = toolName;
		this.posTool = toolName;
		this.featsTool = toolName;
		this.depTool = toolName;
		this.pdepTool = toolName + "/projective";
		this.timestamp = System.currentTimeMillis() / 1000;
	}

	public static boolean isDash(String d) {
		d = d.trim();
		return "-".equals(d) || "_".equals(d);
	}

	private TokenTagging makeTags(String toolName, String taggingType, Tokenization addTo) {
		if (toolName == null)
			return null;
		TokenTagging t = new TokenTagging();
		t.setUuid(uuidGen.next());
		AnnotationMetadata m = new AnnotationMetadata();
		m.setTool(toolName);
		m.setTimestamp(timestamp);
		t.setMetadata(m);
		t.setTaggingType(taggingType);
		addTo.addToTokenTaggingList(t);
		return t;
	}

	private void addTag(String tag, TokenTagging tags) {
		if (tags == null)
			return;
		TaggedToken t = new TaggedToken();
		t.setTag(tag);
		tags.addToTaggedTokenList(t);
	}

	private DependencyParse makeDeps(String toolName, Tokenization addTo) {
		if (toolName == null)
			return null;
		DependencyParse deps = new DependencyParse();
		deps.setUuid(uuidGen.next());
		AnnotationMetadata m = new AnnotationMetadata();
		m.setTool(toolName);
		m.setTimestamp(timestamp);
		deps.setMetadata(m);
		addTo.addToDependencyParseList(deps);
		return deps;
	}

	private void addDep(String gov, String dep, String deprel, DependencyParse addTo) {
		if (addTo == null)
			return;
		if (isDash(gov.trim()))
			return;
		Dependency d = new Dependency();
		d.setGov(Integer.parseInt(gov) - 1);
		d.setDep(Integer.parseInt(dep) - 1);
		d.setEdgeType(deprel);
		addTo.addToDependencyList(d);
	}

	private static void pruneEmptyTaggings(Tokenization t) {
		List<TokenTagging> nonEmpty = new ArrayList<>();
		for (TokenTagging tt : t.getTokenTaggingList()) {
			boolean keep = false;
			for (TaggedToken ttt : tt.getTaggedTokenList()) {
				if (!isDash(ttt.getTag())) {
					keep = true;
					break;
				}
			}
			if (keep)
				nonEmpty.add(tt);
		}
		t.setTokenTaggingList(nonEmpty);
	}

	private static void pruneEmptyDeps(Tokenization t) {
		List<DependencyParse> keep = new ArrayList<>();
		for (DependencyParse d : t.getDependencyParseList()) {
			if (d.getDependencyList() == null || d.getDependencyListSize() == 0)
				continue;
			keep.add(d);
		}
		t.setDependencyParseList(keep);
	}

	public Tokenization convert(List<String> lines) {
		if (uuidGen == null)
			throw new IllegalStateException("must call setCommunicationId first");
		Tokenization t = new Tokenization();
		t.setMetadata(new AnnotationMetadata());
		t.getMetadata().setTool(rawTool);
		t.getMetadata().setTimestamp(timestamp);
		t.setUuid(uuidGen.next());
		t.setKind(TokenizationKind.TOKEN_LIST);

		TokenList words = new TokenList();
		t.setTokenList(words);

		TokenTagging lemma = makeTags(lemmaTool, "LEMMA", t);
		TokenTagging cpos = makeTags(cposTool, "POS/coarse", t);
		TokenTagging pos = makeTags(posTool, "POS", t);
		TokenTagging feats = makeTags(featsTool, "FEATS", t);
		DependencyParse deps = makeDeps(depTool, t);
		DependencyParse pdeps = makeDeps(pdepTool, t);
		int n = lines.size();
		for (int i = 0; i < n; i++) {
			String[] fields = lines.get(i).split("\t");
			assert fields.length == 8 || fields.length == 10;
			assert i+1 == Integer.parseInt(fields[0]) : "token ids must be one-indexed";

			Token token = new Token();
			token.setText(fields[1]);
			words.addToTokenList(token);

			addTag(fields[2], lemma);
			addTag(fields[3], cpos);
			addTag(fields[4], pos);
			addTag(fields[5], feats);

			addDep(fields[6], fields[0], fields[7], deps);
			if (fields.length == 10)
				addDep(fields[8], fields[0], fields[9], pdeps);
		}
		pruneEmptyTaggings(t);
		pruneEmptyDeps(t);
		return t;
	}

	public static Communication readCommunication(File inputConll, String toolName, boolean showTiming) throws Exception {
		Communication c = new Communication();
		c.setUuid(new UUID(java.util.UUID.randomUUID().toString()));
		c.setId(inputConll.getPath());
		c.setType("document");
		c.setMetadata(new AnnotationMetadata());
		c.getMetadata().setTool(toolName);
		c.getMetadata().setTimestamp(System.currentTimeMillis() / 1000);

		CoNLLX conll = new CoNLLX(toolName);
		conll.setCommunicationId(c);

		int sent = 0, tok = 0;
		List<String> cur = new ArrayList<>();
		List<Tokenization> sentences = new ArrayList<>();
		long start = System.currentTimeMillis();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(inputConll)))) {
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				if (line.isEmpty()) {
					// End of sentence
					sent++;
					Tokenization t = conll.convert(cur);
					sentences.add(t);
					cur.clear();

					if (showTiming && sent % 1000 == 0) {
						int sec = (int) ((System.currentTimeMillis() - start) / 1000);
						System.err.println("read " + sent + " sentences and " + tok + " tokens in " + sec + " seconds");
					}
				} else {
					tok++;
					cur.add(line);
				}
			}
		}
		if (!cur.isEmpty()) {
			System.err.println("file should end in newline, including last sentence anyway");
			sentences.add(conll.convert(cur));
			cur.clear();
			sent++;
		}
		if (showTiming) {
			int sec = (int) ((System.currentTimeMillis() - start) / 1000);
			System.err.println("read " + sent + " sentences and " + tok + " tokens in " + sec + " seconds");
		}

		Section section = new Section();
		section.setUuid(conll.uuidGen.next());
		section.setKind("passage");
		c.addToSectionList(section);
		for (Tokenization t : sentences) {
			Sentence snt = new Sentence();
			snt.setUuid(conll.uuidGen.next());
			snt.setTokenization(t);
			section.addToSentenceList(snt);
		}

		return c;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("please provide:");
			System.err.println("1) a tool name which will be applied to all annotations");
			System.err.println("2) an input CoNLL-X formateed file (8 or 10 column TSV)");
			System.err.println("3) an output Concrete Communication file");
			return;
		}
		String toolName = args[0];
		File inputConll = new File(args[1]);
		if (!inputConll.isFile())
			throw new RuntimeException("input doesn't exist: " + inputConll.getPath());
		File outputConcrete = new File(args[2]);

		boolean showTiming = true;
		Communication c = readCommunication(inputConll, toolName, showTiming);

		long start = System.currentTimeMillis();
		System.err.println("writing Communication to " + outputConcrete.toString());
		CommunicationTarGzSerializer ts = new TarGzCompactCommunicationSerializer();
		ts.toTarGz(Arrays.asList(c), outputConcrete.toPath());
		int sec = (int) ((System.currentTimeMillis() - start) / 1000);
		System.err.println("done writing in " + sec + " seconds");
	}
}
