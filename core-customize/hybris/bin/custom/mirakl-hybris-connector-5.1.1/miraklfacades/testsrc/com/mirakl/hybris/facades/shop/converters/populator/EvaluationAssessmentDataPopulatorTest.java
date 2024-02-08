package com.mirakl.hybris.facades.shop.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessmentResponse;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@RunWith(value = MockitoJUnitRunner.class)
public class EvaluationAssessmentDataPopulatorTest {

  public static final String ASSESSMENT_CODE = "assessment_code";
  public static final String ASSESSMENT_LABEL = "assessment_label";
  public static final String ASSESSMENT_RESPONSE = "assessment_response";
  @InjectMocks
  private EvaluationAssessmentDataPopulator testObj = new EvaluationAssessmentDataPopulator();

  @Mock
  private MiraklEvaluation mockedMiraklEvaluation;

  @Mock
  private MiraklAssessmentResponse mockedMiraklAssessment;

  @Before
  public void setUp() {
    when(mockedMiraklEvaluation.getAssessments()).thenReturn(Collections.singletonList(mockedMiraklAssessment));
    when(mockedMiraklAssessment.getCode()).thenReturn(ASSESSMENT_CODE);
    when(mockedMiraklAssessment.getLabel()).thenReturn(ASSESSMENT_LABEL);
    when(mockedMiraklAssessment.getResponse()).thenReturn(ASSESSMENT_RESPONSE);
  }

  @Test
  public void populateIfAssessmentsAreFoundTest() {
    EvaluationData output = new EvaluationData();
    testObj.populate(mockedMiraklEvaluation, output);

    List<AssessmentData> assessments = output.getAssessments();
    assertThat(assessments).hasSize(1);
    assertThat(assessments.get(0).getCode()).isEqualTo(ASSESSMENT_CODE);
    assertThat(assessments.get(0).getLabel()).isEqualTo(ASSESSMENT_LABEL);
    assertThat(assessments.get(0).getResponse()).isEqualTo(ASSESSMENT_RESPONSE);
  }

  @Test
  public void populateIfNoAssessmentsAreFoundTest() {
    when(mockedMiraklEvaluation.getAssessments()).thenReturn(Collections.<MiraklAssessmentResponse>emptyList());
    EvaluationData output = new EvaluationData();
    testObj.populate(mockedMiraklEvaluation, output);

    assertThat(output.getAssessments()).hasSize(0);
  }

}
