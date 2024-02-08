package com.mirakl.hybris.core.shop.strategies.impl;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.strategies.ShopValidationStrategy;

import org.apache.log4j.Logger;

import static java.lang.String.format;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultShopValidationStrategy implements ShopValidationStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultShopValidationStrategy.class);

  @Override
  public boolean isValid(ShopModel shop) {
    if (!shop.getCurrency().getActive()) {
      LOG.error(format("The currency [%s] of the shop [%s] with id [%s] is inactive. This shop will not be imported",
          shop.getCurrency().getName(), shop.getName(), shop.getId()));
      return false;
    }
    if (!shop.getContactInformation().getCountry().getActive()) {
      LOG.error(format("The country [%s] of the shop [%s] with id [%s] is inactive. This shop will not be imported",
          shop.getContactInformation().getCountry().getName(), shop.getName(), shop.getId()));
      return false;
    }
    return true;
  }
}
