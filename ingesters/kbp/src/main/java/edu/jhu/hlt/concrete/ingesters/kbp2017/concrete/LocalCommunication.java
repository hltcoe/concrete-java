package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.Tokenization;

class LocalCommunication {

  private final Communication c;
  private final String text;

  private final List<Sentence> sentenceList;
  private final Map<UUID, LocalTokenization> sentenceIDToTokensMap;

  public LocalCommunication(Communication c) {
    if (!c.isSetText())
      throw new IllegalArgumentException("must have text");
    this.c = new Communication(c);
    this.text = c.getText();

    ImmutableList.Builder<Sentence> b = ImmutableList.builder();
    if (this.c.isSetSectionList())
      for (Section s : this.c.getSectionList())
        if (s.isSetSentenceList())
          b.addAll(s.getSentenceListIterator());
    this.sentenceList = b.build();

    ImmutableMap.Builder<UUID, LocalTokenization> sidtb = ImmutableMap.builder();
    for (Sentence s : this.sentenceList)
      if (s.isSetTokenization())
        sidtb.put(UUID.fromString(s.getUuid().getUuidString()), new LocalTokenization(s.getTokenization()));

    this.sentenceIDToTokensMap = sidtb.build();
  }

  public Map<UUID, LocalTokenization> sentenceIDToTokenizationMap() {
    return this.sentenceIDToTokensMap;
  }

  public List<Tokenization> getTokenizations() {
    ImmutableList.Builder<Tokenization> b = ImmutableList.builder();
    if (this.c.isSetSectionList())
      for (Section s : this.c.getSectionList())
        if (s.isSetSentenceList())
          for (Sentence st : s.getSentenceList())
            if (st.isSetTokenization())
              b.add(st.getTokenization());
    return b.build();
  }

  public List<Sentence> getSentences() {
    return ImmutableList.copyOf(this.sentenceList);
  }

  public Multimap<UUID, String> getTokenizationIDToTokenStrings() {
    Multimap<UUID, String> mm = ArrayListMultimap.create();
    for (Tokenization tkz : this.getTokenizations()) {
      if (tkz.isSetTokenList()) {
        TokenList tl = tkz.getTokenList();
        if (tl.isSetTokenList() && tl.getTokenListSize() > 0) {
          UUID tkzID = UUID.fromString(tkz.getUuid().getUuidString());
          mm.putAll(tkzID, tl.getTokenList().stream().map(Token::getText).collect(Collectors.toList()));
        }
      }
    }

    return mm;
  }

  public Optional<Sentence> getSentence(TextSpan ts) {
    List<Sentence> filtered = this.sentenceList.stream()
      .filter(st -> {
        final TextSpan lts = st.getTextSpan();
        // start should be LTE passed in text span
        return lts.getStart() <= ts.getStart()
            && lts.getEnding() >= ts.getEnding();
      })
    .collect(Collectors.toList());
    if (filtered.size() > 1)
      throw new IllegalArgumentException("Data integrity problem: a text span"
          + " of a mention did not fit inside a single sentence;"
          + " Passed in TS: " + ts.toString() + "; "
          + " Sentence TSes: " + filtered.stream().map(Sentence::getTextSpan).collect(Collectors.toList()));
    else if (filtered.isEmpty())
      return Optional.empty();
      // throw new IllegalArgumentException("Data integrity problem: a mention was not found");
    else
      return Optional.of(filtered.get(0));
  }
}
