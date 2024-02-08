package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;

import org.apache.log4j.Logger;

import com.mirakl.hybris.core.product.services.impl.DefaultOfferImportService;
import com.mirakl.hybris.core.product.strategies.OfferImportErrorHandler;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultOfferImportErrorHandler implements OfferImportErrorHandler {

  private static final Logger LOG = Logger.getLogger(DefaultOfferImportService.class);

  @Override
  public void handle(Exception e, String offerId) {
    LOG.error(format("An error occurred when importing offer [%s]", offerId), e);
  }
}
