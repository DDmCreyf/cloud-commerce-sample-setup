package com.mirakl.hybris.core.catalog.interceptors;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.CATEGORY_ATTRIBUTE;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCoreAttributeConfigurationValidateInterceptorTest {

  @Mock
  private MiraklCoreAttributeConfigurationModel coreAttributeConfiguration;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;
  @Mock
  private MiraklCategoryCoreAttributeModel categoryCoreAttribute;

  @InjectMocks
  private MiraklCoreAttributeConfigurationValidateInterceptor testObj;

  @Before
  public void setUp() throws Exception {
    when(coreAttributeConfiguration.getCoreAttributes()).thenReturn(Sets.newSet(coreAttribute, categoryCoreAttribute));
    when(categoryCoreAttribute.getRole()).thenReturn(CATEGORY_ATTRIBUTE);
  }

  @Test
  public void onValidate() throws Exception {
    testObj.onValidate(coreAttributeConfiguration, null);
  }

  @Test(expected = InterceptorException.class)
  public void onValidateWhenNoCategoryAttribute() throws Exception {
    when(coreAttributeConfiguration.getCoreAttributes()).thenReturn(Sets.newSet(coreAttribute));

    testObj.onValidate(coreAttributeConfiguration, null);
  }

}
