package com.mirakl.hybris.core.i18n.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.i18n.services.impl.DefaultCountryService;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCountryServiceTest {

  @InjectMocks
  private DefaultCountryService defaultCountryService = new DefaultCountryService();

  @Mock
  private GenericDao<CountryModel> countryDao;

  @Test
  public void getCountryForIso3ShouldReturnAResult() {
    when(countryDao.find(anyMapOf(String.class, Object.class))).thenReturn(singletonList(new CountryModel()));

    assertNotNull(defaultCountryService.getCountryForIsoAlpha3Code("testId"));
  }

  @Test
  public void getCountryForIso3ShouldReturnNullWhenNoResult() {
    when(countryDao.find(anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<CountryModel>());

    assertNull(defaultCountryService.getCountryForIsoAlpha3Code("testId"));
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void getCountryForIso3ShouldThrowExceptionWhenMultipleResults() {
    when(countryDao.find(anyMapOf(String.class, Object.class))).thenReturn(asList(new CountryModel(), new CountryModel()));
    defaultCountryService.getCountryForIsoAlpha3Code("testId");
  }
}
