package com.mirakl.hybris.facades.order.converters.populator;

import java.util.ArrayList;
import java.util.List;

import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluationAssessment;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class MiraklCreateOrderEvaluationPopulator implements Populator<EvaluationData, MiraklCreateOrderEvaluation> {

  protected boolean defaultEvaluationVisibility;

  @Override
  public void populate(EvaluationData evaluationData, MiraklCreateOrderEvaluation evaluation) throws ConversionException {
    evaluation.setGrade(evaluationData.getGrade());
    evaluation.setComment(evaluationData.getComment());
    List<MiraklCreateOrderEvaluationAssessment> assessments = new ArrayList<>();
    for (AssessmentData assessmentData : evaluationData.getAssessments()) {
      assessments.add(new MiraklCreateOrderEvaluationAssessment(assessmentData.getCode(), assessmentData.getResponse()));
    }
    evaluation.setAssessments(assessments);
    evaluation.setVisible(defaultEvaluationVisibility);
  }

  @Required
  public void setDefaultEvaluationVisibility(boolean defaultEvaluationVisibility) {
    this.defaultEvaluationVisibility = defaultEvaluationVisibility;
  }
}
