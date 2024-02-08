package com.mirakl.hybris.b2bcore.returns.dao;

import java.util.List;

import de.hybris.platform.returns.model.RefundEntryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MiraklB2BRefundEntryDao {

  /**
   * Returns marketplace refund entries which are tagged as Completed but without having a paymentTransactionEntry. It occurs
   * mainly for orders whose refund was skipped. See {@link com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy} for
   * more information.
   *
   * @param confirmedToMirakl Orders whose refund was already confirmed to Mirakl
   * @return a List of refund entries matching the requirements
   */
  List<RefundEntryModel> findUnpaidCompletedMarketplaceRefundEntries(boolean confirmedToMirakl);
}
