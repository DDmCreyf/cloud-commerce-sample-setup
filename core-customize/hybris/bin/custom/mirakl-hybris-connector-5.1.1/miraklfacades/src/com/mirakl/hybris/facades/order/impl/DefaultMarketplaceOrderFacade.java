package com.mirakl.hybris.facades.order.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.facades.order.MarketplaceOrderFacade;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMarketplaceOrderFacade implements MarketplaceOrderFacade {

  protected MiraklOrderService orderService;
  protected Converter<MiraklAssessment, AssessmentData> assessmentDataConverter;

  @Override
  public List<AssessmentData> getAssessments() {
    return assessmentDataConverter.convertAll(orderService.getAssessments());
  }

  @Required
  public void setOrderService(MiraklOrderService orderService) {
    this.orderService = orderService;
  }

  @Required
  public void setAssessmentDataConverter(Converter<MiraklAssessment, AssessmentData> assessmentDataConverter) {
    this.assessmentDataConverter = assessmentDataConverter;
  }
}
