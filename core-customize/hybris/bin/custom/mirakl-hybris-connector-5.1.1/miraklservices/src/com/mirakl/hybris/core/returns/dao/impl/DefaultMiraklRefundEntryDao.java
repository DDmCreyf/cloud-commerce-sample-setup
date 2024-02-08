package com.mirakl.hybris.core.returns.dao.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultMiraklRefundEntryDao extends DefaultGenericDao<RefundEntryModel> implements MiraklRefundEntryDao {

  private static final String MARKETPLACE_REFUND_ENTRIES_FOR_STATUS_QUERY =
      "SELECT {" + RefundEntryModel.PK + "} FROM {" + RefundEntryModel._TYPECODE + "}"//
          + " WHERE {" + RefundEntryModel.STATUS + "} IN (?" + RefundEntryModel.STATUS + ")"//
          + " AND {" + RefundEntryModel.MIRAKLREFUNDID + "} IS NOT NULL" //
          + " AND {" + RefundEntryModel.CONFIRMEDTOMIRAKL + "} =?" + RefundEntryModel.CONFIRMEDTOMIRAKL;

  private static final String PAID_MARKETPLACE_REFUND_ENTRIES_QUERY =
      "SELECT {" + RefundEntryModel.PK + "} FROM {" + RefundEntryModel._TYPECODE + "}"//
          + " WHERE {" + RefundEntryModel.PAYMENTTRANSACTIONENTRY + "} IS NOT NULL"//
          + " AND {" + RefundEntryModel.MIRAKLREFUNDID + "} IS NOT NULL" //
          + " AND {" + RefundEntryModel.CONFIRMEDTOMIRAKL + "} =?" + RefundEntryModel.CONFIRMEDTOMIRAKL;

  public DefaultMiraklRefundEntryDao() {
    super(RefundEntryModel._TYPECODE);
  }

  @Override
  public List<RefundEntryModel> findMarketplaceRefundEntriesForStatuses(boolean confirmedToMirakl,
      ReturnStatus... returnStatuses) {
    checkArgument(returnStatuses != null, "returnStatuses should not be null");

    Map<String, Object> params = new HashMap<>();
    params.put(RefundEntryModel.STATUS, asList(returnStatuses));
    params.put(RefundEntryModel.CONFIRMEDTOMIRAKL, confirmedToMirakl);

    SearchResult<RefundEntryModel> result =
        getFlexibleSearchService().search(new FlexibleSearchQuery(MARKETPLACE_REFUND_ENTRIES_FOR_STATUS_QUERY, params));

    return result.getResult();
  }

  @Override
  public List<RefundEntryModel> findPaidMarketplaceRefundEntries(boolean confirmedToMirakl) {
    SearchResult<RefundEntryModel> result =
        getFlexibleSearchService().search(new FlexibleSearchQuery(PAID_MARKETPLACE_REFUND_ENTRIES_QUERY,
            singletonMap(RefundEntryModel.CONFIRMEDTOMIRAKL, confirmedToMirakl)));

    return result.getResult();
  }

}
