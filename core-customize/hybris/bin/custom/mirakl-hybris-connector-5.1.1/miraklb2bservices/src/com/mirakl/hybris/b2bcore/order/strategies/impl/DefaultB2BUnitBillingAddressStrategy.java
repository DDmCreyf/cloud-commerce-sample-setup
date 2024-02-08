package com.mirakl.hybris.b2bcore.order.strategies.impl;

import static com.google.common.collect.Iterables.isEmpty;

import java.util.Collection;

import com.mirakl.hybris.b2bcore.order.strategies.B2BUnitBillingAddressStrategy;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultB2BUnitBillingAddressStrategy implements B2BUnitBillingAddressStrategy {

  @Override
  public AddressModel getBillingAddressForUnit(B2BUnitModel unit) {

    if (unit.getBillingAddress() != null) {
      return unit.getBillingAddress();
    }

    Collection<AddressModel> addresses = unit.getAddresses();
    if (!isEmpty(addresses)) {
      return addresses.iterator().next();
    }

    throw new IllegalStateException(String.format("Impossible to find a billing address for B2B Unit [%s]", unit.getName()));
  }

}
