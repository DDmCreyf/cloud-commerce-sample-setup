package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.util.services.impl.TranslationException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCoreAttributeHandlerTest extends AbstractCoreAttributeHandlerTest<MiraklCoreAttributeModel> {

  private static final String ENUMERATION_TYPE = "enumeration-type";
  private static final String UNKNOWN_ATTRIBUTE = "weight";
  private static final String VARIANT_PRODUCT_TYPE_CODE = "ApparelStyleVariantProduct";
  private static final String ROOT_PRODUCT_TYPE_CODE = "ApparelProduct";
  private static final String INVALID_ATTRIBUTE_TYPE_CODE = "InvalidTypeCode";

  @InjectMocks
  @Spy
  private DefaultCoreAttributeHandler testObj;

  @Mock
  private DefaultCoreAttributeValueTranslationStrategy attributeValueTranslationStrategy;
  @Mock
  private Object translatedAttribute;
  @Mock
  private MiraklExportCatalogContext exportContext;
  @Mock
  private MiraklExportCatalogConfig exportConfig;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private ValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private AtomicTypeModel atomicType;
  @Mock
  private AttributeDescriptorModel attributeDescriptor;
  @Mock
  private EnumerationMetaTypeModel enumerationType;
  @Mock
  private HybrisEnumValue enumerationValue1, enumerationValue2;
  @Mock
  private ComposedTypeModel composedTypeOwner;
  @Mock
  private EnumerationMetaTypeModel enumAttributeType;
  @Mock
  private TypeModel unsupportedType;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(importContext.getGlobalContext()).thenReturn(globalImportContext);
    when(attributeValue.getCoreAttribute()).thenReturn(coreAttribute);
    when(coreAttribute.getCode()).thenReturn(SIZE_ATTRIBUTE_QUALIFIER);
    when(attributeValueTranslationStrategy.translateAttributeValue(attributeValue, ownerProduct))
        .thenReturn(translatedAttribute);
    when(exportContext.getExportConfig()).thenReturn(exportConfig);
    when(exportConfig.getDefaultLocale()).thenReturn(Locale.ENGLISH);
    when(enumerationService.getEnumerationValues(anyString())).thenReturn(asList(enumerationValue1, enumerationValue2));
    when(typeService.getAttributeDescriptor(ROOT_PRODUCT_TYPE_CODE, SIZE_ATTRIBUTE_QUALIFIER)).thenReturn(attributeDescriptor);
    when(typeService.getAttributeDescriptor(VARIANT_PRODUCT_TYPE_CODE, STYLE_ATTRIBUTE_QUALIFIER))
        .thenReturn(attributeDescriptor);
    when(attributeDescriptor.getAttributeType()).thenReturn(enumAttributeType);
    when(composedTypeOwner.getCode()).thenReturn(VARIANT_PRODUCT_TYPE_CODE);
    when(exportConfig.getRootProductType()).thenReturn(ROOT_PRODUCT_TYPE_CODE);
    when(ownerProduct.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
    when(globalImportContext.getAttributesPerType()).thenReturn(attributesPerType);
    when(enumerationType.getCode()).thenReturn(ENUMERATION_TYPE);
  }

  @Test
  public void setUnkownAttributeValueForProduct() throws Exception {
    when(coreAttribute.getCode()).thenReturn(UNKNOWN_ATTRIBUTE);

    testObj.setValue(attributeValue, ownerProduct, data, importContext);

    verifyZeroInteractions(modelService);
  }

  @Test
  public void setAttributeValueForProduct() throws Exception {
    testObj.setValue(attributeValue, ownerProduct, data, importContext);

    verify(attributeValueTranslationStrategy).translateAttributeValue(attributeValue, ownerProduct);
    verify(modelService).setAttributeValue(ownerProduct, SIZE_ATTRIBUTE_QUALIFIER, translatedAttribute);
  }

  @Test(expected = ProductImportException.class)
  public void setAttributeValueForProductWhenTranslationFailed() throws Exception {
    when(attributeValueTranslationStrategy.translateAttributeValue(attributeValue, ownerProduct))
        .thenThrow(new TranslationException());

    testObj.setValue(attributeValue, ownerProduct, data, importContext);
  }

  @Test
  public void getValuesForNonListAttribute() throws Exception {
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.DATE);

    List<Map<String, String>> output = testObj.getValues(coreAttribute, exportContext);

    verifyZeroInteractions(typeService);
    verifyZeroInteractions(enumerationService);
    assertThat(output).isEmpty();
  }

  @Test
  public void getValuesForListButNonEnumAttribute() throws Exception {
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.LIST);
    when(attributeDescriptor.getAttributeType()).thenReturn(atomicType);

    List<Map<String, String>> output = testObj.getValues(coreAttribute, exportContext);

    verifyZeroInteractions(enumerationService);
    assertThat(output).isEmpty();
  }

  @Test
  public void getValuesForList() throws Exception {
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.LIST);
    when(attributeDescriptor.getAttributeType()).thenReturn(enumerationType);

    List<Map<String, String>> output = testObj.getValues(coreAttribute, exportContext);

    verify(enumerationService).getEnumerationValues(anyString());
    assertThat(output).hasSize(2);
  }

  @Test
  public void getValuesWhenAttributeIsNotPresentOnType() throws Exception {
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.LIST);
    when(coreAttribute.getCode()).thenReturn(STYLE_ATTRIBUTE_QUALIFIER);
    when(typeService.getAttributeDescriptor(anyString(), anyString())).thenThrow(new UnknownIdentifierException(""));

    testObj.getValues(coreAttribute, exportContext);

    verify(testObj).handleMissingAttributeDescriptor(coreAttribute, ROOT_PRODUCT_TYPE_CODE);
  }

  @Test
  public void getValuesWhenAttributeIsPresentOnComposedType() throws Exception {
    when(coreAttribute.getComposedTypeOwners()).thenReturn(asList(composedTypeOwner));
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.LIST);
    when(coreAttribute.getCode()).thenReturn(STYLE_ATTRIBUTE_QUALIFIER);

    testObj.getValues(coreAttribute, exportContext);
  }

  @Test
  public void getValuesWhenAttributeIsNotSupported() throws Exception {
    when(coreAttribute.getComposedTypeOwners()).thenReturn(asList(composedTypeOwner));
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.LIST);
    when(coreAttribute.getCode()).thenReturn(STYLE_ATTRIBUTE_QUALIFIER);
    when(attributeDescriptor.getAttributeType()).thenReturn(unsupportedType);
    when(unsupportedType.getCode()).thenReturn(INVALID_ATTRIBUTE_TYPE_CODE);

    testObj.getValues(coreAttribute, exportContext);

    verify(testObj).handleUnsupportedAttributeType(coreAttribute, INVALID_ATTRIBUTE_TYPE_CODE);
  }
}
