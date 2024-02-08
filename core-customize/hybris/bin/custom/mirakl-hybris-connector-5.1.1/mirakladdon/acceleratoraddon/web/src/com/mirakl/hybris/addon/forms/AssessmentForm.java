package com.mirakl.hybris.addon.forms;

import com.mirakl.hybris.core.constants.GeneratedMiraklservicesConstants.Enumerations.AssessmentType;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class AssessmentForm {

  private String code;

  private String response;

  private AssessmentType type;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public AssessmentType getType() {
    return type;
  }

  public void setType(AssessmentType type) {
    this.type = type;
  }
}
