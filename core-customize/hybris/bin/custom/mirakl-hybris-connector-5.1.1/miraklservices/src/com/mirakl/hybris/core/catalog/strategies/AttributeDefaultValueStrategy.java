package com.mirakl.hybris.core.catalog.strategies;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public interface AttributeDefaultValueStrategy {

  /**
   * Returns the default value of an attribute
   * 
   * @param classAttributeAssignment
   * @return the default value
   */
  <T> T getDefaultValue(ClassAttributeAssignmentModel classAttributeAssignment);

}
