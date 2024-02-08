package com.mirakl.hybris.core.catalog.interceptors;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.type.TypeService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCatalogValidateInterceptorTest {


  @InjectMocks
  private MiraklCatalogValidateInterceptor testObj;

  @Mock
  private CatalogModel catalog;
  @Mock
  private InterceptorContext context;
  @Mock
  private ComposedTypeModel rootProductType, productType;
  @Mock
  private TypeService typeService;

  @Before
  public void setUp() throws Exception {
    when(catalog.getRootProductType()).thenReturn(rootProductType);
    when(typeService.getComposedTypeForCode(ProductModel._TYPECODE)).thenReturn(productType);
    when(typeService.isAssignableFrom(productType, rootProductType)).thenReturn(true);
  }

  @Test(expected = Test.None.class)
  public void onValidateWhenNoRootProductTypeDefined() throws Exception {
    when(catalog.getRootProductType()).thenReturn(null);

    testObj.onValidate(catalog, context);
  }

  @Test(expected = InterceptorException.class)
  public void onValidateWhenProductTypeNotAssignableFromRootProductType() throws Exception {
    when(typeService.isAssignableFrom(productType, rootProductType)).thenReturn(false);

    testObj.onValidate(catalog, context);
  }

  @Test(expected = Test.None.class)
  public void onValidateWhenProductTypeIsAssignableFromRootProductType() throws Exception {
    testObj.onValidate(catalog, context);
  }

}
