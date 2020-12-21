/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.jdbc;

import org.postgresql.core.Oid;
import org.postgresql.jdbc.ArrayEncoding.PrimitiveIteratorArrayEncoder;

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.PrimitiveIterator.OfDouble;

public class DoubleArraysTest extends AbstractPrimitiveArraysTest<double[], PrimitiveIterator.OfDouble> {

  private static final double[][][] doubles = new double[][][] {
      { { 1.2, 2.3, 3.7, 4.9 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } },
      { { 13, 14, 15, 16 }, { 17, 18, 19, 20 }, { 21, 22, 23, 24 } } };

  public DoubleArraysTest() {
    super(doubles, true, Oid.FLOAT8_ARRAY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  PrimitiveIteratorArrayEncoder<?, OfDouble> getArrayEncoder() {
    return ArrayEncoding.getPrimitiveDoubleArrayEncoder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  OfDouble forData(double[] data) {
    return Arrays.stream(data).iterator();
  }
}
