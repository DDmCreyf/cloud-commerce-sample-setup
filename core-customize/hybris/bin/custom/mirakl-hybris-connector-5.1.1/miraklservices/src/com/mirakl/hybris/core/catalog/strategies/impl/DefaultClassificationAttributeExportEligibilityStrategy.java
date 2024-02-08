package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeExportEligibilityStrategy;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public class DefaultClassificationAttributeExportEligibilityStrategy implements ClassificationAttributeExportEligibilityStrategy {

  @Override
  public boolean isExportableAttribute(ClassAttributeAssignmentModel assignment) {
    if (assignment == null) {
      return false;
    }
    if (ClassificationAttributeTypeEnum.ENUM == assignment.getAttributeType() && isEmpty(assignment.getAttributeValues())) {
      return false;
    }
    if (assignment.getOperatorExclusive() == null) {
      return !assignment.getClassificationAttribute().isOperatorExclusive();
    }

    return !assignment.getOperatorExclusive().booleanValue();
  }

}
