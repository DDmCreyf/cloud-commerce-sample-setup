package com.mirakl.hybris.core.catalog.populators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.HeaderInfoData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AttributeValueDataPopulatorTest {

  private static final String ATTRIBUTE_CODE = "description";
  private static final String ATTRIBUTE_VALUE = "A beautiful T-shirt with blue patterns. 100% cotton.";
  private static final PK CORE_ATTRIBUTE_PK = PK.fromLong(1L);

  @InjectMocks
  private AttributeValueDataPopulator testObj;

  @Mock
  private Pair<Map.Entry<String, String>, ProductImportFileContextData> source;
  @Mock
  private ModelService modelService;
  @Mock
  private Map.Entry<String, String> entry;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private HeaderInfoData headerInfo;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;

  @Before
  public void setUp() throws Exception {
    when(source.getLeft()).thenReturn(entry);
    when(source.getRight()).thenReturn(context);
    when(entry.getKey()).thenReturn(ATTRIBUTE_CODE);
    HashMap<String, HeaderInfoData> headerInfos = new HashMap<>();
    headerInfos.put(ATTRIBUTE_CODE, headerInfo);
    when(context.getHeaderInfos()).thenReturn(headerInfos);
    when(headerInfo.getAttribute()).thenReturn(ATTRIBUTE_CODE);
    when(headerInfo.getLocale()).thenReturn(Locale.ENGLISH);
    when(entry.getValue()).thenReturn(ATTRIBUTE_VALUE);
    when(context.getGlobalContext()).thenReturn(globalContext);
    Map<String, PK> coreAttributes = new HashMap<>();
    coreAttributes.put(ATTRIBUTE_CODE, CORE_ATTRIBUTE_PK);
    when(globalContext.getCoreAttributes()).thenReturn(coreAttributes);
    when(modelService.get(CORE_ATTRIBUTE_PK)).thenReturn(coreAttribute);
  }

  @Test
  public void populate() throws Exception {
    AttributeValueData target = new AttributeValueData();

    testObj.populate(source, target);

    assertThat(target.getCode()).isEqualTo(ATTRIBUTE_CODE);
    assertThat(target.getValue()).isEqualTo(ATTRIBUTE_VALUE);
    assertThat(target.getCoreAttribute()).isEqualTo(coreAttribute);
    assertThat(target.getLocale()).isEqualTo(Locale.ENGLISH);
  }

}
