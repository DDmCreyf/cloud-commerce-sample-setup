package com.mirakl.hybris.core.returns.dao;

import java.util.List;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklRefundEntryDao extends GenericDao<RefundEntryModel> {

  /**
   * Retrieves refund entries received from Mirakl and matching some given statuses
   * 
   * @param confirmedToMirakl match only refund entries already confirmed to Mirakl
   * @param statuses statuses the status to be searched for
   * @return refund entries matching the given statuses
   */
  List<RefundEntryModel> findMarketplaceRefundEntriesForStatuses(boolean confirmedToMirakl, ReturnStatus... statuses);

  /**
   * Retrieves paid refund entries (having a payment transaction entry)
   * 
   * @param confirmedToMirakl match only refund entries already confirmed to Mirakl
   * @return paid refund entries
   */
  List<RefundEntryModel> findPaidMarketplaceRefundEntries(boolean confirmedToMirakl);


}
