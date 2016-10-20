/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class, extending {@link FlatMetadataWithUUID}, that represents
 * a tokenization of some textual content. Offers methods to get
 * collections of {@link Parse}, {@link DependencyParse},
 * {@link TaggedTokenGroup}, and {@link SpanLink} objects.
 */
@FreeBuilder
public abstract class Tokenization implements FlatMetadataWithUUID {

  private static final Logger LOGGER = LoggerFactory.getLogger(Tokenization.class);

  Tokenization() {
  }

  public abstract Map<UUID, Parse> getIdToParseMap();

  public abstract Map<UUID, DependencyParse> getIdToDependencyParseMap();

  public abstract Map<UUID, TaggedTokenGroup> getIdToTokenTagGroupMap();

  public abstract List<SpanLink> getSpanLinks();

  /**
   * @return a zero-based map of integer indices to their respective {@link Token}s
   */
  public abstract Map<Integer, Token> getIndexToTokenMap();

  public abstract String getType();

  public static class Builder extends Tokenization_Builder {
    public Builder() {
      // defaults: UUID
      super.setUUID(UUID.randomUUID());
    }

    @Override
    public Tokenization build() {
      Tokenization ptkz = super.build();

      // validate token indices by crawling the list
      // and ensuring tokens go from 0..n
      Map<Integer, Token> idxToTM = ptkz.getIndexToTokenMap();
      Iterator<Entry<Integer, Token>> iter = idxToTM.entrySet().iterator();
      for (int i = 0; i < idxToTM.size(); i++) {
        Map.Entry<Integer, Token> e = iter.next();
        LOGGER.debug("Got map entry: K: {} / V: {}", e.getKey(), e.getValue().toString());
        Integer idx = e.getKey();
        if (idx != i)
          throw new IllegalArgumentException("Token should have index " + i + ", but the index was " + idx);
      }

      // TokenTaggings: each tagged token index
      // must be present in token set.
      final Set<Integer> idxs = idxToTM.keySet();
      ptkz.getIdToTokenTagGroupMap().values()
          .stream()
          // stream of TokenTaggings
          .flatMap(ptt -> ptt.getIndexToTaggedTokenMap().keySet().stream())
          // stream of Ints
          .forEach(i -> {
            if (!idxs.contains(i))
              throw new IllegalArgumentException("TaggedToken references token indexed at: " + i
                  + ", but there is no token with that index.");
          });

      // DepParses: each Dependency field has to be
      // present in the token index set.
      ptkz.getIdToDependencyParseMap().values()
          .stream()
          // stream of DepParses
          .flatMap(dp -> dp.getDependencies().stream())
          // stream of Dependencies
         .forEach(dp -> {
           dp.getGovernorIndex().ifPresent(i -> {
             // currently i can be -1, which has special meaning: no gov found
             if (i >= 0 && !idxs.contains(i))
               throw new IllegalArgumentException("Governor references token indexed at: " + i
                   + ", but there is no token with that index.");
           });
           final int i = dp.getDependentIndex();
           if (!idxs.contains(dp.getDependentIndex()))
             throw new IllegalArgumentException("Dependent references token indexed at: " + i
                 + ", but there is no token with that index.");
         });

      // Constituents: each Constituent that has a start and end
      // must reference a correct token.
      ptkz.getIdToParseMap().values()
          .stream()
          .forEach(pp -> crawlConstituents(pp.getConstituents(), idxs));

      return ptkz;
    }

    private static final void crawlConstituents(Map<Integer, Constituent> cl, Set<Integer> tokIdxS) {
      cl.forEach((i, c) -> {
        // check start idx is a proper token idx
        c.getStart().ifPresent(idx -> {
          if (!tokIdxS.contains(idx))
            throw new IllegalArgumentException("Constituent start references token index: " + idx
                + ", but this token index does not exist in the tokenization.");
        });
        // check end idx is a proper token idx
        c.getEnd().ifPresent(idx -> {
          if (!tokIdxS.contains(idx - 1))
            throw new IllegalArgumentException("Constituent end references token index: " + idx
                + ", but this token index does not exist in the tokenization.");
        });
      });
    }
  }
}
