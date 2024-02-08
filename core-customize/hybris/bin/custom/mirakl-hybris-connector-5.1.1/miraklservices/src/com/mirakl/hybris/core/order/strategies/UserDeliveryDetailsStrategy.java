package com.mirakl.hybris.core.order.strategies;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;

public interface UserDeliveryDetailsStrategy {

  /**
   * Returns the default delivery address for the current user.
   *
   * @return the default delivery address if any, or null
   */
  AddressModel getDefaultDeliveryAddress();

  /**
   * Extracts the default delivery country for the current user whenever possible.
   * Falls back to {@link de.hybris.platform.store.BaseStoreModel#getDefaultDeliveryCountry()} if necessary
   *
   * @return the default delivery country if any, or null
   */
  CountryModel getDefaultDeliveryCountry();

}
