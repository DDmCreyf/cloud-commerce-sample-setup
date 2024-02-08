package com.mirakl.hybris.core.product.strategies.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeUpdateStrategy;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMcmProductUpdateStrategyTest {

  private static final String PRODUCT_ATTRIBUTE = "size";
  private static final String PRODUCT_VALUE = "M";

  @InjectMocks
  private DefaultMcmProductUpdateStrategy mcmUpdateStrategy;

  @Mock
  private ModelService modelService;
  @Mock
  private ClassificationAttributeUpdateStrategy classificationAttributeUpdateStrategy;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private Converter<MiraklRawProductModel, ProductModel> mcmProductValuesConverter;
  @Mock
  private Converter<Pair<Map.Entry<String, String>, ProductImportFileContextData>, AttributeValueData> attributeValueDataConverter;
  @Mock
  private MiraklRawProductModel rawProduct;
  @Mock
  private ProductModel productToUpdate;
  @Mock
  private AttributeValueData coreAttributeValue;

  @Before
  public void setUp() throws Exception {
    Map<String, String> rawProductValues = new HashMap<>();
    rawProductValues.put(PRODUCT_ATTRIBUTE, PRODUCT_VALUE);

    when(data.getRawProduct()).thenReturn(rawProduct);
    when(data.getProductToUpdate()).thenReturn(productToUpdate);
    when(rawProduct.getValues()).thenReturn(rawProductValues);

    when(attributeValueDataConverter.convert(Matchers.<Pair<Map.Entry<String, String>, ProductImportFileContextData>>any()))
        .thenReturn(coreAttributeValue);
  }

  @Test
  public void shouldPopulateMcmValuesWhenNecessary() throws Exception {
    mcmUpdateStrategy.applyValues(data, context);

    verify(mcmProductValuesConverter).convert(rawProduct, productToUpdate);
  }

}
