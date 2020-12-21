/*
 * Copyright (c) 2020, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.util;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A simplified {@code HashMap}-like collection where the key and value are both {@code int}s.
 *
 * <p>
 * This implementation is intended as a simplified and optimized replacement for {@code HashMap<Integer,Integer>}.
 * The scope of intended use in this project expects relatively few entries (no more than several hundreds). While
 * performance should be similar to {@link java.util.HashMap} even as sizes grow, large instances with millions
 * of entries are not being tested.
 * </p>
 * <p>
 * <b>NOTE:</b> This implementation is not synchronized. If multiple threads access an instance concurrently,
 * and any of the threads make a structural modification, it <b>MUST</b> be externally synchronized.
 * </p>
 * @author Brett Okken
 */
public final class SimpleIntIntMap extends AbstractSimpleIntMap<SimpleIntIntMap.EntryImpl> {

  static final class EntryImpl extends AbstractSimpleIntMap.BaseEntry<EntryImpl> {
    int value;

    protected EntryImpl(int key, int value) {
      super(key);
      this.value = value;
    }

    @Override
    public String toString() {
      return this.key + "=" + this.value;
    }
  }

  /**
   * Constructs an instance optimized for <i>expectedSize</i> entries.
   * @param expectedSize The number of entries to expect.
   */
  public SimpleIntIntMap(int expectedSize) {
    super(expectedSize, EntryImpl[]::new);
  }

  /**
   * Associates <i>value</i> with <i>key</i> in this map. If a value was previously associated with <i>key</i>,
   * the boxed representation will be returned.
   *
   * @param key key that <i>value</i> is to be associated to.
   * @param value value to associate with <i>key</i>.
   * @return The boxed {@link Integer} value of existing value or {@code null} if no value previously present.
   * @see java.util.Map#put(Object, Object)
   */
  public @Nullable Integer put(int key, int value) {
    final @Nullable EntryImpl existing = manageEntry(key, () ->  new EntryImpl(key, value));
    if (existing != null) {
      int existingValue = existing.value;
      existing.value = value;
      return existingValue;
    }
    return null;
  }

  /**
   * Associates <i>value</i> with <i>key</i> in this map iff no value is currently associated with <i>key</i>.
   *
   * @param key key that <i>value</i> is to be associated to.
   * @param value value to associate with <i>key</i> if no value currently exists.
   * @return The boxed {@link Integer} value previously associated with <i>key</i>, or {@code null} if there
   *         was no mapping for <i>key</i>.
   * @see java.util.Map#putIfAbsent(Object, Object)
   */
  public @Nullable Integer putIfAbsent(int key, int value) {
    final @Nullable EntryImpl existing = manageEntry(key, () ->  new EntryImpl(key, value));
    return existing == null ? null : existing.value;
  }

  /**
   * Returns the value mapped to <i>key</i> or <i>def</i>.
   *
   * @param key The key whose associated value is to be returned.
   * @param def The value to return if no value exists for <i>key</i>.
   * @return the value mapped to <i>key</i> or <i>def</i>.
   * @see java.util.Map#getOrDefault(Object, Object)
   */
  public int getOrDefault(int key, int def) {
    final @Nullable EntryImpl existing = getEntry(key);
    return existing != null ? existing.value : def;
  }

  /**
   * Removes value from map at <i>key</i>.
   *
   * @param key The key to remove from this instance.
   * @return the previous value for <i>key</i> or {@code null} if no entry existed.
   * @see java.util.Map#remove(Object)
   */
  public @Nullable Integer remove(int key) {
    final @Nullable EntryImpl existing = removeEntry(key);
    return existing == null ? null : existing.value;
  }

  /**
   * Performs <i>action</i> for each entry in the map.
   *
   * @param action The action to execute for each entry.
   * @see java.util.Map#forEach(java.util.function.BiConsumer)
   */
  public void forEach(IntBiConsumer action) {
    forEachEntry(e -> action.accept(e.key, e.value));
  }
}
