package edu.jhu.hlt.concrete.simpleaccumulo;

/**
 * Class designed to answer the question:
 * "has it been X seconds since I called you last?"
 *
 * @author travis
 */
public class TimeMarker {
  private long lastMark;
  private long firstMark;
  private int numMarks;

  public TimeMarker() {
    lastMark = firstMark = System.currentTimeMillis();
    numMarks = 0;
  }

  /**
   * @return true if enoughSeconds have passed since this this method last
   * returned true, or if this method has never been called.
   */
  public boolean enoughTimePassed(double enoughSeconds) {
    long time = System.currentTimeMillis();
    numMarks++;
    double elapsed = (time - lastMark) / 1000d;
    if (elapsed >= enoughSeconds) {
      lastMark = time;
      return true;
    } else {
      return false;
    }
  }

  public double secondsSinceLastMark() {
    return (System.currentTimeMillis() - lastMark) / 1000d;
  }

  public double secondsSinceFirstMark() {
    return (System.currentTimeMillis() - firstMark) / 1000d;
  }

  public int numMarks() {
    return numMarks;
  }

  public double secondsPerMark() {
    assert numMarks > 0;
    return secondsSinceFirstMark() / numMarks;
  }
}
