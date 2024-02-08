package com.mirakl.hybris.promotions.services;

import java.util.Collection;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

public interface MiraklPromotionService {

  /**
   * Returns the promotion matching a given internal identifier and shop.
   *
   * @param shopId the id of the shop owning the promotion
   * @param internalId the promotion identifier given by its owning shop
   * 
   * @return The associated {@link MiraklPromotionModel} if found, null otherwise
   */
  MiraklPromotionModel getPromotion(String shopId, String internalId);

  /**
   * Returns all the promotions for the given offer.
   *
   * @param offer the offer
   * @param activeOnly set to true to include only promotions that are currently active
   * @return a list of promotions
   */
  Collection<MiraklPromotionModel> getPromotionsForOffer(OfferModel offer, boolean activeOnly);

  /**
   * Return all the promotions for the given shop id.
   *
   * @param shopId the shop id
   * @param activeOnly set to true to include only promotions that are currently active
   * @return a list of promotions
   */
  Collection<MiraklPromotionModel> getPromotionsForShop(String shopId, boolean activeOnly);

}
