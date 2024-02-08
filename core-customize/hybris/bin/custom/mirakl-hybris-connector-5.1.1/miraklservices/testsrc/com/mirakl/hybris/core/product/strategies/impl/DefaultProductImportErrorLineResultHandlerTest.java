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
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductImportErrorLineResultHandlerTest extends AbstractProductImportLineResultHandlerTest {

  private static final String ERROR_LINE_HEADER = "error-line";
  private static final String ERROR_MESSAGE_HEADER = "error-message";
  private static final String ERROR_FILENAME_SUFFIX = "_error.csv";
  private static final String SHOP_FILENAME = "shop-filename";
  private static final int ERROR_ROW_NUMBER = 284;
  private static final String ERROR_MESSAGE = "An error occured !";
  private static final String EXISTING_HEADER = "attribute";
  private static final String EXISTING_VALUE = "value";

  @Mock
  private ProductImportFileContextData context;
  @Mock
  private Configuration configuration;
  @Mock
  private Map<String, HeaderInfoData> headerInfos;

  private ProductImportErrorData resultData = new ProductImportErrorData();

  private DefaultProductImportErrorLineResultHandler testObj;

  @Before
  public void setUp() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(PRODUCTS_IMPORT_ERROR_LINE_HEADER)).thenReturn(ERROR_LINE_HEADER);
    when(configuration.getString(PRODUCTS_IMPORT_ERROR_MESSAGE_HEADER)).thenReturn(ERROR_MESSAGE_HEADER);
    when(configuration.getString(PRODUCTS_IMPORT_ERROR_FILENAME_SUFFIX)).thenReturn(ERROR_FILENAME_SUFFIX);
    when(context.getHeaderInfos()).thenReturn(headerInfos);
    testObj = new DefaultProductImportErrorLineResultHandler(context);
    testObj.setConfigurationService(configurationService);
    testObj.setCsvService(csvService);
    super.setUp();
  }

  @Test
  public void getFileName() {
    when(context.getShopFilename()).thenReturn(SHOP_FILENAME);

    String result = testObj.getFilename();

    assertThat(result).isEqualTo(SHOP_FILENAME + ERROR_FILENAME_SUFFIX);
  }

  @Test
  public void buildLine() throws IOException {
    resultData.setLineValues(Collections.singletonMap(EXISTING_HEADER, EXISTING_VALUE));
    resultData.setRowNumber(ERROR_ROW_NUMBER);
    resultData.setErrorMessage(ERROR_MESSAGE);

    testObj.initialize();
    Map<String, String> result = testObj.buildLine(resultData);

    assertThat(result.keySet()).contains(EXISTING_HEADER, ERROR_LINE_HEADER, ERROR_MESSAGE_HEADER);
    assertThat(result.get(ERROR_LINE_HEADER)).isEqualTo(String.valueOf(ERROR_ROW_NUMBER));
    assertThat(result.get(ERROR_MESSAGE_HEADER)).isEqualTo(ERROR_MESSAGE);
  }

  @Test
  public void getHeader() throws IOException {
    when(headerInfos.keySet()).thenReturn(Sets.newHashSet(EXISTING_HEADER));

    testObj.initialize();
    String[] result = testObj.getHeader();

    assertThat(result).contains(EXISTING_HEADER, ERROR_LINE_HEADER, ERROR_MESSAGE_HEADER);
  }

}
