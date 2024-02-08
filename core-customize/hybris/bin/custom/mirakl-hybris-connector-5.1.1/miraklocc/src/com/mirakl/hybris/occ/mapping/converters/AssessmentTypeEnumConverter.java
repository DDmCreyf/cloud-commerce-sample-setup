package com.mirakl.hybris.occ.mapping.converters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.mirakl.hybris.core.enums.AssessmentType;

import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

/**
 * Bidirectional converter between {@link AssessmentType} and String
 */
@WsDTOMapping
public class AssessmentTypeEnumConverter extends BidirectionalConverter<AssessmentType, String> {

  @Override
  public String convertTo(AssessmentType assessmentType, Type<String> type, MappingContext mappingContext) {
    return assessmentType.toString();
  }

  @Override
  public AssessmentType convertFrom(String source, Type<AssessmentType> type, MappingContext mappingContext) {
    return AssessmentType.valueOf(source);
  }
}


