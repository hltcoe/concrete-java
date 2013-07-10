/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.util;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Graph;

/**
 * Utility class containing static methods to convert various types to and from
 * fixed-length byte arrays.
 */
public class ByteUtil {
	public static int toInt(byte[] bytes) {
		assert(bytes.length>=4);
		return (int)( 
					 (0xff & bytes[0]) << 24 |
					 (0xff & bytes[1]) << 16 |
					 (0xff & bytes[2]) << 8  |
					 (0xff & bytes[3]) << 0
					  );
	}

	public static long toLong(byte[] bytes) {
		assert(bytes.length>=8);
		return ((0xffL & bytes[0]) << 56 |
				(0xffL & bytes[1]) << 48 |
				(0xffL & bytes[2]) << 40 |
				(0xffL & bytes[3]) << 32 |
				(0xffL & bytes[4]) << 24 |
				(0xffL & bytes[5]) << 16 |
				(0xffL & bytes[6]) << 8  |
				(0xffL & bytes[7]) << 0);
	}

	public static long toLong(byte[] bytes, int offset) {
		assert(bytes.length>=(8+offset));
		return ((0xffL & bytes[0+offset]) << 56 |
				(0xffL & bytes[1+offset]) << 48 |
				(0xffL & bytes[2+offset]) << 40 |
				(0xffL & bytes[3+offset]) << 32 |
				(0xffL & bytes[4+offset]) << 24 |
				(0xffL & bytes[5+offset]) << 16 |
				(0xffL & bytes[6+offset]) << 8  |
				(0xffL & bytes[7+offset]) << 0);
	}

	public static Concrete.UUID toUUID(byte[] bytes) {
		assert(bytes.length>=16);
		return Concrete.UUID.newBuilder()
			.setHigh((0xffL & bytes[0])  << 56 |
					 (0xffL & bytes[1])  << 48 |
					 (0xffL & bytes[2])  << 40 |
					 (0xffL & bytes[3])  << 32 |
					 (0xffL & bytes[4])  << 24 |
					 (0xffL & bytes[5])  << 16 |
					 (0xffL & bytes[6])  << 8  |
					 (0xffL & bytes[7])  << 0)
			.setLow((0xffL & bytes[8])  << 56 |
					(0xffL & bytes[9])  << 48 |
					(0xffL & bytes[10]) << 40 |
					(0xffL & bytes[11]) << 32 |
					(0xffL & bytes[12]) << 24 |
					(0xffL & bytes[13]) << 16 |
					(0xffL & bytes[14]) << 8  |
					(0xffL & bytes[15]) << 0)
			.build();
	}

	public static byte[] fromUUID(Concrete.UUID uuid) {
		long msb = uuid.getHigh();
		long lsb = uuid.getLow();
		return new byte[] {
			(byte)((msb>>56) & 0xff),
			(byte)((msb>>48) & 0xff),
			(byte)((msb>>40) & 0xff),
			(byte)((msb>>32) & 0xff),
			(byte)((msb>>24) & 0xff),
			(byte)((msb>>16) & 0xff),
			(byte)((msb>> 8) & 0xff),
			(byte)((msb>> 0) & 0xff),
			(byte)((lsb>>56) & 0xff),
			(byte)((lsb>>48) & 0xff),
			(byte)((lsb>>40) & 0xff),
			(byte)((lsb>>32) & 0xff),
			(byte)((lsb>>24) & 0xff),
			(byte)((lsb>>16) & 0xff),
			(byte)((lsb>> 8) & 0xff),
			(byte)((lsb>> 0) & 0xff),
		};
	}

	public static Graph.EdgeId toEdgeId(byte[] bytes) {
		assert(bytes.length>=32);
		Concrete.UUID v1 = toUUID(bytes);
		Concrete.UUID v2 = Concrete.UUID.newBuilder()
			.setHigh((0xffL & bytes[16])  << 56 |
					 (0xffL & bytes[17])  << 48 |
					 (0xffL & bytes[18])  << 40 |
					 (0xffL & bytes[19])  << 32 |
					 (0xffL & bytes[20])  << 24 |
					 (0xffL & bytes[21])  << 16 |
					 (0xffL & bytes[22])  << 8  |
					 (0xffL & bytes[23])  << 0)
			.setLow((0xffL & bytes[24])  << 56 |
					(0xffL & bytes[25])  << 48 |
					(0xffL & bytes[26]) << 40 |
					(0xffL & bytes[27]) << 32 |
					(0xffL & bytes[28]) << 24 |
					(0xffL & bytes[29]) << 16 |
					(0xffL & bytes[30]) << 8  |
					(0xffL & bytes[31]) << 0)
			.build();
		return Graph.EdgeId.newBuilder()
			.setV1(v1).setV2(v2).build();
	}

	public static byte[] fromEdgeId(Graph.EdgeId edgeId) {
		long msb1 = edgeId.getV1().getHigh();
		long lsb1 = edgeId.getV1().getLow();
		long msb2 = edgeId.getV2().getHigh();
		long lsb2 = edgeId.getV2().getLow();
		return new byte[] {
			(byte)((msb1>>56) & 0xff),
			(byte)((msb1>>48) & 0xff),
			(byte)((msb1>>40) & 0xff),
			(byte)((msb1>>32) & 0xff),
			(byte)((msb1>>24) & 0xff),
			(byte)((msb1>>16) & 0xff),
			(byte)((msb1>> 8) & 0xff),
			(byte)((msb1>> 0) & 0xff),
			(byte)((lsb1>>56) & 0xff),
			(byte)((lsb1>>48) & 0xff),
			(byte)((lsb1>>40) & 0xff),
			(byte)((lsb1>>32) & 0xff),
			(byte)((lsb1>>24) & 0xff),
			(byte)((lsb1>>16) & 0xff),
			(byte)((lsb1>> 8) & 0xff),
			(byte)((lsb1>> 0) & 0xff),

			(byte)((msb2>>56) & 0xff),
			(byte)((msb2>>48) & 0xff),
			(byte)((msb2>>40) & 0xff),
			(byte)((msb2>>32) & 0xff),
			(byte)((msb2>>24) & 0xff),
			(byte)((msb2>>16) & 0xff),
			(byte)((msb2>> 8) & 0xff),
			(byte)((msb2>> 0) & 0xff),
			(byte)((lsb2>>56) & 0xff),
			(byte)((lsb2>>48) & 0xff),
			(byte)((lsb2>>40) & 0xff),
			(byte)((lsb2>>32) & 0xff),
			(byte)((lsb2>>24) & 0xff),
			(byte)((lsb2>>16) & 0xff),
			(byte)((lsb2>> 8) & 0xff),
			(byte)((lsb2>> 0) & 0xff),
		};
	}

	public static byte[] fromInt(int v) {
		return new byte[] {
			(byte)((v >> 24) & 0xff),
			(byte)((v >> 16) & 0xff),
			(byte)((v >> 8) & 0xff),
			(byte)((v >> 0) & 0xff),
		};
	}

	public static void fromLong(long v, int offset, byte[] dest) {
		assert(dest.length>=(8+offset));
		dest[0+offset] = (byte)((v>>56) & 0xff);
		dest[1+offset] = (byte)((v>>48) & 0xff);
		dest[2+offset] = (byte)((v>>40) & 0xff);
		dest[3+offset] = (byte)((v>>32) & 0xff);
		dest[4+offset] = (byte)((v>>24) & 0xff);
		dest[5+offset] = (byte)((v>>16) & 0xff);
		dest[6+offset] = (byte)((v>> 8) & 0xff);
		dest[7+offset] = (byte)((v>> 0) & 0xff);
	}

	public static byte[] fromIntWithPrefix(byte prefix, int v) {
		return new byte[] {
			prefix,
			(byte)((v >> 24) & 0xff),
			(byte)((v >> 16) & 0xff),
			(byte)((v >> 8) & 0xff),
			(byte)((v >> 0) & 0xff),
		};
	}

	public static int toIntWithPrefix(byte prefix, byte[] bytes) {
		assert(bytes[0] == prefix);
		assert(bytes.length>=5);
		return (int)( 
					 (0xff & bytes[1]) << 24 |
					 (0xff & bytes[2]) << 16 |
					 (0xff & bytes[3]) << 8  |
					 (0xff & bytes[4]) << 0
					  );
	}
}
