/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.annotatednyt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.jhu.hlt.annotatednyt.AnnotatedNYTDocument;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.NITFInfo;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * Class that implements {@link Communicationizable} given an
 * {@link AnnotatedNYTDocument}.
 */
public class CommunicationizableAnnotatedNYTDocument implements Communicationizable, SafeTooledAnnotationMetadata {

  private final AnnotatedNYTDocument anytd;

  public CommunicationizableAnnotatedNYTDocument(AnnotatedNYTDocument anytd) {
    this.anytd = anytd;
  }
  
  public NITFInfo extractNITFInfo(AnnotatedNYTDocument cDoc) {
    final NITFInfo ni = new NITFInfo();

    cDoc.getAlternateURL().ifPresent(url -> ni.setAlternateURL(url.toString()));
    cDoc.getArticleAbstract().ifPresent(a -> ni.setArticleAbstract(a));
    cDoc.getAuthorBiography().ifPresent(ab -> ni.setAuthorBiography(ab));
    cDoc.getBanner().ifPresent(i -> ni.setBanner(i));
    ni.setBiographicalCategoryList(cDoc.getBiographicalCategories());
    cDoc.getColumnName().ifPresent(i -> ni.setColumnName(i));
    cDoc.getColumnNumber().ifPresent(i -> ni.setColumnNumber(i));
    cDoc.getCorrectionDate().ifPresent(dt -> ni.setCorrectionDate(dt.getTime()));

    cDoc.getCorrectionText().ifPresent(i -> ni.setCorrectionText(i));
    ni.setCredit(cDoc.getCredit());

    cDoc.getDayOfWeek().ifPresent(i -> ni.setDayOfWeek(i));
    ni.setDescriptorList(cDoc.getDescriptors());
    cDoc.getFeaturePage().ifPresent(i -> ni.setFeaturePage(i));
    ni.setGeneralOnlineDescriptorList(cDoc.getGeneralOnlineDescriptors());
    ni.setGuid(cDoc.getGuid());
    cDoc.getKicker().ifPresent(i -> ni.setKicker(i));
    ni.setLeadParagraphList(cDoc.getLeadParagraphAsList());
    ni.setLocationList(cDoc.getLocations());
    ni.setNameList(cDoc.getNames());
    cDoc.getNewsDesk().ifPresent(i -> ni.setNewsDesk(i));
    cDoc.getNormalizedByline().ifPresent(i -> ni.setNormalizedByline(i));
    ni.setOnlineDescriptorList(cDoc.getOnlineDescriptors());
    cDoc.getOnlineHeadline().ifPresent(i -> ni.setOnlineHeadline(i));
    cDoc.getOnlineLeadParagraph().ifPresent(i -> ni.setOnlineLeadParagraph(i));
    ni.setOnlineLocationList(cDoc.getOnlineLocations());
    ni.setOnlineOrganizationList(cDoc.getOnlineOrganizations());
    ni.setOnlinePeople(cDoc.getOnlinePeople());
    ni.setOnlineSectionList(cDoc.getOnlineSectionAsList());
    ni.setOnlineTitleList(cDoc.getOnlineTitles());
    ni.setOrganizationList(cDoc.getOrganizations());
    cDoc.getPage().ifPresent(p -> ni.setPage(p));
    ni.setPeopleList(cDoc.getPeople());
    cDoc.getPublicationDate().ifPresent(d -> ni.setPublicationDate(d.getTime()));
    cDoc.getPublicationDayOfMonth().ifPresent(p -> ni.setPublicationDayOfMonth(p));
    cDoc.getPublicationMonth().ifPresent(p -> ni.setPublicationMonth(p));
    cDoc.getPublicationYear().ifPresent(p -> ni.setPublicationYear(p));
    cDoc.getSection().ifPresent(i -> ni.setSection(i));
    cDoc.getSeriesName().ifPresent(i -> ni.setSeriesName(i));
    cDoc.getSlug().ifPresent(i -> ni.setSlug(i));
    ni.setTaxonomicClassifierList(cDoc.getTaxonomicClassifiers());
    ni.setTitleList(cDoc.getTitles());
    ni.setTypesOfMaterialList(cDoc.getTypesOfMaterial());

    cDoc.getUrl().ifPresent(url -> ni.setUrl(url.toString()));
    cDoc.getWordCount().ifPresent(wc -> ni.setWordCount(wc));

    return ni;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable#toCommunication()
   */
  @Override
  public Communication toCommunication() {
    final String localId = "AnnotatedNYT-" + this.anytd.getGuid();
    // shouldn't really throw - inputs are valid
    try {
      CommunicationMetadata cmd = new CommunicationMetadata();
      cmd.setNitfInfo(this.extractNITFInfo(this.anytd));
      Communication c = CommunicationFactory.create(localId);
      c.setCommunicationMetadata(cmd);
      c.setType("news");
      int ctr = 0;
      StringBuilder ctxt = new StringBuilder();
      List<StringStringStringTuple> sstList = this.extractTuples().sequential().collect(Collectors.toList());
      final int lSize = sstList.size();
      for (int i = 0; i < lSize; i++) {
        final StringStringStringTuple t = sstList.get(i);
        final String skind = t.getS1();
        final String slabel = t.getS2();
        final String txt = t.getS3();
        final int txtlen = txt.length();
        final TextSpan ts = new TextSpan(ctr, ctr + txtlen);
        final Section s = SectionFactory.fromTextSpan(ts, skind);
        s.setLabel(slabel);
        c.addToSectionList(s);
        ctxt.append(txt);
        if (i + 1 != lSize) {
          ctxt.append(System.lineSeparator());
          ctxt.append(System.lineSeparator());
          ctr += txtlen + 2;
        }
      }

      c.setText(ctxt.toString());
      return c;
    } catch (ConcreteException e) {
      // something went way wrong
      throw new RuntimeException(e);
    }
  }

  private Stream<StringStringStringTuple> extractTuples() {
    Stream.Builder<StringStringStringTuple> stream = Stream.builder();
    // kind, label, content triples
    this.anytd.getHeadline().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Headline", str)));
    this.anytd.getOnlineHeadline().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Online Headline", str)));
    this.anytd.getByline().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Byline", str)));
    this.anytd.getDateline().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Dateline", str)));
    this.anytd.getArticleAbstract().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Article Abstract", str)));
    this.anytd.getLeadParagraphAsList().stream()
        .filter(i -> !i.isEmpty())
        .forEach(str -> stream.add(StringStringStringTuple.create("Other", "Lead Paragraphs", str)));
    this.anytd.getOnlineLeadParagraphAsList().stream()
        .filter(str -> !str.isEmpty())
        .forEach(str -> stream.add(StringStringStringTuple.create("Other", "Online Lead Paragraph", str)));
    // judicious use of null - going to thrift, so is OK
    this.anytd.getBodyAsList()
        .stream()
        .filter(str -> !str.isEmpty()).forEach(str -> stream.add(StringStringStringTuple.create("Passage", null, str)));
    this.anytd.getCorrectionText().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Correction Text", str)));
    this.anytd.getKicker().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Kicker", str)));
    return stream.build();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return CommunicationizableAnnotatedNYTDocument.class.getSimpleName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    return new ArrayList<String>();
  }
}
