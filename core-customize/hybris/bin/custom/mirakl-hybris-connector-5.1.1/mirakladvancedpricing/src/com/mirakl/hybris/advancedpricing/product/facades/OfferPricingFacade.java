package com.mirakl.hybris.advancedpricing.product.facades;

import java.util.List;

import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.beans.OfferPricingData;

public interface OfferPricingFacade {


  /**
   * Searches for {@link OfferPricingData}s corresponding to the given search criteria
   * 
   * @param offerPricingCriteria the search criteria
   * @return the {@link OfferPricingData} corresponding to the search criteria
   */
  List<OfferPricingData> getOfferPricingsForCriteria(OfferPricingCriteria offerPricingCriteria);

}
