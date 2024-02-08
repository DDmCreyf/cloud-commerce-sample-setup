package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_FORMAT;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_VALUES_SEPARATOR;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeUpdateStrategy;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultClassificationAttributeUpdateStrategy implements ClassificationAttributeUpdateStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultClassificationAttributeUpdateStrategy.class);

  protected ClassificationService classificationService;
  protected ConfigurationService configurationService;
  protected ModelService modelService;

  @Override
  public void updateAttributes(Collection<AttributeValueData> attributeValues, ProductImportData data,
      ProductImportFileContextData context) throws ProductImportException {
    if (isEmpty(attributeValues)) {
      return;
    }

    boolean updated = false;
    FeatureList features = classificationService.getFeatures(data.getProductToUpdate());
    Map<String, Feature> featuresByAttributeCode = groupFeaturesByAttributeCode(features);

    for (AttributeValueData attributeValue : attributeValues) {
      Feature feature = featuresByAttributeCode.get(attributeValue.getCode());
      if (feature == null) {
        // Attribute not defined for this product
        continue;
      }
      updated = true;
      try {
        setFeatureValue(feature, attributeValue);
      } catch (ParseException e) {
        throw new ProductImportException(data.getRawProduct(),
            format("Unable to parse date for attribute [%s]. Expected format [%s]",
                feature.getClassAttributeAssignment().getClassificationAttribute().getCode(), getDateFormat()));
      }
    }

    if (updated) {
      if (data.getIdentifiedProduct() != null) {
        classificationService.replaceFeatures(data.getProductToUpdate(), features);
      } else {
        classificationService.setFeatures(data.getProductToUpdate(), features);
      }
      data.getModelsToSave().add(data.getProductToUpdate());
    }
  }

  protected Map<String, Feature> groupFeaturesByAttributeCode(FeatureList features) {
    Map<String, Feature> featureByAttributeCode = new HashMap<>();
    for (Feature feature : features) {
      featureByAttributeCode.put(feature.getClassAttributeAssignment().getClassificationAttribute().getCode(), feature);
    }
    return featureByAttributeCode;
  }

  protected void setFeatureValue(Feature feature, AttributeValueData attributeValue) throws ParseException {
    removeFeatureValues(feature, attributeValue.getLocale());
    if (attributeValue.getValue() == null) {
      return;
    }

    ClassAttributeAssignmentModel assignment = feature.getClassAttributeAssignment();
    List<FeatureValue> featureValues = new ArrayList<>();
    if (isTrue(assignment.getMultiValued())) {
      addFeatureValueMultivalued(featureValues, attributeValue, assignment);
    } else {
      addFeatureValue(featureValues, attributeValue.getValue(), assignment);
    }

    if (feature instanceof LocalizedFeature) {
      ((LocalizedFeature) feature).setValues(featureValues, attributeValue.getLocale());
    } else {
      feature.setValues(featureValues);
    }
  }

  protected void removeFeatureValues(Feature feature, Locale locale) {
    if (feature instanceof LocalizedFeature && locale != null) {
      ((LocalizedFeature) feature).removeAllValues(locale);
    } else {
      feature.removeAllValues();
    }
  }

  protected void addFeatureValueMultivalued(List<FeatureValue> featureValues, AttributeValueData attributeValue,
      ClassAttributeAssignmentModel assignment) throws ParseException {
    String[] values = attributeValue.getValue()
        .split(Pattern.quote(configurationService.getConfiguration().getString(PRODUCTS_IMPORT_VALUES_SEPARATOR)));
    for (String value : values) {
      addFeatureValue(featureValues, value.trim(), assignment);
    }
  }

  protected void addFeatureValue(List<FeatureValue> featureValues, String receivedValue, ClassAttributeAssignmentModel assignment)
      throws ParseException {
    Object convertedValue = convertFeatureValue(assignment, receivedValue);
    if (convertedValue == null) {
      handleUnresolvedValue(assignment, receivedValue);
      return;
    }
    featureValues.add(new FeatureValue(convertedValue, null, assignment.getUnit()));
  }

  protected void handleUnresolvedValue(ClassAttributeAssignmentModel assignment, String receivedValue) {
    LOG.warn(String.format("Unable to resolve value [%s] for attribute [%s-%s]", receivedValue,
        assignment.getClassificationAttribute().getCode(), assignment.getClassificationClass().getCode()));
  }

  protected Object convertFeatureValue(final ClassAttributeAssignmentModel assignment, final String stringValue)
      throws ParseException {
    switch (assignment.getAttributeType()) {
      case STRING:
        return stringValue;
      case BOOLEAN:
        return Boolean.valueOf(stringValue);
      case NUMBER:
        return Double.valueOf(stringValue);
      case DATE:
        return new SimpleDateFormat(getDateFormat()).parse(stringValue);
      case ENUM:
        return FluentIterable.from(assignment.getAttributeValues())
            .firstMatch(new Predicate<ClassificationAttributeValueModel>() {

              @Override
              public boolean apply(ClassificationAttributeValueModel attributeValue) {
                return attributeValue.getCode().equals(stringValue);
              }
            }).orNull();
      default:
        LOG.warn(format("Unknown attribute type for attribute [%s] on class [%s]",
            assignment.getClassificationAttribute().getCode(), assignment.getClassificationClass().getCode()));
        return null;
    }
  }

  protected String getDateFormat() {
    return configurationService.getConfiguration().getString(CATALOG_EXPORT_DATE_FORMAT, "dd-MM-yyyy");
  }

  @Required
  public void setClassificationService(ClassificationService classificationService) {
    this.classificationService = classificationService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
