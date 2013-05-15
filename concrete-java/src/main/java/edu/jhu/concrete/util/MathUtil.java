/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.concrete.util;

/**
 * @author max
 *
 */
public class MathUtil {

    /**
     * 
     */
    private MathUtil() {
        // TODO Auto-generated constructor stub
    }

    private static double _ADD_LOGS_MAX_DIFF = Math.log(1e-30);

    /**
     * Given two numbers logx=log(x) and logy=log(y), return log(x+y).
     * Conceptually, this is the same as returning log(e**(logx)+e**(logy)), but
     * the actual implementation avoids overflow errors that could result from
     * direct computation.
     */
    public static double addLogs(double logx, double logy) {
        if (logx < logy + _ADD_LOGS_MAX_DIFF)
            return logy;
        if (logy < logx + _ADD_LOGS_MAX_DIFF)
            return logx;
        double base = Math.min(logx, logy);
        return base + Math.log(Math.exp(logx - base) + Math.exp(logy - base));
    }

    /**
     * Return true if a (interepted as an unsigned string of bits) is less than
     * b (interpreted likewise)
     */
    static boolean unsignedLessThan(long a, long b) {
        long shifted_a = (a >>> 1);
        long shifted_b = (b >>> 1);
        return ((shifted_a == shifted_b) ? ((a & 1) < (b & 1)) : (shifted_a < shifted_b));
    }
}
