/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.annotatednyt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.nytlabs.corpus.NYTCorpusDocument;

import edu.jhu.hlt.annotatednyt.AnnotatedNYTDocument;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.NITFInfo;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.concrete.validation.ValidatableTextSpan;

/**
 * Class that implements {@link Communicationizable} given an
 * {@link AnnotatedNYTDocument}.
 */
public class CommunicationizableAnnotatedNYTDocument implements Communicationizable, SafeTooledAnnotationMetadata {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationizableAnnotatedNYTDocument.class);

  private final AnnotatedNYTDocument anytd;

  public CommunicationizableAnnotatedNYTDocument(AnnotatedNYTDocument anytd) {
    this.anytd = anytd;
  }

  public static AnnotatedNYTDocument fromConcrete(NITFInfo nitf) throws ConcreteException {
    NYTCorpusDocument nd = new NYTCorpusDocument();
    try {
      Optional<String> ourl = Optional.ofNullable(nitf.getAlternateURL());
      if (ourl.isPresent())
        nd.setAlternateURL(new URL(ourl.get()));
      Optional.ofNullable(nitf.getArticleAbstract()).ifPresent(nd::setArticleAbstract);
      Optional.ofNullable(nitf.getAuthorBiography()).ifPresent(nd::setAuthorBiography);
      Optional.ofNullable(nitf.getBanner()).ifPresent(nd::setBanner);
      Optional.ofNullable(nitf.getBiographicalCategoryList())
          .ifPresent(nd::setBiographicalCategories);
      Optional.ofNullable(nitf.getColumnName()).ifPresent(nd::setColumnName);
      if (nitf.isSetColumnNumber())
        nd.setColumnNumber(nitf.getColumnNumber());
      if (nitf.isSetCorrectionDate())
        nd.setCorrectionDate(new Date(nitf.getCorrectionDate()));
      Optional.ofNullable(nitf.getCorrectionText()).ifPresent(nd::setCorrectionText);
      Optional.ofNullable(nitf.getCredit()).ifPresent(nd::setCredit);
      Optional.ofNullable(nitf.getDayOfWeek()).ifPresent(nd::setDayOfWeek);
      Optional.ofNullable(nitf.getDescriptorList()).ifPresent(nd::setDescriptors);
      Optional.ofNullable(nitf.getFeaturePage()).ifPresent(nd::setFeaturePage);
      Optional.ofNullable(nitf.getGeneralOnlineDescriptorList()).ifPresent(nd::setGeneralOnlineDescriptors);
      if (nitf.isSetGuid())
        nd.setGuid(nitf.getGuid());

      Optional.ofNullable(nitf.getKicker()).ifPresent(nd::setKicker);
      // newline split
      Optional.ofNullable(nitf.getLeadParagraphList()).ifPresent(lpl -> {
        nd.setLeadParagraph(Joiner.on("\n").join(lpl));
      });

      Optional.ofNullable(nitf.getLocationList()).ifPresent(nd::setLocations);
      Optional.ofNullable(nitf.getNameList()).ifPresent(nd::setNames);
      Optional.ofNullable(nitf.getNewsDesk()).ifPresent(nd::setNewsDesk);
      Optional.ofNullable(nitf.getNormalizedByline()).ifPresent(nd::setNormalizedByline);
      Optional.ofNullable(nitf.getOnlineDescriptorList()).ifPresent(nd::setOnlineDescriptors);
      Optional.ofNullable(nitf.getOnlineHeadline()).ifPresent(nd::setOnlineHeadline);
      Optional.ofNullable(nitf.getOnlineLeadParagraph()).ifPresent(nd::setOnlineLeadParagraph);
      Optional.ofNullable(nitf.getOnlineLocationList()).ifPresent(nd::setOnlineLocations);
      Optional.ofNullable(nitf.getOnlineOrganizationList()).ifPresent(nd::setOnlineOrganizations);
      Optional.ofNullable(nitf.getOnlinePeople()).ifPresent(nd::setOnlinePeople);
      // these are split by '; '. so join them up.
      Optional.ofNullable(nitf.getOnlineSectionList()).ifPresent(s -> {
        nd.setOnlineSection(Joiner.on("; ").join(s));
      });

      Optional.ofNullable(nitf.getOnlineTitleList()).ifPresent(nd::setOnlineTitles);
      Optional.ofNullable(nitf.getOrganizationList()).ifPresent(nd::setOrganizations);
      if (nitf.isSetPage())
        nd.setPage(nitf.getPage());

      Optional.ofNullable(nitf.getPeopleList()).ifPresent(nd::setPeople);
      if (nitf.isSetPublicationDate())
        nd.setPublicationDate(new Date(nitf.getPublicationDate()));
      if (nitf.isSetPublicationDayOfMonth())
        nd.setPublicationDayOfMonth(nitf.getPublicationDayOfMonth());
      if (nitf.isSetPublicationMonth())
        nd.setPublicationMonth(nitf.getPublicationMonth());
      if (nitf.isSetPublicationYear())
        nd.setPublicationYear(nitf.getPublicationYear());
      if (nitf.isSetSection())
        nd.setSection(nitf.getSection());

      Optional.ofNullable(nitf.getSeriesName()).ifPresent(nd::setSeriesName);
      Optional.ofNullable(nitf.getSlug()).ifPresent(nd::setSlug);
      Optional.ofNullable(nitf.getTaxonomicClassifierList()).ifPresent(nd::setTaxonomicClassifiers);
      Optional.ofNullable(nitf.getTitleList()).ifPresent(nd::setTitles);
      Optional.ofNullable(nitf.getTypesOfMaterialList()).ifPresent(nd::setTypesOfMaterial);
      if (nitf.isSetUrl())
        nd.setUrl(new URL(nitf.getUrl()));
      if (nitf.isSetWordCount())
        nd.setWordCount(nitf.getWordCount());
    } catch (MalformedURLException e) {
      throw new ConcreteException(e);
    }

    return new AnnotatedNYTDocument(nd);
  }

  public static NITFInfo extractNITFInfo(AnnotatedNYTDocument cDoc) {
    final NITFInfo ni = new NITFInfo();

    cDoc.getAlternateURL().ifPresent(url -> ni.setAlternateURL(url.toString()));
    cDoc.getArticleAbstract().ifPresent(ni::setArticleAbstract);
    cDoc.getAuthorBiography().ifPresent(ni::setAuthorBiography);
    cDoc.getBanner().ifPresent(ni::setBanner);
    ni.setBiographicalCategoryList(cDoc.getBiographicalCategories());
    cDoc.getColumnName().ifPresent(ni::setColumnName);
    cDoc.getColumnNumber().ifPresent(ni::setColumnNumber);
    cDoc.getCorrectionDate().ifPresent(dt -> ni.setCorrectionDate(dt.getTime()));

    cDoc.getCorrectionText().ifPresent(ni::setCorrectionText);
    ni.setCredit(cDoc.getCredit());

    cDoc.getDayOfWeek().ifPresent(ni::setDayOfWeek);
    ni.setDescriptorList(cDoc.getDescriptors());
    cDoc.getFeaturePage().ifPresent(ni::setFeaturePage);
    ni.setGeneralOnlineDescriptorList(cDoc.getGeneralOnlineDescriptors());
    ni.setGuid(cDoc.getGuid());
    cDoc.getKicker().ifPresent(ni::setKicker);
    ni.setLeadParagraphList(cDoc.getLeadParagraphAsList());
    ni.setLocationList(cDoc.getLocations());
    ni.setNameList(cDoc.getNames());
    cDoc.getNewsDesk().ifPresent(ni::setNewsDesk);
    cDoc.getNormalizedByline().ifPresent(ni::setNormalizedByline);
    ni.setOnlineDescriptorList(cDoc.getOnlineDescriptors());
    cDoc.getOnlineHeadline().ifPresent(ni::setOnlineHeadline);
    cDoc.getOnlineLeadParagraph().ifPresent(ni::setOnlineLeadParagraph);
    ni.setOnlineLocationList(cDoc.getOnlineLocations());
    ni.setOnlineOrganizationList(cDoc.getOnlineOrganizations());
    ni.setOnlinePeople(cDoc.getOnlinePeople());
    ni.setOnlineSectionList(cDoc.getOnlineSectionAsList());
    ni.setOnlineTitleList(cDoc.getOnlineTitles());
    ni.setOrganizationList(cDoc.getOrganizations());
    cDoc.getPage().ifPresent(ni::setPage);
    ni.setPeopleList(cDoc.getPeople());
    cDoc.getPublicationDate().ifPresent(d -> ni.setPublicationDate(d.getTime()));
    cDoc.getPublicationDayOfMonth().ifPresent(ni::setPublicationDayOfMonth);
    cDoc.getPublicationMonth().ifPresent(ni::setPublicationMonth);
    cDoc.getPublicationYear().ifPresent(ni::setPublicationYear);
    cDoc.getSection().ifPresent(ni::setSection);
    cDoc.getSeriesName().ifPresent(ni::setSeriesName);
    cDoc.getSlug().ifPresent(ni::setSlug);
    ni.setTaxonomicClassifierList(cDoc.getTaxonomicClassifiers());
    ni.setTitleList(cDoc.getTitles());
    ni.setTypesOfMaterialList(cDoc.getTypesOfMaterial());

    cDoc.getUrl().ifPresent(url -> ni.setUrl(url.toString()));
    cDoc.getWordCount().ifPresent(ni::setWordCount);

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
      AnnotationMetadata md = TooledMetadataConverter.convert(this);
      CommunicationMetadata cmd = new CommunicationMetadata();
      cmd.setNitfInfo(extractNITFInfo(this.anytd));
      AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
      AnalyticUUIDGenerator g = f.create();
      Communication c = new Communication();
      c.setUuid(g.next());
      c.setId(localId);
      c.setMetadata(md);
      c.setCommunicationMetadata(cmd);
      c.setType("news");
      int ctr = 0;
      StringBuilder ctxt = new StringBuilder();
      List<StringStringStringTuple> sstList = this.extractTuples().sequential().collect(Collectors.toList());
      final int lSize = sstList.size();
      LOGGER.debug("{} tuples need to be processed.", lSize);
      for (int i = 0; i < lSize; i++) {
        LOGGER.debug("Current ctr position: {}", ctr);
        final StringStringStringTuple t = sstList.get(i);
        final String skind = t.getS1();
        LOGGER.debug("Section kind: {}", skind);
        final String slabel = t.getS2();
        LOGGER.debug("Section label: {}", slabel);
        final String txt = t.getS3();
        LOGGER.debug("Section text: {}", txt);
        final int txtlen = txt.length();
        LOGGER.debug("Preparing to create text span with boundaries: {}, {}", ctr, ctr + txtlen);
        final TextSpan ts = new TextSpan(ctr, ctr + txtlen);
        final ValidatableTextSpan vts = new ValidatableTextSpan(ts);
        if (!vts.isValid()) {
          LOGGER.info("TextSpan was not valid for label: {}. Omitting from output.", slabel);
          continue;
        }

        final Section s = new SectionFactory(g).fromTextSpan(ts, skind);
        s.setLabel(slabel);
        c.addToSectionList(s);
        ctxt.append(txt);
        if (i + 1 != lSize) {
          ctxt.append(System.lineSeparator());
          ctxt.append(System.lineSeparator());
          ctr += txtlen + 2;
        }
      }

      final String ctxtstr = ctxt.toString();
      LOGGER.debug("Text length: {}", ctxtstr.length());
      c.setText(ctxtstr);
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
    this.anytd.getByline().ifPresent(str -> {
      if (!str.isEmpty())
        stream.add(StringStringStringTuple.create("Other", "Byline", str));
      else
        LOGGER.debug("Byline was empty; not adding a zone for it.");
    });

    this.anytd.getDateline().ifPresent(str -> stream.add(StringStringStringTuple.create("Other", "Dateline", str)));
    this.anytd.getArticleAbstract().ifPresent(str -> {
      if (!str.isEmpty())
        stream.add(StringStringStringTuple.create("Other", "Article Abstract", str));
      else
        LOGGER.debug("Article abstract was empty; not adding a zone for it.");
    });
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
    this.anytd.getKicker().ifPresent(str -> {
      if (!str.isEmpty())
        stream.add(StringStringStringTuple.create("Other", "Kicker", str));
      else
        LOGGER.debug("Kicker was empty; not adding a zone for it.");
    });
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
