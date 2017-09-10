/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleImmutableEntry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;

class Util {
  private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

  static void handleDocumentStartWithDocIDBlock(final XMLEventReader rdr, final Communication c) throws XMLStreamException {
    // "zero" block
    rdr.nextEvent();
    // document block
    XMLEvent docEvent  = rdr.nextEvent();

    // id attr
    Attribute docIDAttr = docEvent.asStartElement().getAttributeByName(QName.valueOf("id"));
    final String docid = docIDAttr.getValue();
    if (!docid.isEmpty())
      c.setId(docid);
  }

  static boolean isSpaceOrUnixNewline(final Character c) {
    return c.equals(' ') || c.equals('\n');
  }

  static int getLeftSpacesPaddingCount(final String str) {
    final int len = str.length();
    for (int i = 0; i < len; i++) {
      Character c = str.charAt(i);
      if (!isSpaceOrUnixNewline(c))
        return i;
    }

    return len;
  }

  static int getRightSpacesPaddingCount(final String str) {
    final int lenIdx = str.length() - 1;
    for (int i = 0; i < lenIdx; i++) {
      Character c = str.charAt(lenIdx - i);
      if (!isSpaceOrUnixNewline(c))
        return i;
    }

    return lenIdx + 1;
  }

  static SimpleImmutableEntry<Integer, Integer> trimSpacing(final String str) {
    final int leftPadding = getLeftSpacesPaddingCount(str);
    LOGGER.trace("Left padding: {}", leftPadding);
    final int rightPadding = getRightSpacesPaddingCount(str);
    LOGGER.trace("Right padding: {}", rightPadding);
    return new SimpleImmutableEntry<Integer, Integer>(leftPadding, rightPadding);
  }

  static Section handleHeadline(XMLEventReader rdr, Communication ptr) throws XMLStreamException {
    if (!ptr.isSetText())
      throw new IllegalArgumentException("need comm with text set.");

    // Headline begin.
    XMLEvent hl = rdr.nextEvent();
    StartElement hlse = hl.asStartElement();
    QName hlqn = hlse.getName();
    final String hlPart = hlqn.getLocalPart();
    LOGGER.debug("QN: {}", hlPart);

    // Headline text.
    Characters hlChars = rdr.nextEvent().asCharacters();
    final int charOff = hlChars.getLocation().getCharacterOffset();
    final int clen = hlChars.getData().length();

    // Construct section, text span, etc.
    final int endTextOffset = charOff + clen;
    final String hlText = ptr.getText().substring(charOff, endTextOffset);
    LOGGER.debug("Got headline text: startOff={}, len={}, endText={}, txt={}",
        charOff, clen, endTextOffset, hlText);
    String trimmed = hlText.trim();
    TextSpan ts;
    if (trimmed.isEmpty()) {
      ts = new TextSpan(charOff, endTextOffset);
    } else {
      SimpleImmutableEntry<Integer, Integer> pads = trimSpacing(hlText);
      ts = new TextSpan(charOff + pads.getKey(), endTextOffset - pads.getValue());
    }

    Section s = new Section();
    s.setKind("headline");
    s.setTextSpan(ts);
    s.addToNumberList(0);
    return s;
  }

  static String getPathContents(Path p) throws IOException {
    try (InputStream is = Files.newInputStream(p);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8);) {
      return IOUtils.toString(bin, StandardCharsets.UTF_8);
    }
  }

  static void setCommunicationTextToPathContents(Path p, Communication ptr) throws IOException {
    ptr.setText(getPathContents(p));
  }
}
