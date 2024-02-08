package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReplicatedAttributeHandlerTest extends AbstractCoreAttributeHandlerTest<MiraklCoreAttributeModel> {

  @Mock
  private VariantProductModel productToUpdate;
  @Mock
  private ProductModel baseProductToUpdate;
  @Mock
  private AttributeValueData attributeValue = new AttributeValueData();
  @Mock
  private DefaultCoreAttributeValueTranslationStrategy attributeValueTranslationStrategy;
  @Mock
  private Object translatedValue;

  @InjectMocks
  private DefaultReplicatedAttributeHandler testObj;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(importContext.getGlobalContext()).thenReturn(globalImportContext);
    when(attributeValue.getCoreAttribute()).thenReturn(coreAttribute);
    when(data.getProductToUpdate()).thenReturn(productToUpdate);
    when(productToUpdate.getBaseProduct()).thenReturn(baseProductToUpdate);
    when(productToUpdate.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
    when(baseProductToUpdate.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
    when(coreAttribute.getCode()).thenReturn(SIZE_ATTRIBUTE_QUALIFIER);
    when(attributeValueTranslationStrategy.translateAttributeValue(attributeValue, productToUpdate)).thenReturn(translatedValue);
    when(attributeValueTranslationStrategy.translateAttributeValue(attributeValue, baseProductToUpdate))
        .thenReturn(translatedValue);
    when(globalImportContext.getAttributesPerType()).thenReturn(attributesPerType);
  }

  @Test
  public void setValue() throws Exception {
    testObj.setValue(attributeValue, data, importContext);

    verify(modelService).setAttributeValue(productToUpdate, SIZE_ATTRIBUTE_QUALIFIER, translatedValue);
    verify(modelService).setAttributeValue(baseProductToUpdate, SIZE_ATTRIBUTE_QUALIFIER, translatedValue);
  } 

}
