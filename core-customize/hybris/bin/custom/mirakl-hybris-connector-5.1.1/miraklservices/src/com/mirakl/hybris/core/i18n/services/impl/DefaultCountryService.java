package com.mirakl.hybris.core.i18n.services.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.i18n.services.CountryService;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultCountryService implements CountryService {

  protected GenericDao<CountryModel> countryDao;

  @Override
  public CountryModel getCountryForIsoAlpha3Code(String isoAlpha3code) {
    Map<String, Object> params = new HashMap<>();
    params.put(CountryModel.ISOALPHA3, isoAlpha3code);
    List<CountryModel> countryModels = countryDao.find(params);

    if (isNotEmpty(countryModels) && countryModels.size() > 1) {
      throw new AmbiguousIdentifierException(format("Found more than one country having the iso-code %s", isoAlpha3code));
    }

    return isEmpty(countryModels) ? null : countryModels.get(0);
  }

  @Required
  public void setCountryDao(GenericDao<CountryModel> countryDao) {
    this.countryDao = countryDao;
  }
}
