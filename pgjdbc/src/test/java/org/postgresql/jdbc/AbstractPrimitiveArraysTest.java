/*
 * Copyright (c) 2020, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.jdbc;

import static org.junit.Assert.assertEquals;

import org.postgresql.jdbc.ArrayEncoding.PrimitiveIteratorArrayEncoder;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.PrimitiveIterator;

/**
 * @author Brett Okken
 */
public abstract class AbstractPrimitiveArraysTest<A, I extends PrimitiveIterator<?, ?>> extends AbstractArraysTest<A> {

  /**
   * @param testData
   * @param binarySupported
   * @param arrayTypeOid
   */
  public AbstractPrimitiveArraysTest(A[][] testData, boolean binarySupported, int arrayTypeOid) {
    super(testData, binarySupported, arrayTypeOid);
  }

  @Test
  public void testPrimitiveIteratorBinary() throws Exception {

    A data = testData[0][0];

    PrimitiveIteratorArrayEncoder<?, I> support = getArrayEncoder();

    final int defaultArrayTypeOid = support.getDefaultArrayTypeOid();

    assertEquals(binarySupported, support.supportBinaryRepresentation(defaultArrayTypeOid));
    final PgArray pgArray = new PgArray(ENCODING_CONNECTION, defaultArrayTypeOid,
      support.toBinaryRepresentation(ENCODING_CONNECTION, Array.getLength(data), forData(data), defaultArrayTypeOid));

    Object actual = pgArray.getArray();

    assertArraysEquals("", data, actual);
  }

  @Test
  public void testPrimitiveIteratorString() throws Exception {

    A data = testData[0][0];

    PrimitiveIteratorArrayEncoder<?, I> support = getArrayEncoder();

    String arrayString = support.toArrayString(',', Array.getLength(data), forData(data));

    final PgArray pgArray = new PgArray(ENCODING_CONNECTION, arrayTypeOid, arrayString);

    Object actual = pgArray.getArray();

    assertArraysEquals("", data, actual);
  }

  abstract PrimitiveIteratorArrayEncoder<?, I> getArrayEncoder();

  abstract I forData(A data);
}
