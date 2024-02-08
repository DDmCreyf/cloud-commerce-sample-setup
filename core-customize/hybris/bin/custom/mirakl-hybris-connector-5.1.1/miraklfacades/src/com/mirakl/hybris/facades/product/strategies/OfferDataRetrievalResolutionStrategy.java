package com.mirakl.hybris.facades.product.strategies;

/**
 * Defines the resolution mechanism for determining which OfferDataRetrievalStrategy should be employed to fetch Offer Data.
 */
public interface OfferDataRetrievalResolutionStrategy {

  /**
   * Resolves and provides the appropriate strategy for retrieving offer data.
   *
   * @return the chosen OfferDataRetrievalStrategy for fetching offer data.
   */
  OfferDataRetrievalStrategy resolveOfferDataRetrievalStrategy();
}
