package com.mirakl.hybris.core.product.strategies.impl;

import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.mockito.Mock;

import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import shaded.org.supercsv.prefs.CsvPreference;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@Ignore
public abstract class AbstractProductImportLineResultHandlerTest {

  @Mock
  protected CsvService csvService;
  @Mock
  protected ConfigurationService configurationService;

  protected void setUp() {
    when(csvService.getDefaultCsvPreference()).thenReturn(CsvPreference.STANDARD_PREFERENCE);
  }

}
