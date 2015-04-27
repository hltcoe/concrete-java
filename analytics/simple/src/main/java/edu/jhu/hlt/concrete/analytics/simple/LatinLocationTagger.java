/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.simple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.analytics.base.Analytic;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * An example of a tool that produces {@link EntityMentionSet}s by tagging any
 * Latin-esq locations.
 */
public class LatinLocationTagger implements Analytic {

  private static final Logger logger = LoggerFactory.getLogger(LatinLocationTagger.class);
  
  private final Set<String> latinWordSet;
  
  /**
   * 
   */
  public LatinLocationTagger() {
    this.latinWordSet = new HashSet<>();
    latinWordSet.add("capua");
    latinWordSet.add("rome");
  }
  

  @Override
  public Communication annotate(Communication original) throws AnalyticException {
    Communication cpy = new Communication(original);
    SuperCommunication sc = new SuperCommunication(cpy);
    EntityMentionSet ems = new EntityMentionSet();
    ems.setMetadata(TooledMetadataConverter.convert(this));
    ems.setUuid(UUIDFactory.newUUID());

    List<Tokenization> tokenizations = new ArrayList<>(sc.generateTokenizationIdToTokenizationMap().values());
    for (Tokenization t : tokenizations) {
      TokenList tkl = t.getTokenList();
      for (Token tk : tkl.getTokenList()) {
        String tokenText = tk.getText();
        logger.debug("Working with token text: {}", tokenText);
        String lc = tokenText.toLowerCase();
        if (this.latinWordSet.contains(lc)) {
          TokenRefSequence trs = new TokenRefSequence();
          trs.setTokenizationId(t.getUuid());
          trs.setTextSpan(tk.getTextSpan());
          trs.addToTokenIndexList(tk.getTokenIndex());

          EntityMention em = new EntityMention();
          em.setUuid(UUIDFactory.newUUID());
          em.setConfidence(1.0d);
          em.setEntityType("LOC");
          em.setPhraseType("Name");
          em.setText(tokenText);
          em.setTokens(trs);

          ems.addToMentionList(em);
        }
      }
    }

    if (ems.getMentionListSize() < 1)
      ems.setMentionList(new ArrayList<EntityMention>());
    cpy.addToEntityMentionSetList(ems);
    return cpy;

  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return LatinLocationTagger.class.getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }
}
