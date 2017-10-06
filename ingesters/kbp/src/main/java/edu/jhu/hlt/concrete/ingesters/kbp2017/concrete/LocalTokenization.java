package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.ingesters.kbp2017.TextSpan;

class LocalTokenization {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalTokenization.class);

  private final Tokenization wrapped;
  private final List<Token> tokenList;
  private final Map<TextSpan, Token> ltsToTokenMap;

  public LocalTokenization(Tokenization tkz) {
    this.wrapped = new Tokenization(tkz);
    if (!this.wrapped.isSetTokenList())
      throw new IllegalArgumentException("depends upon set token list");
    if (this.wrapped.getTokenList().getTokenListSize() < 1)
      throw new IllegalArgumentException("depends upon non-empty token list");
    this.tokenList = ImmutableList.copyOf(this.wrapped.getTokenList().getTokenList());
    ImmutableMap.Builder<TextSpan, Token> mb = ImmutableMap.builder();
    this.tokenList.forEach(t -> mb.put(TextSpan.create(t.getTextSpan()), t));
    this.ltsToTokenMap = mb.build();
  }

  public TextSpan getTextSpan() {
    TextSpan.Builder bldr = new TextSpan.Builder();
    final int tokenListSize = this.tokenList.size();
    bldr.setStart(this.tokenList.get(0).getTextSpan().getStart());
    bldr.setEnd(this.tokenList.get(tokenListSize - 1).getTextSpan().getEnding());
    return bldr.build();
  }

  public Set<TextSpan> getTokenTextSpans() {
    ImmutableSet.Builder<TextSpan> bldr =  ImmutableSet.builder();
    this.wrapped.getTokenList().getTokenList()
      .stream()
      .map(Token::getTextSpan)
      .map(TextSpan::create)
      .forEach(bldr::add);
    return bldr.build();
  }

  public Optional<TokenRefSequence> generateTRS(TextSpan lts) {
    LOGGER.debug("Working span: {}", lts.toString());
    TokenRefSequence trs = new TokenRefSequence();
    trs.setTokenizationId(this.wrapped.getUuid());
    trs.setTextSpan(lts.toConcrete());
    boolean within = false;
    for (Map.Entry<TextSpan, Token> e : this.ltsToTokenMap.entrySet()) {
      final TextSpan tokenTS = e.getKey();
      final Token t = e.getValue();
      // are either within the relevant tokens or not
      if (within) {
        // is the END of the parameter (passed in) span LT/E
        // the START of the current token?
        if (lts.getEnd() <= tokenTS.getStart()) {
          // if yes, that means there's nothing more to go (have already passed over this token)
          break;
        }

        LOGGER.debug("Adding token: {}", t.toString());
        trs.addToTokenIndexList(t.getTokenIndex());
        // is the ending equal?
        if (tokenTS.getEnd() == lts.getEnd()) {
          // done
          break;
        } else if (lts.getEnd() > tokenTS.getEnd()) {
          // more to go
        } else {
          // if the passed in TS ending is < the token ending,
          // then the token is larger than the passed in string expects
          LOGGER.info("Passed in token does not align with end token: parameter {} vs current token {}",
              lts.toString(), tokenTS.toString());
          // break out anyway
          break;
        }
      } else {
        // is this start == passed in start?
        if (tokenTS.getStart() == lts.getStart()) {
          // token is a part of passed in TS
          LOGGER.debug("Token captures span: {}", t.toString());
          trs.addToTokenIndexList(t.getTokenIndex());
          // are the two text spans equal?
          if (tokenTS.equals(lts)) {
            // 1:1 mapping so break out
            LOGGER.debug("Tokens equal; breaking out");
            break;
          } else if (tokenTS.getEnd() > lts.getEnd()) {
            // is the ending of the token >= the passed in TS?
            // if so, print something
            LOGGER.info("Tokenized token is longer than input: {} vs. {}",
                tokenTS.toString(), lts.toString());
            break;
          }
          within = true;
        } else if (tokenTS.within(lts.getStart())) {
          // is this start within the current token?
          LOGGER.info("Non-aligned tokenization beginning: {} vs. target {}", tokenTS.toString(), lts.toString());
          if (tokenTS.getEnd() == lts.getEnd()) {
            // this captures the token
            trs.addToTokenIndexList(t.getTokenIndex());
            break;
          } else {
            within = true;
          }
        }
      }
    }

    if (!trs.isSetTokenIndexList()) {
      LOGGER.warn("Unable to find tokens for span: {}", lts.toString());
      LOGGER.warn("Last token index: {}", this.tokenList.get(this.tokenList.size() - 1));
      return Optional.empty();
    }

    return Optional.of(trs);
  }
}
