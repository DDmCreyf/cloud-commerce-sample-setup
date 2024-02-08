package com.mirakl.hybris.core.order.strategies;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

public interface MarketplaceConsignmentMessagesStrategy {
  /**
   * Strategy allowing or not the user to contact a seller for a given consignment
   *
   * @param consignment The marketplace consignment (== Mirakl Order) to check
   * @return true when users should be able to contact the seller, false otherwise
   */
  boolean canWriteMessages(MarketplaceConsignmentModel consignment);
}
