package com.mirakl.hybris.core.catalog.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.collections.Sets.newSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CoreAttributeConfigurationRootCategoryDynamicHandlerTest {

  private static final String ROOT_CATEGORY_CODE = "root-category-code";
  @Mock
  private MiraklCoreAttributeConfigurationModel coreAttributeConfiguration;
  @Mock
  private MiraklCoreAttributeModel coreAttribute1, coreAttribute2;
  @Mock
  private MiraklCategoryCoreAttributeModel categoryAttribute;

  @InjectMocks
  private CoreAttributeConfigurationRootCategoryDynamicHandler testObj;

  @Before
  public void setUp() throws Exception {
    when(coreAttributeConfiguration.getCoreAttributes()).thenReturn(newSet(coreAttribute1, coreAttribute2, categoryAttribute));
    when(categoryAttribute.getRole()).thenReturn(MiraklAttributeRole.CATEGORY_ATTRIBUTE);
    when(categoryAttribute.getRootCategoryCode()).thenReturn(ROOT_CATEGORY_CODE);
  }

  @Test
  public void get() throws Exception {
    String result = testObj.get(coreAttributeConfiguration);

    assertThat(result).isEqualTo(ROOT_CATEGORY_CODE);
  }

  @Test
  public void getWhenNoCategoryAttributeDefined() throws Exception {
    when(coreAttributeConfiguration.getCoreAttributes()).thenReturn(newSet(coreAttribute1, coreAttribute2));

    String result = testObj.get(coreAttributeConfiguration);

    assertThat(result).isNull();
  }

}
