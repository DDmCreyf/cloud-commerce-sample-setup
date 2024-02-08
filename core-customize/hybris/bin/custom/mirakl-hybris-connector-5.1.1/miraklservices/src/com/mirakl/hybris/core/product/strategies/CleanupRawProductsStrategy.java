package com.mirakl.hybris.core.product.strategies;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface CleanupRawProductsStrategy {

  /**
   * Cleans all the raw products generated during a specific product import
   *
   * @param importId The ID of the product import
   */
  void cleanForImport(String importId);

}
