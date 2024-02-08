package com.mirakl.hybris.core.promotions.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultMiraklPromotionActivationStrategy implements MiraklPromotionsActivationStrategy {

  protected BaseStoreService baseStoreService;

  @Override
  public boolean isMiraklPromotionsEnabled() {
    BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
    return currentBaseStore != null && currentBaseStore.isMiraklPromotionsEnabled();
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

}
