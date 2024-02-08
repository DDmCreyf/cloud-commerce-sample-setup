package com.mirakl.hybris.advancedpricing.product.populators;

import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder;
import com.mirakl.hybris.beans.OfferPricingCriteria;

public interface OfferPricingCriteriaBuilderPopulator {

  /**
   * Adds filters to the {@link OfferPricingCriteriaBuilder} in order to build the {@link OfferPricingCriteria}
   * 
   * @param builder the builder to enrich
   */
  void populate(OfferPricingCriteriaBuilder builder);

}
