package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;

import java.util.Locale;

import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

public class DefaultValueListNamingStrategy implements ValueListNamingStrategy {

  @Override
  public String getCode(ClassAttributeAssignmentModel classAttributeAssignment) {
    return format("%s-%s", classAttributeAssignment.getClassificationAttribute().getCode(),
        classAttributeAssignment.getClassificationClass().getCode());
  }

  @Override
  public String getLabel(ClassAttributeAssignmentModel classAttributeAssignment, Locale locale) {
    return format("%s-%s", getClassificationAttributeLabel(classAttributeAssignment, locale),
        getClassificationClassLabel(classAttributeAssignment, locale));
  }

  @Override
  public String getCode(MiraklCoreAttributeModel coreAttribute) {
    return format("%s-values", coreAttribute.getUid());
  }

  @Override
  public String getLabel(MiraklCoreAttributeModel coreAttribute, Locale locale) {
    return coreAttribute.getLabel(locale);
  }

  protected String getClassificationClassLabel(ClassAttributeAssignmentModel classAttributeAssignment, Locale locale) {
    ClassificationClassModel classificationClass = classAttributeAssignment.getClassificationClass();
    if (classificationClass.getName(locale) == null) {
      return (classificationClass.getCode());
    }
    return classificationClass.getName(locale);
  }

  protected String getClassificationAttributeLabel(ClassAttributeAssignmentModel classAttributeAssignment, Locale locale) {
    ClassificationAttributeModel classificationAttribute = classAttributeAssignment.getClassificationAttribute();
    if (classificationAttribute.getName(locale) == null) {
      return classificationAttribute.getCode();
    }
    return classificationAttribute.getName(locale);
  }


}
