package com.mirakl.hybris.core.i18n.services.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.i18n.services.CurrencyService;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultCurrencyService implements CurrencyService {

  protected CurrencyDao currencyDao;

  @Override
  public CurrencyModel getCurrencyForCode(String currencyCode) {
    List<CurrencyModel> currencies = currencyDao.findCurrenciesByCode(currencyCode);
    if (CollectionUtils.isNotEmpty(currencies) && currencies.size() > 1) {
      throw new AmbiguousIdentifierException(format("Found more than one currency having the code %s", currencyCode));
    }
    return isEmpty(currencies) ? null : currencies.get(0);
  }

  @Required
  public void setCurrencyDao(CurrencyDao currencyDao) {
    this.currencyDao = currencyDao;
  }
}
