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
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.analytics.base.Analytic;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.tokenized.CachedTokenizationCommunication;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * An example of a tool that produces {@link SituationMentionSet}s by tagging any
 * some basic actions.
 */
public class BasicSituationTagger implements Analytic {

  private static final Logger logger = LoggerFactory.getLogger(BasicSituationTagger.class);

  private final Set<String> basicActionSet;

  /**
   *
   */
  public BasicSituationTagger() {
    this.basicActionSet = new HashSet<>();
    basicActionSet.add("fled");
    basicActionSet.add("returned");
  }

  @Override
  public Communication annotate (Communication c) throws AnalyticException {
    final Communication cpy = new Communication(c);
    // SuperCommunication sc = new SuperCommunication(cpy);
    try {
      CachedTokenizationCommunication ctc = new CachedTokenizationCommunication(cpy);
      SituationMentionSet sms = new SituationMentionSet();
      sms.setMetadata(TooledMetadataConverter.convert(this));
      sms.setUuid(UUIDFactory.newUUID());

      List<Tokenization> tokenizations = new ArrayList<>(ctc.getTokenizations());
      for (Tokenization t : tokenizations) {
        TokenList tl = t.getTokenList();
        for (Token tk : tl.getTokenList()) {
          String tokenText = tk.getText();
          logger.debug("Working with token text: {}", tokenText);
          String lc = tokenText.toLowerCase();
          if (this.basicActionSet.contains(lc)) {
            TokenRefSequence trs = new TokenRefSequence();
            trs.setTokenizationId(t.getUuid());
            trs.setTextSpan(tk.getTextSpan());
            trs.addToTokenIndexList(tk.getTokenIndex());

            SituationMention sm = new SituationMention();
            sm.setUuid(UUIDFactory.newUUID());
            sm.setConfidence(1.0d);
            sm.setSituationType("Fact");

            sm.setText(tokenText);
            sm.setTokens(trs);

            sms.addToMentionList(sm);
          }
        }
      }

      if (sms.getMentionListSize() < 1)
        sms.setMentionList(new ArrayList<SituationMention>());
      cpy.addToSituationMentionSetList(sms);
      return cpy;
    } catch (MiscommunicationException e) {
      throw new AnalyticException(e);
    }
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
    return BasicSituationTagger.class.getName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }
}
