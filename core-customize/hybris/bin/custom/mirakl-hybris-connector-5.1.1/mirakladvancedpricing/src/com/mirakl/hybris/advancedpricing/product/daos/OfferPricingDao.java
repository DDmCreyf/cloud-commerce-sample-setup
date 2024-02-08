package com.mirakl.hybris.advancedpricing.product.daos;

import java.util.List;

import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface OfferPricingDao extends GenericDao<OfferPricingModel> {

  /**
   * Searches for {@link OfferPricingModel}s corresponding to the given search criteria
   * 
   * @param offerPricingCriteria the search criteria
   * @return the {@link OfferPricingModel} corresponding to the search criteria
   */
  List<OfferPricingModel> findOfferPricingsByCriteria(OfferPricingCriteria offerPricingCriteria);

  /**
   * Returns a list of {@link OfferPricingModel}s belonging to one offer
   * 
   * @param offerId the Mirakl id of the offer
   * @return the {@link OfferPricingModel}s of the offer
   */
  List<OfferPricingModel> findByOfferId(String offerId);

}
