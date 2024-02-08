package com.mirakl.hybris.core.ordersplitting.daos;

import java.util.List;
import java.util.Set;

import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

public interface MarketplaceConsignmentDao {

  /**
   * Returns the Mirakl consignment with the specified code.
   * 
   * @param code the code of the consignment
   * @return the consignment with the specified code.
   * 
   */
  MarketplaceConsignmentModel findMarketplaceConsignmentByCode(String code);


  /**
   * Returns the Mirakl consignment for the payment status.
   *
   * @param paymentStatuses the payment statuses to search
   * @return the consignments.
   *
   */
  List<MarketplaceConsignmentModel> findMarketplaceConsignmentsByPaymentStatuses(
      Set<MarketplaceConsignmentPaymentStatus> paymentStatuses);
}
