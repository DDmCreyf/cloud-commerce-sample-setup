package com.mirakl.hybris.core.catalog.daos.impl;

import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCoreAttributeConfigurationDaoTest {

  private static final String CORE_ATTRIBUTE_CONFIGURATION_CODE = "apparel-uk-coreAttributesConfiguration";

  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private MiraklCoreAttributeConfigurationModel coreAttributeConfiguration, excessCoreAttributeConfiguration;

  @InjectMocks
  private DefaultMiraklCoreAttributeConfigurationDao testObj;

  @Test
  public void getCoreAttributeConfigurationForCode() throws Exception {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(coreAttributeConfiguration), 1, 0, 0));

    MiraklCoreAttributeConfigurationModel result =
        testObj.getCoreAttributeConfigurationForCode(CORE_ATTRIBUTE_CONFIGURATION_CODE);

    assertThat(result).isEqualTo(coreAttributeConfiguration);
  }

  @Test
  public void getCoreAttributeConfigurationForCodeWhenNoneExists() throws Exception {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(new SearchResultImpl<>(emptyList(), 1, 0, 0));

    MiraklCoreAttributeConfigurationModel result =
        testObj.getCoreAttributeConfigurationForCode(CORE_ATTRIBUTE_CONFIGURATION_CODE);

    assertThat(result).isNull();
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void getCoreAttributeConfigurationForCodeWhenTooMuchExist() throws Exception {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(
        new SearchResultImpl<>(ImmutableList.<Object>of(coreAttributeConfiguration, excessCoreAttributeConfiguration), 1, 0, 0));

    testObj.getCoreAttributeConfigurationForCode(CORE_ATTRIBUTE_CONFIGURATION_CODE);
  }

}
