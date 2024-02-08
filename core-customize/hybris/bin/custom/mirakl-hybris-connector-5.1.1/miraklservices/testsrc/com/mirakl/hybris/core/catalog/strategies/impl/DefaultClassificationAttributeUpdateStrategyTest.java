package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_FORMAT;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_VALUES_SEPARATOR;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultClassificationAttributeUpdateStrategyTest {

  private static final String INCORRECTLY_FORMATTED_DATE = "12/12/2099";
  private static final String VALUES_SEPARATOR = "|";
  private static final String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";
  private static final String FEATURE_VALUE = "feature-value";
  private static final String STRING_VALUE = "string-value";
  private static final Boolean BOOLEAN_VALUE = Boolean.TRUE;
  private static final Double NUMBER_VALUE = 12d;
  private static final Date DATE_VALUE = DateTime.now().withTimeAtStartOfDay().toDate();
  private static final String ENUM_VALUE = "enumValue";
  private static final String MULTIVALUE2 = "multivalue2";
  private static final String MULTIVALUE1 = "multivalue1";
  private static final String LOCALIZED_VALUE_DE = "value-de";
  private static final String LOCALIZED_VALUE_EN = "value-en";
  private static final String CLASS_CODE = "class-code";
  private static final String ATTRIBUTE_CODE = "attribute-code";
  private static final String UNKOWN_ATTRIBUTE_CODE = "unkown-attribute-code";

  @InjectMocks
  private DefaultClassificationAttributeUpdateStrategy updateStrategy;

  @Mock
  protected ClassificationService classificationService;
  @Mock
  protected ConfigurationService configurationService;
  @Mock
  protected ModelService modelService;
  @Mock
  private ProductImportData data;
  @Mock
  private ProductModel productToUpdate, identifiedProduct;
  @Mock
  private ClassAttributeAssignmentModel classAttributeAssignment;
  @Mock
  private ClassificationAttributeModel classificationAttribute;
  @Mock
  private ClassificationClassModel classificationClass;
  @Mock
  private ClassificationAttributeValueModel classificationAttributeValue;
  @Mock
  private AttributeValueData attributeValueData, attributeValueDataEn, attributeValueDataDe;
  @Mock
  private Configuration configuration;

  private Collection<AttributeValueData> attributeValues;
  private ProductImportFileContextData context;
  private FeatureList featureList;

  @Captor
  private ArgumentCaptor<FeatureList> featureListCaptor;


  @Before
  public void setUp() throws Exception {
    attributeValues = asList(attributeValueData);
    when(attributeValueData.getCode()).thenReturn(ATTRIBUTE_CODE);
    when(attributeValueData.getValue()).thenReturn(STRING_VALUE);

    when(attributeValueDataEn.getCode()).thenReturn(ATTRIBUTE_CODE);
    when(attributeValueDataEn.getValue()).thenReturn(LOCALIZED_VALUE_EN);
    when(attributeValueDataEn.getLocale()).thenReturn(Locale.ENGLISH);

    when(attributeValueDataDe.getCode()).thenReturn(ATTRIBUTE_CODE);
    when(attributeValueDataDe.getValue()).thenReturn(LOCALIZED_VALUE_DE);
    when(attributeValueDataDe.getLocale()).thenReturn(Locale.GERMAN);

    when(classAttributeAssignment.getClassificationAttribute()).thenReturn(classificationAttribute);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.STRING);
    when(classificationAttribute.getCode()).thenReturn(ATTRIBUTE_CODE);

    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(PRODUCTS_IMPORT_VALUES_SEPARATOR)).thenReturn(VALUES_SEPARATOR);
    when(configuration.getString(CATALOG_EXPORT_DATE_FORMAT, DEFAULT_DATE_FORMAT)).thenReturn(DEFAULT_DATE_FORMAT);

    when(data.getProductToUpdate()).thenReturn(productToUpdate);
  }

  @Test
  public void shouldSetFeaturesForNewProducts() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(data.getIdentifiedProduct()).thenReturn(null);
    when(attributeValueData.getValue()).thenReturn(STRING_VALUE);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(STRING_VALUE);
  }

  @Test
  public void shouldReplaceFeaturesForUpdates() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(data.getIdentifiedProduct()).thenReturn(identifiedProduct);
    when(attributeValueData.getValue()).thenReturn(STRING_VALUE);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).replaceFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(STRING_VALUE);
  }

  @Test
  public void shouldIgnoreUnkownAttributes() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(attributeValueData.getCode()).thenReturn(UNKOWN_ATTRIBUTE_CODE);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService, never()).setFeatures(any(ProductModel.class), any(FeatureList.class));
    verify(classificationService, never()).replaceFeatures(any(ProductModel.class), any(FeatureList.class));
  }

  @Test
  public void shouldHandleNullValues() throws Exception {
    FeatureValue featureValue = new FeatureValue(FEATURE_VALUE);
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, asList(featureValue)));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(attributeValueData.getValue()).thenReturn(null);
    when(data.getIdentifiedProduct()).thenReturn(identifiedProduct);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).replaceFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).isEmpty();
  }

  @Test
  public void shouldDoNothingWhenNoAttributes() throws Exception {
    attributeValues = new ArrayList<>();

    updateStrategy.updateAttributes(attributeValues, data, context);

    verifyZeroInteractions(classificationService);
  }

  @Test
  public void shouldHandleMultiValuedAttributes() throws Exception {
    FeatureValue featureValue = new FeatureValue(FEATURE_VALUE);
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, asList(featureValue)));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getMultiValued()).thenReturn(true);
    when(attributeValueData.getValue()).thenReturn(MULTIVALUE1 + VALUES_SEPARATOR + MULTIVALUE2);
    when(data.getIdentifiedProduct()).thenReturn(identifiedProduct);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).replaceFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(2);
    assertThat(feature.getValues()).onProperty("value").containsOnly(MULTIVALUE1, MULTIVALUE2);

  }

  @Test
  public void shouldHandleBooleanAttributes() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.BOOLEAN);
    when(attributeValueData.getValue()).thenReturn(BOOLEAN_VALUE.toString());

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(BOOLEAN_VALUE);
  }

  @Test
  public void shouldHandleNumberAttributes() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.NUMBER);
    when(attributeValueData.getValue()).thenReturn(NUMBER_VALUE.toString());

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(NUMBER_VALUE);

  }

  @Test
  public void shouldHandleDateAttributes() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.DATE);
    when(attributeValueData.getValue()).thenReturn(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(DATE_VALUE));

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(DATE_VALUE);
  }

  @Test(expected = ProductImportException.class)
  public void shouldShouldThrowExceptionWhenIncorrectDateFormat() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.DATE);
    when(attributeValueData.getValue()).thenReturn(INCORRECTLY_FORMATTED_DATE);

    updateStrategy.updateAttributes(attributeValues, data, context);
  }

  @Test
  public void shouldHandleEnumAttributes() throws Exception {
    featureList = new FeatureList(new UnlocalizedFeature(classAttributeAssignment, new ArrayList<>()));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    when(classAttributeAssignment.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
    when(attributeValueData.getValue()).thenReturn(ENUM_VALUE);
    when(classAttributeAssignment.getAttributeValues()).thenReturn(asList(classificationAttributeValue));
    when(classificationAttributeValue.getCode()).thenReturn(ENUM_VALUE);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature.getValues()).hasSize(1);
    assertThat(feature.getValue().getValue()).isEqualTo(classificationAttributeValue);
  }

  @Test
  public void shouldHandleLocalizedFeatures() throws Exception {
    featureList = new FeatureList(new LocalizedFeature(classAttributeAssignment, new HashMap<>(), Locale.FRENCH));
    when(classificationService.getFeatures(productToUpdate)).thenReturn(featureList);
    attributeValues = asList(attributeValueDataEn, attributeValueDataDe);

    updateStrategy.updateAttributes(attributeValues, data, context);

    verify(classificationService).setFeatures(eq(productToUpdate), featureListCaptor.capture());
    FeatureList modifiedFeatureList = featureListCaptor.getValue();
    Feature feature = modifiedFeatureList.getFeatureByAssignment(classAttributeAssignment);
    assertThat(feature).isInstanceOf(LocalizedFeature.class);
    LocalizedFeature localizedFeature = (LocalizedFeature) feature;
    assertThat(localizedFeature.getValues(Locale.ENGLISH)).hasSize(1);
    assertThat(localizedFeature.getValue(Locale.ENGLISH).getValue()).isEqualTo(LOCALIZED_VALUE_EN);
    assertThat(localizedFeature.getValues(Locale.GERMAN)).hasSize(1);
    assertThat(localizedFeature.getValue(Locale.GERMAN).getValue()).isEqualTo(LOCALIZED_VALUE_DE);
  }

}
