package com.mirakl.hybris.facades.order;

import java.util.List;

import com.mirakl.hybris.beans.AssessmentData;


public interface MarketplaceOrderFacade {

  /**
   * Gets the Assessments as defined in Mirakl
   *
   * @return the requested assessmentData as a list
   */
  List<AssessmentData> getAssessments();
}
