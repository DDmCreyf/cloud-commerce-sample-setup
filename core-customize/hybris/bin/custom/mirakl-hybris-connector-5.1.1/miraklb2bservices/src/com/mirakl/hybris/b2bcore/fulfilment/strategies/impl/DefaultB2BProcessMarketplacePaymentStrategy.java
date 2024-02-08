package com.mirakl.hybris.b2bcore.fulfilment.strategies.impl;

import static com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus.INITIAL;
import static com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus.SUCCESS;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.fulfilment.strategies.impl.DefaultProcessMarketplacePaymentStrategy;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

public class DefaultB2BProcessMarketplacePaymentStrategy extends DefaultProcessMarketplacePaymentStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultB2BProcessMarketplacePaymentStrategy.class);

  protected SkipPaymentStrategy skipPaymentStrategy;

  @Override
  public boolean processPayment(MarketplaceConsignmentModel consignment, MiraklOrderPayment miraklOrderPayment) {
    boolean success = true;
    if (INITIAL.equals(consignment.getPaymentStatus())) {
      if (skipPaymentStrategy.shouldSkipPayment(consignment.getOrder())) {
        skipPayment(consignment);
      } else {
        success = capturePayment(consignment, miraklOrderPayment);
      }
    }
    confirmOrderDebitToMirakl(consignment, miraklOrderPayment);
    return success;
  }

  @Override
  public boolean processPayment(MarketplaceConsignmentModel consignment) {
    MiraklOrderPayment miraklOrderPayment = marketplaceConsignmentService.loadDebitRequest(consignment);
    if(miraklOrderPayment != null) {
      return processPayment(consignment, miraklOrderPayment);
    }
    return false;
  }

  protected void skipPayment(MarketplaceConsignmentModel consignment) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("Skipping Payment Action for marketplace consignment [%s]...", consignment.getCode()));
    }
    consignment.setPaymentStatus(SUCCESS);
  }

  @Required
  public void setSkipPaymentStrategy(SkipPaymentStrategy skipPaymentStrategy) {
    this.skipPaymentStrategy = skipPaymentStrategy;
  }
}
