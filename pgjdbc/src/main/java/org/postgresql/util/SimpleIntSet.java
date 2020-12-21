/*
 * Copyright (c) 2020, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Objects;

/**
 * A simple {@link java.util.Set}-like implementation for {@code int} values.
 *
 * @author Brett Okken
 */
public final class SimpleIntSet extends AbstractSimpleIntMap<SimpleIntSet.EntryImpl> {

  static final class EntryImpl extends AbstractSimpleIntMap.BaseEntry<EntryImpl> {

    protected EntryImpl(int key) {
      super(key);
    }

    @Override
    public String toString() {
      return Integer.toString(key);
    }
  }

  /**
   * Creates new instance containing all values from <i>ints</i>.
   * @param ints The values to populate new instance with.
   */
  public SimpleIntSet(int[] ints) {
    this(ints.length);
    for (int i : ints) {
      add(i);
    }
  }

  /**
   * Creates new instance containing all values from <i>ints</i>.
   * @param ints The values to populate new instance with.
   */
  public SimpleIntSet(Collection<Integer> ints) {
    this(ints.size());
    addAll(ints);
  }

  /**
   * Creates new instance containing all values from <i>ints</i>.
   * @param ints The values to populate new instance with.
   */
  public SimpleIntSet(SimpleIntSet ints) {
    this(ints.size());
    addAll(ints);
  }

  /**
   * Constructs an instance optimized for <i>expectedSize</i> entries.
   * @param expectedSize The number of entries to expect.
   */
  public SimpleIntSet(int expectedSize) {
    super(expectedSize, EntryImpl[]::new);
  }

  /**
   * Adds <i>val</i> to collection if not already present.
   * @param val The value to add.
   * @return {@code true} if element added or {@code false} if already present.
   * @see java.util.Set#add(Object)
   */
  public boolean add(int val) {
    final @Nullable EntryImpl existing = manageEntry(val, () ->  new EntryImpl(val));
    return existing == null;
  }

  /**
   * Adds all of values from <i>ints</i> which are not already present.
   * @param ints The values to add.
   * @return {@code true} if at least 1 element added or {@code false} if all already present.
   * @see java.util.Set#addAll(Collection)
   */
  public boolean addAll(SimpleIntSet ints) {
    int startingSize = size;
    ints.forEachKey(this::add);
    return startingSize != size;
  }

  /**
   * Adds all of values from <i>ints</i> which are not already present.
   * @param ints The values to add.
   * @return {@code true} if at least 1 element added or {@code false} if all already present.
   * @see java.util.Set#addAll(Collection)
   */
  public boolean addAll(Collection<Integer> ints) {
    int startingSize = size;
    ints.stream()
        .filter(Objects::nonNull)
        .mapToInt(Integer::intValue)
        .forEach(this::add);
    return startingSize != size;
  }

  /**
   * Removes <i>val</i> from the collection.
   * @param val The value to remove.
   * @return {@code true} if value was present or {@code false} if it was not.
   * @see java.util.Set#remove(Object)
   */
  public boolean remove(int val) {
    final @Nullable EntryImpl existing = removeEntry(val);
    return existing != null;
  }

  /**
   * Removes all the <i>ints</i> from the collection.
   * @param ints The values to remove.
   * @return {@code true} if at least 1 value was present or {@code false} if none were present.
   * @see java.util.Set#removeAll(Collection)
   */
  public boolean removeAll(SimpleIntSet ints) {
    int startingSize = size;
    ints.forEachKey(this::remove);
    return startingSize != size;
  }

  /**
   * Removes all the <i>ints</i> from the collection.
   * @param ints The values to remove.
   * @return {@code true} if at least 1 value was present or {@code false} if none were present.
   * @see java.util.Set#removeAll(Collection)
   */
  public boolean removeAll(Collection<Integer> ints) {
    int startingSize = size;
    ints.stream()
        .filter(Objects::nonNull)
        .mapToInt(Integer::intValue)
        .forEach(this::remove);
    return startingSize != size;
  }
}
