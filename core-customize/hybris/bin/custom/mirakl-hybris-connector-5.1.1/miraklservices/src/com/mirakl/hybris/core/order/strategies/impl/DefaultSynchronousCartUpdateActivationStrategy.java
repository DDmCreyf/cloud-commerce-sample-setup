package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.hybris.core.order.strategies.SynchronousCartUpdateActivationStrategy;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Required;

public class DefaultSynchronousCartUpdateActivationStrategy implements SynchronousCartUpdateActivationStrategy {

  protected BaseStoreService baseStoreService;

  @Override
  public boolean isSynchronousCartUpdateEnabled() {
    BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
    return currentBaseStore != null && currentBaseStore.isSynchronousCartUpdateEnabled();
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

}
