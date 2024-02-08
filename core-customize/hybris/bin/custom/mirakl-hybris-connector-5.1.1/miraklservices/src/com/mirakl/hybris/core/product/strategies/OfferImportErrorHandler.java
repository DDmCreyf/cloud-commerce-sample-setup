package com.mirakl.hybris.core.product.strategies;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface OfferImportErrorHandler {

  /**
   * Handles exceptions occurring during Mirakl offers import
   * 
   * @param e the thrown exception
   * @param offerId the offer that triggered the exception
   */
  void handle(Exception e, String offerId);
}
