package com.mirakl.hybris.core.order.strategies;

public interface SynchronousCartUpdateActivationStrategy {

  /**
   * Determines whether the synchronous cart update is enabled or not.
   *
   * @return true if the synchronous cart update is enabled, false otherwise
   */
  boolean isSynchronousCartUpdateEnabled();

}
