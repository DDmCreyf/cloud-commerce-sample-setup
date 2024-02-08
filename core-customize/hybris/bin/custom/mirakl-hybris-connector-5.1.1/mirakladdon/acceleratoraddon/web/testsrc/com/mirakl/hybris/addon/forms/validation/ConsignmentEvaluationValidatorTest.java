package com.mirakl.hybris.addon.forms.validation;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;

import com.mirakl.hybris.addon.forms.AssessmentForm;
import com.mirakl.hybris.addon.forms.ConsignmentEvaluationForm;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentEvaluationValidatorTest {

  public static final String EVALUATION_COMMENT = "comment";
  public static final int EVALUATION_SELLER_GRADE = 4;
  public static final String ASSESSMENT_CODE = "assessmentCode";
  public static final String ASSESSMENT_RESPONSE = "true";

  @Mock
  private ConsignmentEvaluationForm form;

  @Mock
  private AssessmentForm assessment;

  @Mock
  private BeanPropertyBindingResult bindingResult;

  @InjectMocks
  private ConsignmentEvaluationValidator testObj;

  @Before
  public void setUp() throws Exception {
    when(assessment.getCode()).thenReturn(ASSESSMENT_CODE);
    when(assessment.getResponse()).thenReturn(ASSESSMENT_RESPONSE);

    when(form.getAssessments()).thenReturn(Collections.singletonList(assessment));
    when(form.getComment()).thenReturn(EVALUATION_COMMENT);
    when(form.getSellerGrade()).thenReturn(EVALUATION_SELLER_GRADE);
  }

  @Test
  public void validateWhenNoErrors() throws Exception {
    testObj.validate(form, bindingResult);

    verifyZeroInteractions(bindingResult);
  }

  @Test
  public void validateErrorWhenNoGrade() throws Exception {
    when(form.getSellerGrade()).thenReturn(null);

    testObj.validate(form, bindingResult);

    verify(bindingResult).rejectValue(anyString(), anyString());
  }

  @Test
  public void validateErrorWhenNoAssessment() throws Exception {
    when(assessment.getResponse()).thenReturn(StringUtils.EMPTY);

    testObj.validate(form, bindingResult);

    verify(bindingResult).rejectValue(anyString(), anyString());
  }
}
