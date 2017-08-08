package edu.jhu.hlt.concrete.ingesters.conll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/** Many Sentences comprising a Document */
public class Conll2011Document {

  private static final Logger LOGGER = LoggerFactory.getLogger(Conll2011Document.class);

  private final Conll2011 conll2011;
  private String id;
  private String communicationType;
  private List<Conll2011Sentence> sentences;
  private EntityMentionSet corefMentions;
  private EntitySet corefClusters;
  private EntityMentionSet nerEms;
  private SituationMentionSet propBankSrlSituationMentions;
  private Communication comm;

  public Conll2011Document(Conll2011 conll2011, String id, String communicationType) {
    this.conll2011 = conll2011;
    this.id = id;
    this.communicationType = communicationType;
    sentences = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public String getPart() {
    return sentences.get(0).getPart();
  }

  public void add(Conll2011Sentence s) {
    if (id == null)
      id = s.getDocId();
    else if (!id.equals(s.getDocId()))
      throw new IllegalArgumentException();
    sentences.add(s);
  }

  public int numSentence() {
    return sentences.size();
  }

  public List<Conll2011Sentence> getSentences() {
    return sentences;
  }

  private void addCoref(Communication comm) {
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory(comm);
    AnalyticUUIDGenerator g = f.create();
    corefMentions = new EntityMentionSet();
    corefMentions.setUuid(g.next());
    corefMentions.setMetadata(Conll2011.META_COREF);
    corefMentions.setMentionList(new ArrayList<>());
    Map<String, List<EntityMention>> clusters = new HashMap<>();
    int addedMentions = 0;
    for (Conll2011Sentence s : sentences) {
      // Get the entity mentions in this sentence
      Map<String, List<EntityMention>> c = s.getCoref(g);
      // Add these mentions to the EntityMentionSet
      for (List<EntityMention> ems : c.values()) {
        for (EntityMention em : ems) {
          corefMentions.addToMentionList(em);
          addedMentions++;
        }
      }
      // Merge into the document-level view of the entities
      for (Map.Entry<String, List<EntityMention>> se : c.entrySet()) {
        String clustId = se.getKey();
        List<EntityMention> existingMentions = clusters.get(clustId);
        if (existingMentions == null) {
          existingMentions = new ArrayList<>();
          clusters.put(clustId, existingMentions);
        }
        existingMentions.addAll(se.getValue());
      }
    }
    int addedEntities = 0;
    corefClusters = new EntitySet();
    corefClusters.setUuid(g.next());
    corefClusters.setMetadata(Conll2011.META_COREF);
    corefClusters.setMentionSetId(corefMentions.getUuid());
    corefClusters.setEntityList(new ArrayList<>());
    for (Map.Entry<String, List<EntityMention>> cluster : clusters.entrySet()) {
      addedEntities++;
      Entity ent = new Entity();
      ent.setUuid(g.next());
      ent.setConfidence(1);
      for (EntityMention em : cluster.getValue())
        ent.addToMentionIdList(em.getUuid());
      corefClusters.addToEntityList(ent);
    }
    comm.addToEntitySetList(corefClusters);
    comm.addToEntityMentionSetList(corefMentions);
    if (conll2011.warnOnEmptyCoref && (addedMentions == 0 || addedEntities == 0)) {
      LOGGER.warn("addedMentions=" + addedMentions
          + " addedEntities=" + addedEntities
          + " communication=" + comm.getId());
    }
  }

  public Communication convertToConcrete() {
    if (comm != null)
      return comm;
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    comm = new Communication();
    comm.setId(id);
    comm.setUuid(g.next());
    comm.setType(communicationType);
    comm.setMetadata(Conll2011.META_GENERAL);

    // Tokenization for the words
    // TokenTagging for the POS tags
    // Parse for the constituency parse
    // TokenTagging for NER labels
    String sectionNum = null;
    Section section = null;
    for (Conll2011Sentence sent : sentences) {
      if (sectionNum == null || !sent.getPart().equals(sectionNum)) {
        if (section != null)
          comm.addToSectionList(section);
        section = new Section();
        section.setUuid(g.next());
        section.setKind(Conll2011.SECTION_TYPE);
        sectionNum = sent.getPart();
      }
      section.addToSentenceList(sent.convertToConcrete(g));
    }
    assert section != null;
    comm.addToSectionList(section);

    //  SituationMentionSet for the SRL labels
    propBankSrlSituationMentions = new SituationMentionSet();
    propBankSrlSituationMentions.setUuid(g.next());
    propBankSrlSituationMentions.setMetadata(Conll2011.META_SRL);
    propBankSrlSituationMentions.setMentionList(new ArrayList<>());
    for (Conll2011Sentence s : sentences) {
      for (int pai = 0; pai < s.getNumPredicates(); pai++) {
        SituationMention sm = s.getPredArg(pai, g);
        assert sm.getTokens() != null;
        propBankSrlSituationMentions.addToMentionList(sm);
      }
    }
    comm.addToSituationMentionSetList(propBankSrlSituationMentions);

    // EntitySet and EntityMentionSet for the coref labels
    addCoref(comm);

    // EntityMentionSet for the NER labels
    if (this.conll2011.addNerAsEntityMentionSet) {
      nerEms = new EntityMentionSet();
      nerEms.setUuid(g.next());
      nerEms.setMetadata(Conll2011.META_NER);
      nerEms.setMentionList(new ArrayList<>());
      for (Conll2011Sentence s : sentences)
        for (EntityMention em : s.getNerEntityMentions())
          nerEms.addToMentionList(em);
      comm.addToEntityMentionSetList(nerEms);
    }

    return comm;
  }
}
