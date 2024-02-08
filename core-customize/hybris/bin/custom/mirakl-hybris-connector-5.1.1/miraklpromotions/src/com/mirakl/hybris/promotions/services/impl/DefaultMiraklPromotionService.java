package com.mirakl.hybris.promotions.services.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.promotions.daos.MiraklPromotionDao;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

public class DefaultMiraklPromotionService implements MiraklPromotionService {

  protected MiraklPromotionDao miraklPromotionDao;

  @Override
  public MiraklPromotionModel getPromotion(String shopId, String internalId) {
    return miraklPromotionDao.findMiraklPromotion(shopId, internalId);
  }

  @Override
  public Collection<MiraklPromotionModel> getPromotionsForOffer(OfferModel offer, boolean activeOnly) {
    Set<MiraklPromotionModel> promotions = new HashSet<>();
    promotions.addAll(offer.getTriggerPromotions());
    promotions.addAll(offer.getRewardPromotions());
    if (activeOnly) {
      return getActivePromotions(promotions);
    }
    return promotions;
  }

  @Override
  public Collection<MiraklPromotionModel> getPromotionsForShop(String shopId, boolean activeOnly) {
    List<MiraklPromotionModel> promotions =
        miraklPromotionDao.find(Collections.singletonMap(MiraklPromotionModel.SHOPID, shopId));
    if (activeOnly) {
      return getActivePromotions(promotions);
    }
    return promotions;
  }

  protected Collection<MiraklPromotionModel> getActivePromotions(Collection<MiraklPromotionModel> promotions) {
    Collection<MiraklPromotionModel> activePromotions = new HashSet<>();
    for (MiraklPromotionModel promotion : promotions) {
      if (promotionIsActive(promotion)) {
        activePromotions.add(promotion);
      }
    }
    return activePromotions;
  }

  protected boolean promotionIsActive(MiraklPromotionModel promotion) {
    Date now = new Date();
    return (promotion.getStartDate() == null || promotion.getStartDate().before(now))
        && (promotion.getEndDate() == null || promotion.getEndDate().after(now));
  }

  @Required
  public void setMiraklPromotionDao(MiraklPromotionDao miraklPromotionDao) {
    this.miraklPromotionDao = miraklPromotionDao;
  }

}
