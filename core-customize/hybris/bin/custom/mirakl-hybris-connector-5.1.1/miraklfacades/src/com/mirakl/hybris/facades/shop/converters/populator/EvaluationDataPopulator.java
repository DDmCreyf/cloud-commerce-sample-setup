package com.mirakl.hybris.facades.shop.converters.populator;

import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.hybris.beans.EvaluationData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class EvaluationDataPopulator implements Populator<MiraklEvaluation, EvaluationData> {
  @Override
  public void populate(MiraklEvaluation miraklEvaluation, EvaluationData evaluationData) throws ConversionException {
    evaluationData.setComment(miraklEvaluation.getComment());
    evaluationData.setDate(miraklEvaluation.getDate());
    evaluationData.setFirstName(miraklEvaluation.getFirstname());
    evaluationData.setGrade(miraklEvaluation.getGrade());
    evaluationData.setLastName(miraklEvaluation.getLastname());
  }
}
