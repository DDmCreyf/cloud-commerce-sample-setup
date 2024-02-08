package com.mirakl.hybris.core.i18n.services;

import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 *
 * Service providing ISO 3166-1 alpha-3 country codes.
 * 
 * @See Countries.impex
 */
public interface CountryService {

  /**
   * Returns the Hybris country matching the given iso alpha-3 code
   * 
   * @param isoAlpha3code
   * @return the <tt>CountryModel</tt> if any, null otherwise
   */
  CountryModel getCountryForIsoAlpha3Code(String isoAlpha3code);
}
