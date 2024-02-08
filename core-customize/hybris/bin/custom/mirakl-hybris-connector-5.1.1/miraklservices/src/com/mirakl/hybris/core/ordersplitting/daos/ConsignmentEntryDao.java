package com.mirakl.hybris.core.ordersplitting.daos;

import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface ConsignmentEntryDao {

  /**
   * Returns the consignment entry for the given code
   *
   * @param miraklOrderLineId The mirakl order line id
   * @return The requested Consignment entry
   */
  ConsignmentEntryModel findConsignmentEntryByMiraklLineId(String miraklOrderLineId);
}
