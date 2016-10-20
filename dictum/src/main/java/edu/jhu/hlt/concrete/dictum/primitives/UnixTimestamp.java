/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.primitives;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;

/**
 * Class representing a unix timestamp. This class attempts fuzzy validation
 * by checking the passed in <code>long</code> against the current system time. If it
 * is 10x greater, an {@link IllegalArgumentException} is thrown, as the value
 * likely is a default Java system time (from {@link System#currentTimeMillis()})
 * or is otherwise invalid.
 */
@FreeBuilder
public abstract class UnixTimestamp {
  UnixTimestamp() {
  }

  public abstract long getTS();

  /**
   * @param ts the <code>long</code> to wrap and validate
   * @return a {@link UnixTimestamp}
   */
  public static UnixTimestamp create(long ts) {
    return new UnixTimestamp.Builder().setTS(ts).build();
  }

  /**
   * @return a {@link UnixTimestamp} with the current system's unix time
   */
  public static UnixTimestamp now() {
    return new Builder().build();
  }

  public static class Builder extends UnixTimestamp_Builder {
    /**
     * Return a {@link Builder} with the default timestamp
     * of the current local time.
     */
    public Builder() {
      super.setTS(System.currentTimeMillis() / 1000);
    }

    @Override
    public Builder setTS(long ts) {
      if (Rules.isReasonableUnixTimestamp().negate().test(ts))
        throw new IllegalArgumentException("TS is over 10x greater than current time. Unlikely to be a correct unix timestamp.");
      else
        return super.setTS(ts);
    }
  }
}
