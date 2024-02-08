package com.mirakl.hybris.core.catalog.attributes.utils;

import static com.mirakl.hybris.core.catalog.attributes.utils.TypeParametersUtils.extractDatePatternFromTypeParameters;
import static com.mirakl.hybris.core.catalog.attributes.utils.TypeParametersUtils.extractListCodeFromTypeParameters;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.DATE;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.LIST;
import static com.mirakl.hybris.core.enums.MiraklAttributeType.LIST_MULTIPLE_VALUES;
import static java.lang.String.format;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TypeParametersUtilsTest {

  @Mock
  private MiraklCoreAttributeModel coreAttribute;


  @Test
  public void shouldExtractDatePattern() {
    String datePattern = "dd/MM/yyyy";
    String dateTypeParameters = "PATTERN|" + datePattern;
    when(coreAttribute.getType()).thenReturn(DATE);
    when(coreAttribute.getTypeParameters()).thenReturn(dateTypeParameters);

    String extractedPattern = extractDatePatternFromTypeParameters(coreAttribute);

    assertThat(extractedPattern).isEqualTo(datePattern);
  }

  @Test
  public void shouldExtractListCodeWithoutDefaultValue() {
    String listCode = "list-code";
    String listTypeParameters = format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode);
    when(coreAttribute.getType()).thenReturn(LIST);
    when(coreAttribute.getTypeParameters()).thenReturn(listTypeParameters);

    String extractedListCode = extractListCodeFromTypeParameters(coreAttribute);

    assertThat(extractedListCode).isEqualTo(listCode);
  }

  @Test
  public void shouldExtractListCodeWithDefaultValue() {
    String listCode = "list-code";
    String listTypeParameters = format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode) + ",DEFAULT_VALUE|value-1";
    when(coreAttribute.getType()).thenReturn(LIST);
    when(coreAttribute.getTypeParameters()).thenReturn(listTypeParameters);

    String extractedListCode = extractListCodeFromTypeParameters(coreAttribute);

    assertThat(extractedListCode).isEqualTo(listCode);
  }

  @Test
  public void shouldExtractListMultipleValuesCodeWithoutDefaultValue() {
    String listCode = "list-code";
    String listTypeParameters = format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode);
    when(coreAttribute.getType()).thenReturn(LIST_MULTIPLE_VALUES);
    when(coreAttribute.getTypeParameters()).thenReturn(listTypeParameters);

    String extractedListCode = extractListCodeFromTypeParameters(coreAttribute);

    assertThat(extractedListCode).isEqualTo(listCode);
  }

  @Test
  public void shouldExtractListMultipleValuesCodeWithDefaultValue() {
    String listCode = "list-code";
    String listTypeParameters = format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, listCode) + ",DEFAULT_VALUE|value-1";
    when(coreAttribute.getType()).thenReturn(LIST_MULTIPLE_VALUES);
    when(coreAttribute.getTypeParameters()).thenReturn(listTypeParameters);

    String extractedListCode = extractListCodeFromTypeParameters(coreAttribute);

    assertThat(extractedListCode).isEqualTo(listCode);
  }

  @Test
  public void shouldHandleEmptyTypeParametersWhenExtractingDatePattern() {
    when(coreAttribute.getType()).thenReturn(DATE);

    String extractedPattern = extractDatePatternFromTypeParameters(coreAttribute);

    assertThat(extractedPattern).isNull();
  }

  @Test
  public void shouldHandleEmptyTypeParametersWhenExtractingListCode() {
    when(coreAttribute.getType()).thenReturn(LIST);

    String extractedListCode = extractListCodeFromTypeParameters(coreAttribute);

    assertThat(extractedListCode).isNull();
  }
}
