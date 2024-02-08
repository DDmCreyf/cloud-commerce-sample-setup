package com.mirakl.hybris.core.returns.strategies.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundLookupStrategy;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;

public class DefaultMiraklRefundLookupStrategy implements MiraklRefundLookupStrategy {

  protected MiraklRefundEntryDao miraklRefundEntryDao;

  @Override
  public List<RefundEntryModel> getRefundEntriesPendingPayment() {
    return miraklRefundEntryDao.findMarketplaceRefundEntriesForStatuses(false, ReturnStatus.WAIT);
  }

  @Override
  public List<RefundEntryModel> getProcessedRefundEntriesPendingConfirmation() {
    return miraklRefundEntryDao.findPaidMarketplaceRefundEntries(false);
  }

  @Required
  public void setMiraklRefundEntryDao(MiraklRefundEntryDao miraklRefundEntryDao) {
    this.miraklRefundEntryDao = miraklRefundEntryDao;
  }

}
