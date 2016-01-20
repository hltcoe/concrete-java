/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.uuid;

import java.util.Random;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.UUID;

/**
 *
 */
public class AnalyticUUIDGeneratorFactory {

  private final String localIDStr;

  private static final Random r = new Random();

  public static final String HEX_ALPHA = "abcdef1234567890";
  public static final int HEX_ALPHA_LEN = HEX_ALPHA.length();

  public AnalyticUUIDGeneratorFactory() {
    this.localIDStr = generateUUIDUnif();
  }

  public AnalyticUUIDGeneratorFactory(Communication comm) {
    this.localIDStr = comm.getUuid().getUuidString();
  }

  public AnalyticUUIDGenerator getGenerator() {
    return new AnalyticUUIDGenerator(this.localIDStr);
  }

  public static class AnalyticUUIDGenerator {
    private final String xPart;
    private final String yPart;

    private int zPart;
    private int zLen;
    private int zBound;

    public AnalyticUUIDGenerator(String uuidstr) {
      UUIDTuple tuple = new UUIDTuple(uuidstr);
      this.xPart = tuple.p1;
      this.yPart = generateHexUnif(tuple.p2.length());

      String zPartStr = generateHexUnif(tuple.p3.length());
      this.zPart = Integer.parseUnsignedInt(zPartStr, 16);
      this.zLen = zPartStr.length();
      this.zBound = (int)Math.pow(2, (4 * this.zLen));
    }

    private String zeroPaddedHex(int k, int pad) {
      String hexStr = Integer.toHexString(k);
      int nonHex = Integer.parseInt(hexStr.substring(2));
      int nZeroes = pad - hexStr.length();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < nZeroes; i++)
        sb.append("0");
      sb.append(nonHex);
      return sb.toString();
    }

    public UUID next() {
      this.zPart = (this.zPart + 1) % this.zBound;
      String lastPart = zeroPaddedHex(zPart, zLen);
      return new UUID(joinUUID(this.xPart, this.yPart, lastPart));
    }
  }

  private static String generateUUIDUnif() {
    return joinUUID(generateHexUnif(12),
        generateHexUnif(8),
        generateHexUnif(12));
  }

  private static String joinUUID(String p1, String p2, String p3) {
    StringBuilder sb = new StringBuilder();
    sb.append(p1.substring(0, 8));
    sb.append("-");
    sb.append(p1.substring(8));
    sb.append("-");
    sb.append(p2.substring(0, 4));
    sb.append("-");
    sb.append(p2.substring(4));
    sb.append("-");
    sb.append(p3);
    return sb.toString();
  }

  public static String generateHexUnif(int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      Character c = HEX_ALPHA.charAt(r.nextInt(HEX_ALPHA_LEN));
      sb.append(c);
    }

    return sb.toString();
  }

  private static class UUIDTuple {
    private final String p1;
    private final String p2;
    private final String p3;

    public UUIDTuple(final String p1, final String p2, final String p3) {
      this.p1 = p1;
      this.p2 = p2;
      this.p3 = p3;
    }

    public UUIDTuple(final String uuidstr) {
      if (!UUIDFactory.isValidUUID(uuidstr))
        throw new IllegalArgumentException("UUID string is not valid for UUID:" + uuidstr);

      String[] spl = uuidstr.split("-");
      this.p1 = spl[0] + spl[1];
      this.p2 = spl[2] + spl[3];
      this.p3 = spl[4];
    }
  }
}


