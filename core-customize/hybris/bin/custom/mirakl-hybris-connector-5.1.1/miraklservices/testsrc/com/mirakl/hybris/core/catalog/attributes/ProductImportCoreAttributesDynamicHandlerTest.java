package com.mirakl.hybris.core.catalog.attributes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductImportCoreAttributesDynamicHandlerTest
    extends AbstractCatalogJobCoreAttributesDynamicHandlerTest<MiraklProductImportCronJobModel> {

  @Mock
  private MiraklProductImportCronJobModel productImportJob;

  @InjectMocks
  private ProductImportCoreAttributesDynamicHandler testObj;

  @Before
  public void setUp() throws Exception {
    super.setUp(testObj);
    when(productImportJob.getCoreAttributeConfiguration()).thenReturn(configuration);
  }

  @Test
  public void get() {
    testObj.get(productImportJob);

    verify(productImportJob).getCoreAttributeConfiguration();
  }

  @Test
  public void set() {
    testObj.set(productImportJob, Sets.newSet(coreAttribute1, coreAttribute2));

    verify(productImportJob).getCoreAttributeConfiguration();
  }
}
