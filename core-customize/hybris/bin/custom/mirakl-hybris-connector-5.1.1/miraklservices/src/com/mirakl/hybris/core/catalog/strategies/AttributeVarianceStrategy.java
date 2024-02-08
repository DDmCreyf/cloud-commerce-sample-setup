package com.mirakl.hybris.core.catalog.strategies;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public interface AttributeVarianceStrategy {

  /**
   * Checks if a classification attribute is variant
   * 
   * @param classAttributeAssignment the classification attribute assignment to be checked
   * @return true if the attribute is variant
   */
  boolean isVariant(ClassAttributeAssignmentModel classAttributeAssignment);

}
