package com.mirakl.hybris.core.catalog.strategies;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public interface ClassificationAttributeExportEligibilityStrategy {

  /**
   * Determines if a classification attribute assignment is eligible for export
   * 
   * @param assignment
   * @return true if the attribute is eligible to be exported to Mirakl
   */
  boolean isExportableAttribute(ClassAttributeAssignmentModel assignment);

}
