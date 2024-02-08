package com.mirakl.hybris.core.catalog.strategies.impl;

import com.mirakl.hybris.core.catalog.strategies.AttributeDefaultValueStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public class DefaultAttributeDefaultValueStrategy implements AttributeDefaultValueStrategy {

  @Override
  public <T> T getDefaultValue(ClassAttributeAssignmentModel classAttributeAssignment) {
    return null;
  }

}
