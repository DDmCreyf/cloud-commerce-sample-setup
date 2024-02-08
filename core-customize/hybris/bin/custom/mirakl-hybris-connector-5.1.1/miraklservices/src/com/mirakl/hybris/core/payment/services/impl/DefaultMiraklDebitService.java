package com.mirakl.hybris.core.payment.services.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.payment.services.MiraklDebitService;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklDebitService implements MiraklDebitService {

  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected ModelService modelService;

  @Override
  public void saveReceivedDebitRequest(MiraklOrderPayment miraklOrderPayment) {
    MarketplaceConsignmentModel consignment =
        marketplaceConsignmentService.getMarketplaceConsignmentForCode(miraklOrderPayment.getOrderId());
    storeDebitRequest(consignment, miraklOrderPayment);
  }

  protected void storeDebitRequest(MarketplaceConsignmentModel consignment, MiraklOrderPayment orderPayment) {
    marketplaceConsignmentService.storeDebitRequest(orderPayment);
    consignment.setPaymentStatus(MarketplaceConsignmentPaymentStatus.INITIAL);
    modelService.save(consignment);
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
