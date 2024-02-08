package com.mirakl.hybris.core.payment.strategies;

import java.util.List;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

public interface LookupMiraklDebitsToProcessStrategy {

  /**
   * Lookup the consignment that have debits that needs to be processed
   * 
   * @return the consignment with debit to process
   */
  List<MarketplaceConsignmentModel> lookupDebitsToProcess();
}
