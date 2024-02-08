package com.mirakl.hybris.facades.order.converters.populator;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderEvaluationPopulatorTest {

  public static final String EVALUATION_COMMENT = "evaluationComment";
  public static final String ASSESSMENT_CODE = "assessmentCode";
  public static final String ASSESSMENT_RESPONSE = "assessmentResponse";
  public static final int EVALUATION_SELLER_GRADE = 4;

  @Mock
  private EvaluationData evaluationData;

  @Mock
  private AssessmentData assessmentData;

  @InjectMocks
  private MiraklCreateOrderEvaluationPopulator testObj;

  @Before
  public void setUp() throws Exception {
    when(evaluationData.getGrade()).thenReturn(EVALUATION_SELLER_GRADE);
    when(evaluationData.getComment()).thenReturn(EVALUATION_COMMENT);
    when(evaluationData.getAssessments()).thenReturn(singletonList(assessmentData));
    when(assessmentData.getCode()).thenReturn(ASSESSMENT_CODE);
    when(assessmentData.getResponse()).thenReturn(ASSESSMENT_RESPONSE);
  }

  @Test
  public void populate() throws Exception {
    MiraklCreateOrderEvaluation convertedEvaluation = new MiraklCreateOrderEvaluation();
    testObj.setDefaultEvaluationVisibility(true);

    testObj.populate(evaluationData, convertedEvaluation);

    assertThat(convertedEvaluation.getComment()).isEqualTo(EVALUATION_COMMENT);
    assertThat(convertedEvaluation.getGrade()).isEqualTo(EVALUATION_SELLER_GRADE);
    assertThat(convertedEvaluation.getAssessments()).isNotEmpty();
    assertThat(convertedEvaluation.getAssessments().get(0).getCode()).isEqualTo(ASSESSMENT_CODE);
    assertThat(convertedEvaluation.getAssessments().get(0).getResponse()).isEqualTo(ASSESSMENT_RESPONSE);
    assertThat(convertedEvaluation.isVisible()).isEqualTo(true);
  }
}
