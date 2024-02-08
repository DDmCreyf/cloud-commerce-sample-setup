package com.mirakl.hybris.b2bcore.returns.strategies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.b2bcore.returns.dao.MiraklB2BRefundEntryDao;
import com.mirakl.hybris.core.returns.strategies.impl.DefaultMiraklRefundLookupStrategy;

import de.hybris.platform.returns.model.RefundEntryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMiraklB2BRefundLookupStrategy extends DefaultMiraklRefundLookupStrategy {

  protected MiraklB2BRefundEntryDao miraklB2BRefundEntryDao;

  @Override
  public List<RefundEntryModel> getProcessedRefundEntriesPendingConfirmation() {
    List<RefundEntryModel> refundEntriesPendingConfirmation =
        new ArrayList<>(super.getProcessedRefundEntriesPendingConfirmation());
    refundEntriesPendingConfirmation.addAll(miraklB2BRefundEntryDao.findUnpaidCompletedMarketplaceRefundEntries(false));
    return refundEntriesPendingConfirmation;
  }

  @Required
  public void setMiraklB2BRefundEntryDao(MiraklB2BRefundEntryDao miraklB2BRefundEntryDao) {
    this.miraklB2BRefundEntryDao = miraklB2BRefundEntryDao;
  }
}
