/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.lid;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Load a .json file of ISO3 languages. Syntax is as follows:
 * <br>
 * <pre>
 *   [
 *     { 'abbr' : 'foo',
 *       'name' : 'fooooooo' } ,
 *     ...
 *   ]
 * </pre>
 * <br>
 * Everything is nastily loaded in a static block. As long as the resource
 * is present, this should be OK.
 */
public class ValidISO3Languages {
  private static final String isoFileNameStr = "iso-639-3.json.gz";
  public static final ObjectMapper om = new ObjectMapper();

  private static final ImmutableMap<String, String> iso3AbbrNameMap;
  private static final Set<ISO6393Abbreviation> abbrSet;
  static {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(isoFileNameStr);
        GzipCompressorInputStream gin = new GzipCompressorInputStream(is);) {
      try {
        List<ISO3AbbreviationNameBean> beanList = om.readValue(gin, new TypeReference<List<ISO3AbbreviationNameBean>>() { });
        ImmutableMap.Builder<String, String> mb = new ImmutableMap.Builder<>();
        for (ISO3AbbreviationNameBean b : beanList)
          mb.put(b.code, b.name);
        iso3AbbrNameMap = mb.build();
        abbrSet = ImmutableSet.copyOf(iso3AbbrNameMap.keySet().stream()
            .map(ISO6393Abbreviation::fromAbbreviationUnchecked)
            .collect(Collectors.toSet()));
      } catch (IOException e) {
        // unlikely to throw given resource does not change.
        throw new RuntimeException("Failed to deserialize resource.");
      }
    } catch (IOException e) {
      // shall not throw unless resource is removed.
      throw new RuntimeException("Failed to load resource: " + isoFileNameStr + "; this program cannot run without it.");
    }
  }

  /**
   *
   * @param abbr
   *          a {@link String} representing a possible ISO3 abbreviation
   * @return <code>true</code> if a valid identifier
   */
  public static final boolean isValidISO3Abbreviation(final String abbr) {
    return iso3AbbrNameMap.containsKey(abbr);
  }

  /**
   *
   * @param abbr
   *          a {@link String} representing a possible ISO3 abbreviation
   * @return {@link Optional#empty()} if not present, otherwise, an
   *         {@link Optional} {@link String} wrapping the value
   */
  public static final Optional<String> getName(final String abbr) {
    return isValidISO3Abbreviation(abbr) ? Optional.of(iso3AbbrNameMap.get(abbr)) : Optional.empty();
  }

  public static final Set<ISO6393Abbreviation> getAbbreviationSet() {
    return abbrSet;
  }

  public static final Set<String> getNameSet() {
    return ImmutableSet.copyOf(iso3AbbrNameMap.values());
  }
}
