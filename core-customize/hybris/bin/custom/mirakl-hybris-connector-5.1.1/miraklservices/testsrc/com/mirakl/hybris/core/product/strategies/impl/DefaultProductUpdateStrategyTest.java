package com.mirakl.hybris.core.product.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeUpdateStrategy;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductUpdateStrategyTest {

  private static final String PRODUCT_ATTRIBUTE_1 = "color";
  private static final String PRODUCT_VALUE_1 = "red";
  private static final String PRODUCT_VALUE_2 = "XS";
  private static final String PRODUCT_VALUE_3 = "T-shirt";
  private static final String PRODUCT_ATTRIBUTE_2 = "size";
  private static final String PRODUCT_ATTRIBUTE_3 = "name";

  @InjectMocks
  private DefaultProductUpdateStrategy testObj;

  @Captor
  private ArgumentCaptor<Collection<AttributeValueData>> classificationAttributeCaptor;
  @Mock
  private ModelService modelService;
  @Mock
  private ClassificationAttributeUpdateStrategy classificationAttributeUpdateStrategy;
  @Mock
  private CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  @Mock
  private Converter<Pair<Map.Entry<String, String>, ProductImportFileContextData>, AttributeValueData> attributeValueDataConverter;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductImportFileContextData context;
  @Mock
  private MiraklRawProductModel rawProduct;
  @Mock
  private AttributeValueData coreAttributeValue1, coreAttributeValue2, classificationAttributeValue1;
  @Mock
  private MiraklCoreAttributeModel coreAttribute1, coreAttribute2;
  @Mock
  private CoreAttributeHandler<MiraklCoreAttributeModel> coreAttributeHandler1, coreAttributeHandler2;
  @Mock
  private ProductImportGlobalContextData globalContext;
  @Mock
  private ProductModel productToUpdate;
  @Mock
  private Populator<MiraklRawProductModel, ProductModel> mcmProductValuesPopulator;

  @Before
  public void setUp() throws Exception {
    Map<String, String> rawProductValues = new HashMap<>();
    rawProductValues.put(PRODUCT_ATTRIBUTE_1, PRODUCT_VALUE_1);
    rawProductValues.put(PRODUCT_ATTRIBUTE_2, PRODUCT_VALUE_2);
    rawProductValues.put(PRODUCT_ATTRIBUTE_3, PRODUCT_VALUE_3);

    when(data.getRawProduct()).thenReturn(rawProduct);
    when(rawProduct.getValues()).thenReturn(rawProductValues);

    when(attributeValueDataConverter.convert(Matchers.<Pair<Map.Entry<String, String>, ProductImportFileContextData>>any()))
        .thenReturn(coreAttributeValue1, coreAttributeValue2, classificationAttributeValue1);

    when(coreAttributeValue1.getCoreAttribute()).thenReturn(coreAttribute1);
    when(coreAttributeValue2.getCoreAttribute()).thenReturn(coreAttribute2);

    when(coreAttributeHandlerResolver.determineHandler(coreAttribute1, data, context)).thenReturn(coreAttributeHandler1);
    when(coreAttributeHandlerResolver.determineHandler(coreAttribute2, data, context)).thenReturn(coreAttributeHandler2);
  }

  @Test
  public void applyValues() throws Exception {
    testObj.applyValues(data, context);

    verify(coreAttributeHandler1).setValue(coreAttributeValue1, data, context);
    verify(coreAttributeHandler2).setValue(coreAttributeValue2, data, context);
    verify(classificationAttributeUpdateStrategy).updateAttributes(classificationAttributeCaptor.capture(), eq(data),
        eq(context));
    assertThat(classificationAttributeCaptor.getValue()).containsOnly(classificationAttributeValue1);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowIllegalStateExceptionWhenCoreAttributeHasNoHandler() throws Exception {
    when(coreAttributeHandlerResolver.determineHandler(coreAttribute2, data, context)).thenReturn(null);

    testObj.applyValues(data, context);
  }

}
