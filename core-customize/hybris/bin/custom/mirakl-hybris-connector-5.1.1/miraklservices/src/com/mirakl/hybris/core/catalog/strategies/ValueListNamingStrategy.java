package com.mirakl.hybris.core.catalog.strategies;

import java.util.Locale;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

public interface ValueListNamingStrategy {

  /**
   * Generates a value list code based on a {@link ClassAttributeAssignmentModel}
   * 
   * @param classAttributeAssignment
   * @return a value list code
   */
  String getCode(ClassAttributeAssignmentModel classAttributeAssignment);

  /**
   * Generates a value list label based on a {@link ClassAttributeAssignmentModel}
   *
   * @param classAttributeAssignment
   * @param locale the locale to use for the label
   * @return a value list label
   */
  String getLabel(ClassAttributeAssignmentModel classAttributeAssignment, Locale locale);

  /**
   * Generates a value list code based on a {@link MiraklCoreAttributeModel}
   * 
   * @param coreAttribute
   * @return a value list code
   */
  String getCode(MiraklCoreAttributeModel coreAttribute);

  /**
   * Generates a value list label based on a {@link MiraklCoreAttributeModel}
   *
   * @param coreAttribute
   * @param locale the locale to use for the label
   * @return a value list label
   */
  String getLabel(MiraklCoreAttributeModel coreAttribute, Locale locale);

}
