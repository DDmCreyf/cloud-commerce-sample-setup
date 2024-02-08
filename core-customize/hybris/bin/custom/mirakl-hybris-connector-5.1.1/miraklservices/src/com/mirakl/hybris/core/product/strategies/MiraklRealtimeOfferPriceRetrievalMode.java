package com.mirakl.hybris.core.product.strategies;

/**
 * Represents a strategy to determine if real-time offer price retrieval is active in the current context.
 */
public interface MiraklRealtimeOfferPriceRetrievalMode {

  /**
   * Determines if the real-time offer price retrieval mode is currently active.
   *
   * @return true if the real-time retrieval strategy for offer data is active, false otherwise.
   */
  boolean isActive();
}
