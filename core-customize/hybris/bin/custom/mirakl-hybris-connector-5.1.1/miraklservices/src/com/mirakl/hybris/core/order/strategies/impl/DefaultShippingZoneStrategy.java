package com.mirakl.hybris.core.order.strategies.impl;

import static com.google.common.base.Preconditions.checkState;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultShippingZoneStrategy implements ShippingZoneStrategy {
  protected BaseStoreService baseStoreService;

  @Override
  public String getShippingZoneCode(AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("order", order);

    AddressModel deliveryAddress = order.getDeliveryAddress();
    checkState(deliveryAddress != null, "Delivery address cannot be null for the shipping rates request");
    return deliveryAddress.getCountry().getIsocode();
  }

  @Override
  public String getEstimatedShippingZoneCode(CartModel cart) {
    validateParameterNotNullStandardMessage("cart", cart);

    AddressModel deliveryAddress = cart.getDeliveryAddress();
    if (deliveryAddress != null && deliveryAddress.getCountry() != null && deliveryAddress.getCountry().getIsocode() != null) {
      return deliveryAddress.getCountry().getIsocode();
    }
    BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
    CountryModel defaultDeliveryCountry = currentBaseStore.getDefaultDeliveryCountry();
    return defaultDeliveryCountry != null ? defaultDeliveryCountry.getIsocode() : null;
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }
}
