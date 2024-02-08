package com.mirakl.hybris.core.i18n.services;

import de.hybris.platform.core.model.c2l.CurrencyModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 *
 * Service providing a quick access to currency codes within Hybris.
 */
public interface CurrencyService {

  /**
   * Returns currency matching the given code
   * 
   * @param currencyCode
   * @return <tt>CurrencyModel</tt> if any, or <tt>null</tt>
   */
  CurrencyModel getCurrencyForCode(String currencyCode);

}
