package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.product.strategies.impl.DefaultMaintenanceCleanupRawProductsStrategy.ALL_RAW_PRODUCTS_QUERY;
import static com.mirakl.hybris.core.product.strategies.impl.DefaultMaintenanceCleanupRawProductsStrategy.RAW_PRODUCTS_TO_CLEAN_BEFORE_DATE_QUERY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCleanupRawProductsCronjobModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMaintenanceCleanupRawProductsStrategyTest {

  private static final Integer DAYS_BEFORE_DELETION = 4;

  @InjectMocks
  private DefaultMaintenanceCleanupRawProductsStrategy testObj;

  @Mock
  private ModelService modelService;
  @Mock
  private MiraklCleanupRawProductsCronjobModel cleanupJob;
  @Mock
  private List<MiraklRawProductModel> elementsToRemove;

  @Before
  public void setUp() throws Exception {}

  @Test
  public void shouldCreateFetchQueryForAllProducts() throws Exception {
    when(cleanupJob.getDaysBeforeDeletion()).thenReturn(null);

    FlexibleSearchQuery output = testObj.createFetchQuery(cleanupJob);

    assertThat(output.getQuery()).isEqualTo(ALL_RAW_PRODUCTS_QUERY);
  }

  @Test
  public void shouldCreateFetchQueryForAllProductsBeforeDate() throws Exception {
    when(cleanupJob.getDaysBeforeDeletion()).thenReturn(DAYS_BEFORE_DELETION);

    FlexibleSearchQuery output = testObj.createFetchQuery(cleanupJob);

    assertThat(output.getQuery()).isEqualTo(RAW_PRODUCTS_TO_CLEAN_BEFORE_DATE_QUERY);
  }

  @Test
  public void process() throws Exception {
    testObj.process(elementsToRemove);

    verify(modelService).removeAll(elementsToRemove);
  }

}
