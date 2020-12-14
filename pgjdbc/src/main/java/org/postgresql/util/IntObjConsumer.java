/*
 * Copyright (c) 2020, PostgreSQL Global Development Group
 * See the LICENSE file in the project root for more information.
 */

package org.postgresql.util;

/**
 * Represents an operation which takes two {@code int} operations and returns no result.
 *
 * @author Brett Okken
 * @param <U> the type of the second argument to the operation
 * @see java.util.function.BiConsumer
 */
public interface IntObjConsumer<U> {

  /**
   * Performs this operation on the given arguments.
   *
   * @param t the first input argument
   * @param u the second input argument
   */
  public void accept(int t, U u);
}
