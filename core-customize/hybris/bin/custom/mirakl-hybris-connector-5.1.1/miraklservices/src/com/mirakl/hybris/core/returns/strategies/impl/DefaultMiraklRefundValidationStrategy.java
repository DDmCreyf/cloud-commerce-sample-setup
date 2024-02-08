package com.mirakl.hybris.core.returns.strategies.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundValidationStrategy;

import de.hybris.platform.returns.model.RefundEntryModel;

public class DefaultMiraklRefundValidationStrategy implements MiraklRefundValidationStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklRefundValidationStrategy.class);

  protected MiraklRefundEntryDao refundEntryDao;

  @Override
  public boolean isValidRefundRequest(MiraklRefundRequestData refundRequest) {
    List<RefundEntryModel> matchingRefunds =
        refundEntryDao.find(singletonMap(RefundEntryModel.MIRAKLREFUNDID, refundRequest.getRefundId()));
    if (isNotEmpty(matchingRefunds)) {
      LOG.warn(format("Found a refund request having the same id [%s]", refundRequest.getRefundId()));
      return false;
    }

    return true;
  }

  @Required
  public void setRefundEntryDao(MiraklRefundEntryDao refundEntryDao) {
    this.refundEntryDao = refundEntryDao;
  }

}
