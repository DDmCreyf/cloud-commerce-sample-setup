package com.mirakl.hybris.core.product.strategies.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCleanupRawProductsStrategyTest {

  private static final String PRODUCT_IMPORT_ID = "0123456";

  @InjectMocks
  private DefaultCleanupRawProductsStrategy testObj;

  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private ModelService modelService;
  @Mock
  private SearchResult<MiraklRawProductModel> searchResult;
  @Mock
  private List<MiraklRawProductModel> rawProductsToRemove;

  @Before
  public void setUp() throws Exception {
    when(flexibleSearchService.<MiraklRawProductModel>search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
    when(searchResult.getResult()).thenReturn(rawProductsToRemove);
  }

  @Test
  public void cleanForImport() throws Exception {
    testObj.cleanForImport(PRODUCT_IMPORT_ID);

    verify(modelService).removeAll(rawProductsToRemove);
  }

}
