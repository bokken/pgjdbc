/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.jdbc;

import org.postgresql.core.Oid;
import org.postgresql.jdbc.ArrayEncoding.PrimitiveIteratorArrayEncoder;

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfLong;

public class LongArraysTest extends AbstractPrimitiveArraysTest<long[], PrimitiveIterator.OfLong> {

  private static final long[][][] longs = new long[][][] { { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } },
      { { 13, 14, 15, 16 }, { 17, 18, 19, 20 }, { 21, 22, 23, 24 } } };

  public LongArraysTest() {
    super(longs, true, Oid.INT8_ARRAY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  PrimitiveIteratorArrayEncoder<?, OfLong> getArrayEncoder() {
    return ArrayEncoding.getPrimitiveLongArrayEncoder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  OfLong forData(long[] data) {
    return Arrays.stream(data).iterator();
  }
}
