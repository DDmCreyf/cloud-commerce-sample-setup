package com.mirakl.hybris.core.promotions.strategies;

public interface MiraklPromotionsActivationStrategy {

  /**
   * Checks if the Mirakl promotions are enabled in Hybris.
   * 
   * @return true if the Mirakl promotions are enabled, false otherwise
   */
  boolean isMiraklPromotionsEnabled();

}
