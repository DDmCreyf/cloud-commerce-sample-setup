package com.mirakl.hybris.core.catalog.attributes;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.BOOLEAN;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.DATE;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.ENUM;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.NUMBER;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.STRING;
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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TypeParametersForClassAttributeAssignmentDynamicHandlerTest {

  private static final String DEFAULT_DATE_TYPE_PARAMS = format(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN, "dd-MM-yyyy");
  private static final String DEFAULT_DECIMAL_TYPE_PARAMS = format(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN, "2");

  @InjectMocks
  private TypeParametersForClassAttributeAssignmentDynamicHandler handler;
  
  @Mock
  private ValueListNamingStrategy valueListNamingStrategy;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;

  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment;
  
  @Test
  public void shouldSetTypeParameters() {
    String typeParameters = "value";
    handler.set(classAttributeAssignment, typeParameters);

    verify(classAttributeAssignment).setMarketplaceTypeParametersInternal(typeParameters);
  }

  @Test
  public void shouldReturnInternalTypeParametersWhenDefined() {
    String typeParametersInternal = "type-parameters";
    when(classAttributeAssignment.getMarketplaceTypeParametersInternal()).thenReturn(typeParametersInternal);
    
    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isEqualTo(typeParametersInternal);
  }

  @Test
  public void shouldGetBooleanTypeParameters() {
    when(classAttributeAssignment.getAttributeType()).thenReturn(BOOLEAN);

    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isEqualTo(BOOLEAN_TYPE_PARAMETERS);
  }

  @Test
  public void shouldGetDefaultDateTypeParameters() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE)).thenReturn(DEFAULT_DATE_TYPE_PARAMS);
    when(classAttributeAssignment.getAttributeType()).thenReturn(DATE);

    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isEqualTo(DEFAULT_DATE_TYPE_PARAMS);
  }

  @Test
  public void shouldGetDefaultDecimalTypeParameters() {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE)).thenReturn(DEFAULT_DECIMAL_TYPE_PARAMS);
    when(classAttributeAssignment.getAttributeType()).thenReturn(NUMBER);

    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isEqualTo(DEFAULT_DECIMAL_TYPE_PARAMS);
  }

  @Test
  public void shouldGetDefaultListTypeParameters() {
    String listCode = "list-code";
    when(valueListNamingStrategy.getCode(classAttributeAssignment)).thenReturn(listCode);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ENUM);

    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isEqualTo(format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode));
  }

  @Test
  public void shouldIgnoreAttributesWithNoSupportedDefaultTypeParameters() {
    when(classAttributeAssignment.getAttributeType()).thenReturn(STRING);

    String typeParameters = handler.get(classAttributeAssignment);

    assertThat(typeParameters).isNull();
  }

}
