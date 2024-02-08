package com.mirakl.hybris.addon.forms.validation;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.mirakl.hybris.addon.forms.AssessmentForm;
import com.mirakl.hybris.addon.forms.ConsignmentEvaluationForm;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ConsignmentEvaluationValidator implements Validator {

  static final List<String> VALID_ASSESSMENT_RESPONSES = Arrays.asList("1", "2", "3", "4", "5", "true", "false");

  @Override
  public boolean supports(Class<?> aClass) {
    return ConsignmentEvaluationForm.class.equals(aClass);
  }

  @Override
  public void validate(Object target, Errors errors) {
    ConsignmentEvaluationForm evaluationForm = (ConsignmentEvaluationForm) target;
    Integer sellerGrade = evaluationForm.getSellerGrade();

    if (sellerGrade == null || sellerGrade > 5 || sellerGrade < 1) {
      errors.rejectValue("sellerGrade", "consignment.evaluation.seller.grade.invalid");
    }

    for (AssessmentForm assessmentForm : evaluationForm.getAssessments()) {
      String response = assessmentForm.getResponse();
      if (StringUtils.isEmpty(response) || !VALID_ASSESSMENT_RESPONSES.contains(response)) {
        errors.rejectValue(null, "consignment.evaluation.assessment.response.invalid");
      }
    }
  }
}
