/*
 * 
 */
package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;

/**
 * A {@link SuperCommunication} with a {@link Map} of Section UUIDs
 * to {@link Section}s.
 * 
 * @author max
 *
 */
public class SectionedSuperCommunication extends SuperCommunication {

  protected final Map<String, Section> sectionIdToSectionMap;
  
  /**
   * 
   */
  public SectionedSuperCommunication(Communication comm) {
    super(comm);
    this.sectionIdToSectionMap = this.sectionIdToSectionMap();
  }
  
  /**
   * Iterate over all {@link SectionSegmentation}s and create a {@link Map} of [SectionID, Section].
   * 
   * @return a {@link Map} whose keys contain {@link Section} {@link UUID} strings, and whose values contain {@link Section} objects with that id string.
   */
  private final Map<String, Section> sectionIdToSectionMap() {
    final Map<String, Section> toRet = new HashMap<String, Section>();

    if (this.comm.isSetSectionSegmentations())
      for (SectionSegmentation ss : this.comm.getSectionSegmentations())
        if (ss.isSetSectionList())
          for (Section s : ss.getSectionList())
            toRet.put(s.getUuid(), s);

    return toRet;
  }
  
  public final Map<String, Section> getSectionIdToSectionMap() {
    return new HashMap<>(this.sectionIdToSectionMap);
  }
  
  public final Set<String> getSectionIds() {
    return new HashSet<>(this.sectionIdToSectionMap.keySet());
  }
  
  public Set<String> enumerateSectionKinds() {
    Set<String> ss = new HashSet<>();
    List<Section> sectList = new ArrayList<>(this.getSectionIdToSectionMap().values());
    for (Section s : sectList)
      ss.add(s.getKind());
    
    return ss;
  }
}
