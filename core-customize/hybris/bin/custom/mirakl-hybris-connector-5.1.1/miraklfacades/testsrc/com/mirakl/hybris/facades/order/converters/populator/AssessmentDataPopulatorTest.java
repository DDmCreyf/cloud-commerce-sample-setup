package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.client.mmp.domain.evaluation.MiraklAssessmentType;
import com.mirakl.hybris.beans.AssessmentData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssessmentDataPopulatorTest {

  public static final String ASSESSMENT_CODE = "assessmentCode";
  public static final String ASSESSMENT_LABEL = "assessmentLabel";
  public static final String ASSESSMENT_BOOLEAN_TYPE = "BOOLEAN";

  @Mock
  private MiraklAssessment miraklAssessment;

  @InjectMocks
  private AssessmentDataPopulator testObj;

  @Before
  public void setUp() throws Exception {
    when(miraklAssessment.getCode()).thenReturn(ASSESSMENT_CODE);
    when(miraklAssessment.getLabel()).thenReturn(ASSESSMENT_LABEL);
    when(miraklAssessment.getType()).thenReturn(MiraklAssessmentType.BOOLEAN);
  }

  @Test
  public void populate() throws Exception {
    AssessmentData assessmentData = new AssessmentData();
    testObj.populate(miraklAssessment, assessmentData);

    assertThat(assessmentData.getCode()).isEqualTo(ASSESSMENT_CODE);
    assertThat(assessmentData.getLabel()).isEqualTo(ASSESSMENT_LABEL);
    assertThat(assessmentData.getType().getCode()).isEqualTo(ASSESSMENT_BOOLEAN_TYPE);
  }
}
