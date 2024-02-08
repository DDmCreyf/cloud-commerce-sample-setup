package com.mirakl.hybris.facades.order.converters.populator;

import static com.mirakl.hybris.core.enums.AssessmentType.valueOf;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.hybris.beans.AssessmentData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class AssessmentDataPopulator implements Populator<MiraklAssessment, AssessmentData> {
  @Override
  public void populate(MiraklAssessment miraklAssessment, AssessmentData assessmentData) throws ConversionException {
    assessmentData.setCode(miraklAssessment.getCode());
    assessmentData.setLabel(miraklAssessment.getLabel());
    assessmentData.setType(valueOf(miraklAssessment.getType().name()));
  }
}
