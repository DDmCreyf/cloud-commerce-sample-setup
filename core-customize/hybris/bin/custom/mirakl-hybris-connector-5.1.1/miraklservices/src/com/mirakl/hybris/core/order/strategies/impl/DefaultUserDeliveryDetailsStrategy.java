package com.mirakl.hybris.core.order.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.strategies.UserDeliveryDetailsStrategy;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultUserDeliveryDetailsStrategy implements UserDeliveryDetailsStrategy {

  protected BaseStoreService baseStoreService;

  @Override
  public AddressModel getDefaultDeliveryAddress() {
    return null;
  }

  @Override
  public CountryModel getDefaultDeliveryCountry() {
    AddressModel deliveryAddress = getDefaultDeliveryAddress();
    if (deliveryAddress != null && deliveryAddress.getCountry() != null) {
      return deliveryAddress.getCountry();
    } else if (baseStoreService.getCurrentBaseStore() != null) {
      return baseStoreService.getCurrentBaseStore().getDefaultDeliveryCountry();
    } else {
      return null;
    }
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }
}
