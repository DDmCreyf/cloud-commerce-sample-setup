package com.mirakl.hybris.core.catalog.attributes;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.BOOLEAN;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.DATE;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.DECIMAL;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.INTEGER;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.LIST;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.LIST_MULTIPLE_VALUES;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.MEDIA;
import static java.lang.String.format;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TypeParametersForCoreAttributeDynamicHandlerTest {

  private static final String DEFAULT_DATE_TYPE_PARAMS = format(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN, "dd-MM-yyyy");
  private static final String DEFAULT_DECIMAL_TYPE_PARAMS = format(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN, "2");
  private static final String DEFAULT_MEDIA_TYPE_PARAMS = format(CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN, "10000");

  @InjectMocks
  private TypeParametersForCoreAttributeDynamicHandler handler;

  @Mock
  private ValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private MiraklCoreAttributeModel coreAttribute;

  @Test
  public void shouldSetTypeParameters() {
    String typeParameters = "value";
    handler.set(coreAttribute, typeParameters);

    verify(coreAttribute).setTypeParametersInternal(typeParameters);
  }

  @Test
  public void shouldGetTypeParametersFromInternalIfDefined() {
    String typeParametersInternal = "type-parameters";
    when(coreAttribute.getTypeParametersInternal()).thenReturn(typeParametersInternal);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(typeParametersInternal);
  }

  @Test
  public void shouldGetBooleanTypeParameters() {
    when(coreAttribute.getType()).thenReturn(BOOLEAN);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(BOOLEAN_TYPE_PARAMETERS);
  }

  @Test
  public void shouldGetDefaultDateTypeParameters() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE)).thenReturn(DEFAULT_DATE_TYPE_PARAMS);
    when(coreAttribute.getType()).thenReturn(DATE);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(DEFAULT_DATE_TYPE_PARAMS);
  }

  @Test
  public void shouldGetDateTypeParametersWithFallback() {
    String datePattern = "dd/MM/yyyy";
    when(coreAttribute.getTypeParameter()).thenReturn(datePattern);
    when(coreAttribute.getType()).thenReturn(DATE);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN, datePattern));
  }

  @Test
  public void shouldGetDefaultDecimalTypeParameters() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE)).thenReturn(DEFAULT_DECIMAL_TYPE_PARAMS);
    when(coreAttribute.getType()).thenReturn(DECIMAL);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(DEFAULT_DECIMAL_TYPE_PARAMS);
  }

  @Test
  public void shouldGetDecimalTypeParametersWithFallback() {
    String precision = "3";
    when(coreAttribute.getTypeParameter()).thenReturn(precision);
    when(coreAttribute.getType()).thenReturn(DECIMAL);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN, precision));
  }

  @Test
  public void shouldGetDefaultMediaTypeParameters() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_VALUE)).thenReturn(DEFAULT_MEDIA_TYPE_PARAMS);
    when(coreAttribute.getType()).thenReturn(MEDIA);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(DEFAULT_MEDIA_TYPE_PARAMS);
  }

  @Test
  public void shouldGetMediaTypeParametersWithFallback() {
    String size = "10000";
    when(coreAttribute.getTypeParameter()).thenReturn(size);
    when(coreAttribute.getType()).thenReturn(MEDIA);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN, size));
  }

  @Test
  public void shouldGetDefaultListTypeParameters() {
    String listCode = "list-code";
    when(valueListNamingStrategy.getCode(coreAttribute)).thenReturn(listCode);
    when(coreAttribute.getType()).thenReturn(LIST);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldGetListTypeParametersWithFallback() {
    String listCode = "list-code";
    when(coreAttribute.getTypeParameter()).thenReturn(listCode);
    when(coreAttribute.getType()).thenReturn(LIST);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldGetDefaultListMultipleValuesTypeParameters() {
    String listCode = "list-code";
    when(valueListNamingStrategy.getCode(coreAttribute)).thenReturn(listCode);
    when(coreAttribute.getType()).thenReturn(LIST_MULTIPLE_VALUES);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldGetListMultipleValuesTypeParametersWithFallback() {
    String listCode = "list-code";
    when(coreAttribute.getTypeParameter()).thenReturn(listCode);
    when(coreAttribute.getType()).thenReturn(LIST_MULTIPLE_VALUES);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldGetDefaultBooleanTypeParameters() {
    String listCode = "list-code";
    when(valueListNamingStrategy.getCode(coreAttribute)).thenReturn(listCode);
    when(coreAttribute.getType()).thenReturn(LIST);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldIgnoreAttributesWithNoSupportedTypeParameters() {
    String typeParameter = "dummy";
    when(coreAttribute.getTypeParameter()).thenReturn(typeParameter);
    when(coreAttribute.getType()).thenReturn(INTEGER);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isNull();
  }

  @Test
  public void shouldIgnoreAttributesWithNoSupportedDefaultTypeParameters() {
    when(coreAttribute.getType()).thenReturn(INTEGER);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isNull();
  }

  @Test
  public void shouldIgnoreAttributesWithNoType() {
    String typeParameter = "dummy";
    when(coreAttribute.getTypeParameter()).thenReturn(typeParameter);

    String typeParameters = handler.get(coreAttribute);

    assertThat(typeParameters).isNull();
  }

}
