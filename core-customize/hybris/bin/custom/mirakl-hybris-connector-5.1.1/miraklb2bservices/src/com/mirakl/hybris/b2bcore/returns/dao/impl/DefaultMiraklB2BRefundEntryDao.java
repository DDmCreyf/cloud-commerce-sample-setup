package com.mirakl.hybris.b2bcore.returns.dao.impl;

import static java.util.Collections.singletonMap;

import java.util.List;

import com.mirakl.hybris.b2bcore.returns.dao.MiraklB2BRefundEntryDao;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMiraklB2BRefundEntryDao extends DefaultGenericDao<RefundEntryModel> implements MiraklB2BRefundEntryDao {

  private static final String UNPAID_COMPLETED_MARKETPLACE_REFUND_ENTRIES_QUERY = //
      "SELECT {" + RefundEntryModel.PK + "} FROM { "//
          + RefundEntryModel._TYPECODE + " AS re JOIN " + ReturnStatus._TYPECODE + " AS rs "//
          + "ON {re." + RefundEntryModel.STATUS + "} = {rs." + EnumerationValueModel.PK + "} } "//
          + "WHERE {rs." + EnumerationValueModel.CODE + "} = 'COMPLETED' "//
          + "AND {re." + RefundEntryModel.PAYMENTTRANSACTIONENTRY + "} IS NULL "//
          + "AND {" + RefundEntryModel.MIRAKLREFUNDID + "} IS NOT NULL "//
          + "AND {" + RefundEntryModel.CONFIRMEDTOMIRAKL + "} =?" + RefundEntryModel.CONFIRMEDTOMIRAKL;

  public DefaultMiraklB2BRefundEntryDao() {
    super(RefundEntryModel._TYPECODE);
  }

  @Override
  public List<RefundEntryModel> findUnpaidCompletedMarketplaceRefundEntries(boolean confirmedToMirakl) {
    SearchResult<RefundEntryModel> result =
        getFlexibleSearchService().search(new FlexibleSearchQuery(UNPAID_COMPLETED_MARKETPLACE_REFUND_ENTRIES_QUERY,
            singletonMap(RefundEntryModel.CONFIRMEDTOMIRAKL, confirmedToMirakl)));

    return result.getResult();
  }
}
