package com.mirakl.hybris.promotions.daos;

import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklPromotionDao extends GenericDao<MiraklPromotionModel> {

  /**
   * Finds the {@link MiraklPromotionModel} matching the given internal Id and shop Id.
   * @param shopId the shop id
   * @param internalId the promotion id given by the shop
   * 
   * @return the matching Mirakl Promotion or null otherwise
   */
  MiraklPromotionModel findMiraklPromotion(String shopId, String internalId);

}
