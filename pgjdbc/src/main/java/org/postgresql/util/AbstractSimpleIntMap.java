/*
 * Copyright (c) 2020, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.util;

import static org.postgresql.util.internal.Nullness.castNonNull;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Base/common implementation of a simplified map implementation with {@code int} keys.
 *
 * @author Brett Okken
 */
abstract class AbstractSimpleIntMap<E extends AbstractSimpleIntMap.BaseEntry<E>> {

  /**
   * This abstract entry has the key and chain to next entry. The actual implementation will have the value.
   * @param <E> The actual entry type, which will contain the value.
   */
  abstract static class BaseEntry<E extends AbstractSimpleIntMap.BaseEntry<E>> {
    final int key;
    @Nullable E next = null;

    protected BaseEntry(int key) {
      this.key = key;
    }
  }

  /**
   * Function to create type-safe array instances.
   */
  final IntFunction<E[]> arrayFunction;

  /**
   * Hash map entries.
   */
  @Nullable E[] nodes;

  /**
   * Denormalized count of entries.
   */
  int size = 0;

  /**
   * Constructs instance with expected number of entries and a function to create typed array.
   * @param expectedSize Expected size.
   * @param arrayFunction Function to create typed array of specified size.
   */
  AbstractSimpleIntMap(int expectedSize, IntFunction<E[]> arrayFunction) {
    nodes = arrayFunction.apply(Math.max(3, massageSize(expectedSize)));
    this.arrayFunction = arrayFunction;
  }

  private static int massageSize(int size) {
    size = (int) (size * .85);
    if ((size & 0x1) == 0) {
      --size;
    }
    return size;
  }

  /**
   * @return {@code true} if there are no entries.
   * @see java.util.Map#isEmpty()
   */
  public final boolean isEmpty() {
    return size == 0;
  }

  /**
   * @return The number of entries.
   * @see java.util.Map#size()
   */
  public final int size() {
    return size;
  }

  /**
   * Removes all entries.
   *
   * @see java.util.Collection#clear()
   * @see java.util.Map#clear()
   */
  public final void clear() {
    if (size > 0) {
      Arrays.fill(nodes, null);
      size = 0;
    }
  }

  /**
   * If no entry is present for <i>key</i>, <i>supplier</i> is used to create an entry instance
   * and it is added. If an entry exists, it is returned (un-modified).
   * @param key The key for entry to manage.
   * @param supplier Used to create entry if not currently present for <i>key</i>.
   * @return Existing entry for <i>key</i> (un-modified), or {@code} null if no entry present.
   */
  final @Nullable E manageEntry(int key, Supplier<E> supplier) {
    return manageEntry(key, supplier, true);
  }

  /**
   * @param key The key to find index for.
   * @return index into <i>nodes</i> which is appropriate for <i>key</i>.
   */
  private int index(int key) {
    //ignore sign bit to always get positive value
    return (key & 0x7FFFFFFF) % nodes.length;
  }

  private @Nullable E manageEntry(int key, Supplier<E> supplier, boolean resize) {
    int idx = index(key);
    @Nullable E node = nodes[idx];
    if (node == null) {
      nodes[idx] = supplier.get();
      ++size;
      return null;
    }
    if (node.key == key) {
      return node;
    }
    int count = 1;
    while (node.next != null) {
      node = node.next;
      if (node.key == key) {
        return node;
      }
      ++count;
    }
    if (resize && count > 4) {
      grow();
      return manageEntry(key, supplier, false);
    }
    node.next = supplier.get();
    ++size;
    return null;
  }

  /**
   * Only used during growth to add entries without managing size.
   * @param entry The entry to add.
   */
  private void addEntry(E entry) {
    int idx = index(entry.key);
    @Nullable E node = nodes[idx];
    if (node == null) {
      nodes[idx] = entry;
    } else {
      while (node.next != null) {
        node = node.next;
      }
      node.next = entry;
    }
  }

  /**
   * Removes the entry with key <i>key</i>.
   * @param key The key to remove.
   * @return The entry removed for <i>key</i> or {@code null} if no entry exists.
   */
  final @Nullable E removeEntry(int key) {
    int idx = index(key);
    @Nullable E node = nodes[idx];
    if (node == null) {
      return null;
    }
    if (node.key == key) {
      nodes[idx] = node.next;
      node.next = null;
      --size;
      return node;
    }
    while (node.next != null) {
      @Nullable E next = node.next;
      if (next.key == key) {
        node.next = next.next;
        next.next = null;
        --size;
        return next;
      }
      node = next;
    }
    return null;
  }

  /**
   * Returns existing Entry for <i>key</i>.
   * @param key The key to get Entry for.
   * @return existing Entry for <i>key</i> or {@code null} if no entry present.
   */
  final @Nullable E getEntry(int key) {
    @Nullable E entry = nodes[index(key)];
    while (entry != null) {
      if (key == entry.key) {
        return entry;
      }
      entry = entry.next;
    }
    return null;
  }

  /**
   * Returns {@code true} if an entry exists for <i>key</i>.
   * @param key Key to check if entry exists.
   * @return {@code true} if an entry exists for <i>key</i>
   * @see java.util.Map#containsKey(Object)
   */
  public final boolean containsKey(int key) {
    return getEntry(key) != null;
  }

  /**
   * Executes <i>action</i> for each entry.
   * @param action The action to call for each entry.
   * @see java.util.Map#forEach(java.util.function.BiConsumer)
   */
  final void forEachEntry(Consumer<? super E> action) {
    for (int i = 0; i < nodes.length; ++i) {
      @Nullable E entry = nodes[i];
      while (entry != null) {
        action.accept(entry);
        entry = entry.next;
      }
    }
  }

  /**
   * Executes the <i>action</i> for each key which has an entry.
   * @param action The action to call for each key which has an entry.
   */
  public final void forEachKey(IntConsumer action) {
    for (int i = 0; i < nodes.length; ++i) {
      @Nullable E entry = nodes[i];
      while (entry != null) {
        action.accept(entry.key);
        entry = entry.next;
      }
    }
  }

  private void grow() {
    @Nullable E[] orig = nodes;
    nodes = arrayFunction.apply(nextSize(size));
    for (int i = 0; i < orig.length; ++i) {
      E entry = orig[i];
      while (entry != null) {
        E next = entry.next;
        entry.next = null;
        addEntry(entry);
        entry = next;
      }
    }
  }

  /**
   * Provides iterator of all keys in instance.
   * <p>
   * Modification of this instance while iterating will result in indeterminate behavior.
   * </p>
   * @return iterator of all keys in instance.
   */
  public final PrimitiveIterator.OfInt keyIterator() {

    return new PrimitiveIterator.OfInt() {
      final @Nullable E[] nodes = AbstractSimpleIntMap.this.nodes;
      int idx = 0;
      @Nullable E next = null;

      @Override
      public boolean hasNext() {
        for ( ; next == null && idx < nodes.length; ++idx) {
          next = nodes[idx];
        }
        return next != null;
      }

      @Override
      public int nextInt() {
        if (next == null && !hasNext()) {
          throw new NoSuchElementException();
        }
        int val = castNonNull(next).key;
        next = next.next;
        return val;
      }
    };
  }

  private static int nextSize(int size) {
    if (size < 33000) {
      return massageSize(size << 1);
    }
    return massageSize((int) ((size + 1) * 1.5));
  }

  @Override
  public final String toString() {
    final StringBuilder sb = new StringBuilder(32 + (size * 16));
    sb.append(getClass().getSimpleName()).append(" [");
    boolean first = true;
    for (int i = 0; i < nodes.length; ++i) {
      @Nullable E entry = nodes[i];
      while (entry != null) {
        if (!first) {
          sb.append(',');
        }
        sb.append(entry.toString());
        entry = entry.next;
        first = false;
      }
    }
    sb.append(']');
    return sb.toString();
  }
}
