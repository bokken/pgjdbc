/*
 * Copyright (c) 2018, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.postgresql.core.Parser.IntCollection;

import org.junit.Test;

/**
 * Tests {@code IntCollection}.
 *
 * @author Brett Okken
 */
public class IntCollectionTest {

  @Test
  public void testSize() {
    final IntCollection list = new IntCollection();
    assertEquals(0, list.size());
    list.add(3);
    assertEquals(1, list.size());

    for (int i = 0; i < 48; ++i) {
      list.add(i);
    }
    assertEquals(49, list.size());

    list.clear();
    assertEquals(0, list.size());
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testGet_empty() {
    final IntCollection list = new IntCollection();
    list.get(0);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testGet_negative() {
    final IntCollection list = new IntCollection();
    list.add(3);
    list.get(-1);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testGet_tooLarge() {
    final IntCollection list = new IntCollection();
    list.add(3);
    list.get(1);
  }

  @Test
  public void testGet() {
    final IntCollection list = new IntCollection();
    list.add(3);
    assertEquals(3, list.get(0));

    for (int i = 0; i < 1048; ++i) {
      list.add(i);
    }

    assertEquals(3, list.get(0));

    for (int i = 0; i < 1048; ++i) {
      assertEquals(i, list.get(i + 1));
    }

    list.clear();
    list.add(4);
    assertEquals(4, list.get(0));
  }

  @Test
  public void testToArray() {
    final IntCollection list = new IntCollection();
    assertSame(Parser.EMPTY_INT_ARRAY, list.toArray());

    list.add(45);
    assertArrayEquals(new int[] { 45 }, list.toArray());

    list.clear();
    assertSame(Parser.EMPTY_INT_ARRAY, list.toArray());

    final int[] expected = new int[1048];
    for (int i = 0; i < 1048; ++i) {
      list.add(i);
      expected[i] = i;
    }
    assertArrayEquals(expected, list.toArray());
  }
}
