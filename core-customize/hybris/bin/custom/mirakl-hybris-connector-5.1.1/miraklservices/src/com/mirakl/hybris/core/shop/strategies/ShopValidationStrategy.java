package com.mirakl.hybris.core.shop.strategies;

import com.mirakl.hybris.core.model.ShopModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface ShopValidationStrategy {
  /**
   * Returns whether or not a shop should be imported from Mirakl
   * 
   * @param shop
   * @return true if the shop should be imported, false otherwise
   */
  boolean isValid(ShopModel shop);
}
