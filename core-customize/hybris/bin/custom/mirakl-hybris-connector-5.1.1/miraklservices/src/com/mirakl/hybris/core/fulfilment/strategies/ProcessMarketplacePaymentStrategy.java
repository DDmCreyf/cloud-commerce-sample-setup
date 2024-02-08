package com.mirakl.hybris.core.fulfilment.strategies;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

public interface ProcessMarketplacePaymentStrategy {

  /**
   * Processes the payment of the marketplace consignments.
   * 
   * @param consignment The marketplace consignment whose payment should be processed
   * @param miraklOrderPayment The Mirakl Order Payment payload
   * @return True or false, depending on the success of the payment processing
   */
  boolean processPayment(MarketplaceConsignmentModel consignment, MiraklOrderPayment miraklOrderPayment);

  /**
   * Processes the payment of the marketplace consignments.
   *
   * @param consignment The marketplace consignment whose payment should be processed
   * @return True or false, depending on the success of the payment processing
   */
  boolean processPayment(MarketplaceConsignmentModel consignment);

}
