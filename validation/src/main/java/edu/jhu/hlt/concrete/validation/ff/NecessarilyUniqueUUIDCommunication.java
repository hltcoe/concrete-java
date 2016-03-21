/**
 *
 */
package edu.jhu.hlt.concrete.validation.ff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.validation.ff.entity.EntityMentionSets;
import edu.jhu.hlt.concrete.validation.ff.entity.EntitySets;
import edu.jhu.hlt.concrete.validation.ff.entity.ValidEntity;
import edu.jhu.hlt.concrete.validation.ff.entity.ValidEntityMention;
import edu.jhu.hlt.concrete.validation.ff.entity.ValidEntityMentionSet;
import edu.jhu.hlt.concrete.validation.ff.entity.ValidEntitySet;
import edu.jhu.hlt.concrete.validation.ff.structure.Sections;
import edu.jhu.hlt.concrete.validation.ff.structure.ValidSection;

/**
 * Implementation of {@link ValidCommunication} that provides validation and utility
 * wrapping of some communication-based methods.
 * <br><br>
 * This class that has the following validation checks:
 * <ul>
 * <li>all necessarily unique {@link UUID} objects within a {@link Communication}
 * that should be unique are truly unique,</li>
 * <li>all EntitySet EntityMentionSet UUID pointers are actually present
 * in the communication,</li>
 * <li>all mentionIDs of Entity objects are actually present in the
 * EntityMentionSets of the communication</li>
 * </ul>
 * <br><br>
 * An {@link InvalidConcreteStructException} is thrown if as soon as a check fails.
 */
public class NecessarilyUniqueUUIDCommunication extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Communication>
    implements ValidCommunication {

  protected final Set<ValidUUID> necessarilyUniqueUUIDs = new HashSet<>();
  protected final Set<ValidUUID> entityMentionUUIDSet;

  private final ValidUUID uuid;
  private final List<ValidSection> sl;
  private final List<ValidEntityMentionSet> emsl;
  private final List<ValidEntitySet> esl;

  /**
   * @param c the {@link Communication} to wrap
   * @throws InvalidConcreteStructException on validation failure
   */
  NecessarilyUniqueUUIDCommunication(final Communication c) throws InvalidConcreteStructException {
    super(c);
    this.uuid = UUIDs.validate(c.getUuid());
    this.addNecessarilyUniqueUUID(this.uuid);
    final int nSects = c.getSectionListSize();
    this.sl = new ArrayList<>(nSects);
    if (nSects > 0)
      for (Section s : c.getSectionList())
        this.sl.add(Sections.validate(s));


    final int nEMS = c.getEntityMentionSetListSize();
    this.emsl = new ArrayList<>(nEMS);
    if (nEMS > 0) {
      for (EntityMentionSet ems : c.getEntityMentionSetList()) {
        ValidEntityMentionSet vems = EntityMentionSets.validate(ems);
        this.emsl.add(vems);
      }

      // Validation step: cache list of EntityMention UUIDs
      // to validate Entity pointers later on
      this.entityMentionUUIDSet = this.emsl.stream()
        .flatMap(vems -> vems.getEntityList().stream())
        .map(ValidEntityMention::getUUID)
        .collect(Collectors.toSet());

    } else
      this.entityMentionUUIDSet = new HashSet<>(0);

    // Validate EntitySet ptrs against these
    Set<ValidUUID> vemsUUIDSet = this.emsl.stream()
        .map(ValidEntityMentionSet::getUUID)
        .collect(Collectors.toSet());

    final int nES = c.getEntitySetListSize();
    this.esl = new ArrayList<>(nES);
    if (nES > 0) {
      for (EntitySet es : c.getEntitySetList()) {
        ValidEntitySet ves = EntitySets.validate(es);
        this.esl.add(ves);
        if (ves.getMentionSetUUID().isPresent()) {
          ValidUUID ptr = ves.getMentionSetUUID().get();
          if (!vemsUUIDSet.contains(ptr))
            throw new InvalidConcreteStructException("At least one EntitySet "
                + "EntityMentionSet UUID pointer is not present in this communication.");
        }

        List<Set<ValidUUID>> mentionUUIDSetList = ves.getEntityList().stream()
            .map(ValidEntity::getMentionIDSet)
            .collect(Collectors.toList());

        // Assert that all pointers of mentions are actually
        // represented inside the entity mentions in this communication
        for (Set<ValidUUID> ve : mentionUUIDSetList)
          if (!this.entityMentionUUIDSet.containsAll(ve))
            throw new InvalidConcreteStructException("At least one Entity UUID Mention pointer "
                + "does not point to a mention in this Communication.");
      }
    }
  }

  @Override
  public ValidUUID getUUID() {
    return this.uuid;
  }

  @Override
  public List<ValidEntitySet> getEntitySetList() {
    return new ArrayList<>(this.esl);
  }
}
