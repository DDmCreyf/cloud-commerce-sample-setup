package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_FORMAT;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.util.services.impl.TranslationException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCoreAttributeValueTranslationStrategyTest {

  private static final String PRODUCT_ITEM_TYPE = "ApparelSizeVariantProduct";
  private static final String CORE_ATTRIBUTE_CODE = "size";
  private static final String ATOMIC_ATTRIBUTE_VALUE = "XXL";
  private static final Class ATOMIC_PERSISTENCE_CLASS = String.class;
  private static final Class UNSUPPORTED_PERSISTENCE_CLASS = List.class;
  private static final String ENUM_ATTRIBUTE_VALUE = "PREMIUM";
  private static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";
  private static final String ENUM_CODE = "PremiumState";
  private static final String STRING_VALUE = "test";
  private static final String DATE_VALUE = "09-12-1955";
  private static final String DATE_VALUE_WRONG_FORMAT = "31/12/1955";
  private static final Date EXPECTED_DATE = getExpectedDate();
  private static final String BOOLEAN_VALUE = "true";
  private static final Boolean EXPECTED_BOOLEAN = true;
  private static final String INTEGER_VALUE = "1984";
  private static final Integer EXPECTED_INTEGER = 1984;
  private static final String DOUBLE_VALUE = "192.168";
  private static final Double EXPECTED_DOUBLE = 192.168;
  private static final String BYTE_VALUE = "50";
  private static final Byte EXPECTED_BYTE = 50;
  private static final String FLOAT_VALUE = "14.89";
  private static final Float EXPECTED_FLOAT = 14.89f;
  private static final String LONG_VALUE = "98654712365";
  private static final Long EXPECTED_LONG = 98654712365L;
  private static final String SHORT_VALUE = "456";
  private static final Short EXPECTED_SHORT = 456;
  private static final String BIG_DECIMAL_VALUE = "94867543.15654987";
  private static final BigDecimal EXPECTED_BIG_DECIMAL = new BigDecimal(BIG_DECIMAL_VALUE);
  private static final String CHARACTER_VALUE = "H";
  private static final Character EXPECTED_CHARACTER = 'H';
  private static final String UNSUPPORTED_VALUE = "{key, value}";


  @InjectMocks
  private DefaultCoreAttributeValueTranslationStrategy testObj;

  @Mock
  private EnumerationService enumerationService;
  @Mock
  private TypeService typeService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private AttributeValueData attributeValue;
  @Mock
  private ProductModel product;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;
  @Mock
  private AttributeDescriptorModel attributeDescriptor;
  @Mock
  private Configuration configuration;
  @Mock
  private EnumerationMetaTypeModel enumerationType;

  @Before
  public void setUp() throws Exception {
    when(attributeValue.getCoreAttribute()).thenReturn(coreAttribute);
    when(attributeValue.getValue()).thenReturn(ATOMIC_ATTRIBUTE_VALUE);
    when(product.getItemtype()).thenReturn(PRODUCT_ITEM_TYPE);
    when(coreAttribute.getCode()).thenReturn(CORE_ATTRIBUTE_CODE);
    when(typeService.getAttributeDescriptor(PRODUCT_ITEM_TYPE, CORE_ATTRIBUTE_CODE)).thenReturn(attributeDescriptor);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(ATOMIC_PERSISTENCE_CLASS);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_DATE_FORMAT)).thenReturn(DEFAULT_DATE_FORMAT);
    when(enumerationType.getCode()).thenReturn(ENUM_CODE);
  }

  @Test
  public void translateEnumAttributeValue() throws Exception {
    when(attributeDescriptor.getAttributeType()).thenReturn(enumerationType);
    when(attributeValue.getValue()).thenReturn(ENUM_ATTRIBUTE_VALUE);

    testObj.translateAttributeValue(attributeValue, product);

    verify(enumerationService).getEnumerationValue(ENUM_CODE, ENUM_ATTRIBUTE_VALUE);
  }


  @Test(expected = TranslationException.class)
  public void translateUnsupportedAttributeValue() throws Exception {
    when(attributeDescriptor.getPersistenceClass()).thenReturn(UNSUPPORTED_PERSISTENCE_CLASS);

    testObj.translateAttributeValue(attributeValue, product);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void translateLocalizedAttributeValue() throws Exception {
    when(coreAttribute.isLocalized()).thenReturn(true);
    when(attributeValue.getLocale()).thenReturn(Locale.ENGLISH);
    when(attributeValue.getValue()).thenReturn(ATOMIC_ATTRIBUTE_VALUE);

    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Map.class);
    assertThat(((Map) output).get(Locale.ENGLISH)).isEqualTo(ATOMIC_ATTRIBUTE_VALUE);
  }

  @Test
  public void getValueForNull() throws Exception {
    when(attributeValue.getValue()).thenReturn(null);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isNull();
  }

  @Test
  public void getValueForEmptyString() throws Exception {
    when(attributeValue.getValue()).thenReturn("");
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isNull();
  }

  @Test
  public void getValueForString() throws Exception {
    when(attributeValue.getValue()).thenReturn(STRING_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(String.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(String.class);
    assertThat(output).isEqualTo(STRING_VALUE);
  }

  @Test
  public void getValueForDate() throws Exception {
    when(attributeValue.getValue()).thenReturn(DATE_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Date.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Date.class);
    assertThat(output).isEqualTo(EXPECTED_DATE);
  }

  @Test
  public void getValueForDateUsingTypeParametersPattern() throws Exception {
    when(attributeValue.getValue()).thenReturn("09/12/1955");
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Date.class);
    when(coreAttribute.getType()).thenReturn(MiraklAttributeType.DATE);
    when(coreAttribute.getTypeParameters()).thenReturn("PATTERN|dd/MM/yyyy");

    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Date.class);
    assertThat(output).isEqualTo(EXPECTED_DATE);
  }

  @Test(expected = TranslationException.class)
  public void getValueForDateWithIncorrectFormat() throws Exception {
    when(attributeValue.getValue()).thenReturn(DATE_VALUE_WRONG_FORMAT);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Date.class);
    testObj.translateAttributeValue(attributeValue, product);
  }

  @Test
  public void getValueForBoolean() throws Exception {
    when(attributeValue.getValue()).thenReturn(BOOLEAN_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Boolean.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Boolean.class);
    assertThat(output).isEqualTo(EXPECTED_BOOLEAN);
  }

  @Test
  public void getValueForInteger() throws Exception {
    when(attributeValue.getValue()).thenReturn(INTEGER_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Integer.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Integer.class);
    assertThat(output).isEqualTo(EXPECTED_INTEGER);
  }

  @Test
  public void getValueForDouble() throws Exception {
    when(attributeValue.getValue()).thenReturn(DOUBLE_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Double.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Double.class);
    assertThat(output).isEqualTo(EXPECTED_DOUBLE);
  }

  @Test
  public void getValueForByte() throws Exception {
    when(attributeValue.getValue()).thenReturn(BYTE_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Byte.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Byte.class);
    assertThat(output).isEqualTo(EXPECTED_BYTE);
  }

  @Test
  public void getValueForFloat() throws Exception {
    when(attributeValue.getValue()).thenReturn(FLOAT_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Float.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Float.class);
    assertThat(output).isEqualTo(EXPECTED_FLOAT);
  }

  @Test
  public void getValueForLong() throws Exception {
    when(attributeValue.getValue()).thenReturn(LONG_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Long.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Long.class);
    assertThat(output).isEqualTo(EXPECTED_LONG);
  }

  @Test
  public void getValueForShort() throws Exception {
    when(attributeValue.getValue()).thenReturn(SHORT_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Short.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Short.class);
    assertThat(output).isEqualTo(EXPECTED_SHORT);
  }

  @Test
  public void getValueForBigDecimal() throws Exception {
    when(attributeValue.getValue()).thenReturn(BIG_DECIMAL_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(BigDecimal.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(BigDecimal.class);
    assertThat(output).isEqualTo(EXPECTED_BIG_DECIMAL);
  }

  @Test
  public void getValueForCharacter() throws Exception {
    when(attributeValue.getValue()).thenReturn(CHARACTER_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(Character.class);
    Object output = testObj.translateAttributeValue(attributeValue, product);

    assertThat(output).isInstanceOf(Character.class);
    assertThat(output).isEqualTo(EXPECTED_CHARACTER);
  }

  @Test(expected = TranslationException.class)
  public void getValueForUnsupportedType() throws Exception {
    when(attributeValue.getValue()).thenReturn(UNSUPPORTED_VALUE);
    when(attributeDescriptor.getPersistenceClass()).thenReturn(List.class);
    testObj.translateAttributeValue(attributeValue, product);
  }

  private static Date getExpectedDate() {
    return new DateTime().withTimeAtStartOfDay().withDayOfMonth(9).withMonthOfYear(12).withYear(1955).toDate();
  }

}
