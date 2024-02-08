package com.mirakl.hybris.addon.forms;

import java.util.List;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ConsignmentEvaluationForm {

  private List<AssessmentForm> assessments;

  private Integer sellerGrade;

  private String comment;

  public List<AssessmentForm> getAssessments() {
    return assessments;
  }

  public void setAssessments(List<AssessmentForm> assessments) {
    this.assessments = assessments;
  }

  public Integer getSellerGrade() {
    return sellerGrade;
  }

  public void setSellerGrade(Integer sellerGrade) {
    this.sellerGrade = sellerGrade;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
