package com.mirakl.hybris.facades.shop.converters.populator;

import java.util.ArrayList;
import java.util.List;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessmentResponse;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class EvaluationAssessmentDataPopulator implements Populator<MiraklEvaluation, EvaluationData> {
  @Override
  public void populate(MiraklEvaluation miraklEvaluation, EvaluationData evaluationData) throws ConversionException {

    List<AssessmentData> assessmentDataList = new ArrayList<>();
    for (MiraklAssessmentResponse assessment : miraklEvaluation.getAssessments()) {
      AssessmentData assessmentData = new AssessmentData();
      assessmentData.setCode(assessment.getCode());
      assessmentData.setLabel(assessment.getLabel());
      assessmentData.setResponse(assessment.getResponse());
      assessmentDataList.add(assessmentData);
    }
    evaluationData.setAssessments(assessmentDataList);

  }
}
