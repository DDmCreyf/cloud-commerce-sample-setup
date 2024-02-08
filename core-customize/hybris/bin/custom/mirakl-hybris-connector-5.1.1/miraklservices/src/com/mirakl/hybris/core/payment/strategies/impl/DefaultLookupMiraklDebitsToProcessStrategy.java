package com.mirakl.hybris.core.payment.strategies.impl;

import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.payment.strategies.LookupMiraklDebitsToProcessStrategy;
import org.springframework.beans.factory.annotation.Required;

import java.util.EnumSet;
import java.util.List;

public class DefaultLookupMiraklDebitsToProcessStrategy implements LookupMiraklDebitsToProcessStrategy {

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public List<MarketplaceConsignmentModel> lookupDebitsToProcess(){
    return marketplaceConsignmentService.getMarketplaceConsignmentsForPaymentStatuses(EnumSet.of(
        MarketplaceConsignmentPaymentStatus.INITIAL));
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

}
