package com.mirakl.hybris.core.catalog.strategies.impl;

import com.mirakl.hybris.core.catalog.strategies.AttributeVarianceStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public class DefaultAttributeVarianceStrategy implements AttributeVarianceStrategy {

  @Override
  public boolean isVariant(ClassAttributeAssignmentModel classAttributeAssignment) {
    return false;
  }

}
