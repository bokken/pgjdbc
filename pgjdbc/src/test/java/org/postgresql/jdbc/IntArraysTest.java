/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.jdbc;

import org.postgresql.core.Oid;
import org.postgresql.jdbc.ArrayEncoding.PrimitiveIteratorArrayEncoder;

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfInt;

public class IntArraysTest extends AbstractPrimitiveArraysTest<int[], PrimitiveIterator.OfInt> {

  private static final int[][][] ints = new int[][][] { { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } },
      { { 13, 14, 15, 16 }, { 17, 18, 19, 20 }, { 21, 22, 23, 24 } } };

  public IntArraysTest() {
    super(ints, true, Oid.INT4_ARRAY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  PrimitiveIteratorArrayEncoder<?, OfInt> getArrayEncoder() {
    return ArrayEncoding.getPrimitiveIntArrayEncoder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  OfInt forData(int[] data) {
    return Arrays.stream(data).iterator();
  }
}
