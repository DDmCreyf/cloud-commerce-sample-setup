package com.mirakl.hybris.advancedpricing.product.services;

import java.util.List;

import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

public interface OfferPricingService {

  /**
   * Searches for {@link OfferPricingModel}s corresponding to the given search criteria
   * 
   * @param offerPricingCriteria the search criteria
   * @return the {@link OfferPricingModel} corresponding to the search criteria
   */
  List<OfferPricingModel> getOfferPricingsForCriteria(OfferPricingCriteria offerPricingCriteria);


  /**
   * Searches for {@link OfferPricingModel}s corresponding to the given offer
   *
   * @param offerId the mirakl id of the offer
   * @return the {@link OfferPricingModel}s corresponding to the given offer
   */
  List<OfferPricingModel> findByOfferId(String offerId);

  /**
   * Deletes all the {@link OfferPricingModel}s belonging to the given offer.
   *
   * @param offerId the mirakl id of the offer
   */
  void deleteByOfferId(String offerId);

}
