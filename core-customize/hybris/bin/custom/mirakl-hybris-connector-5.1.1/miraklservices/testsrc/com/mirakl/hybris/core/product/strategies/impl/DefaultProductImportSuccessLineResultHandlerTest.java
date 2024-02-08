package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.HeaderInfoData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportSuccessData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductImportSuccessLineResultHandlerTest extends AbstractProductImportLineResultHandlerTest {

  private static final String ADDITIONAL_MESSAGE_HEADER = "additional-message";
  private static final String SUCCESS_FILENAME_SUFFIX = "_success.csv";
  private static final String ORIGINAL_LINE_NUMBER = "original-line";
  private static final String SHOP_FILENAME = "shop-filename";
  private static final int ROW_NUMBER = 284;
  private static final String ADDITIONAL_MESSAGE = "Additional message";
  private static final String EXISTING_HEADER = "attribute";
  private static final String EXISTING_VALUE = "value";

  @Mock
  private ProductImportFileContextData context;
  @Mock
  private Configuration configuration;
  @Mock
  private Map<String, HeaderInfoData> headerInfos;

  private ProductImportSuccessData resultData = new ProductImportSuccessData();

  private DefaultProductImportSuccessLineResultHandler testObj;

  @Before
  public void setUp() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(PRODUCTS_IMPORT_ADDITIONAL_MESSAGE_HEADER)).thenReturn(ADDITIONAL_MESSAGE_HEADER);
    when(configuration.getString(PRODUCTS_IMPORT_ORIGINAL_LINE_NUMBER_HEADER)).thenReturn(ORIGINAL_LINE_NUMBER);
    when(configuration.getString(PRODUCTS_IMPORT_SUCCESS_FILENAME_SUFFIX)).thenReturn(SUCCESS_FILENAME_SUFFIX);
    when(context.getHeaderInfos()).thenReturn(headerInfos);
    testObj = new DefaultProductImportSuccessLineResultHandler(context);
    testObj.setConfigurationService(configurationService);
    testObj.setCsvService(csvService);
    super.setUp();
  }

  @Test
  public void getFileName() {
    when(context.getShopFilename()).thenReturn(SHOP_FILENAME);

    String result = testObj.getFilename();

    assertThat(result).isEqualTo(SHOP_FILENAME + SUCCESS_FILENAME_SUFFIX);
  }

  @Test
  public void buildLine() throws IOException {
    resultData.setLineValues(Collections.singletonMap(EXISTING_HEADER, EXISTING_VALUE));
    resultData.setRowNumber(ROW_NUMBER);
    resultData.setAdditionalMessage(ADDITIONAL_MESSAGE);

    testObj.initialize();
    Map<String, String> result = testObj.buildLine(resultData);

    assertThat(result.keySet()).contains(EXISTING_HEADER, ADDITIONAL_MESSAGE_HEADER, ORIGINAL_LINE_NUMBER);
    assertThat(result.get(ORIGINAL_LINE_NUMBER)).isEqualTo(String.valueOf(ROW_NUMBER));
    assertThat(result.get(ADDITIONAL_MESSAGE_HEADER)).isEqualTo(ADDITIONAL_MESSAGE);
  }

  @Test
  public void getHeader() throws IOException {
    when(headerInfos.keySet()).thenReturn(Sets.newHashSet(EXISTING_HEADER));

    testObj.initialize();
    String[] result = testObj.getHeader();

    assertThat(result).contains(EXISTING_HEADER, ADDITIONAL_MESSAGE_HEADER, ORIGINAL_LINE_NUMBER);
  }

}
