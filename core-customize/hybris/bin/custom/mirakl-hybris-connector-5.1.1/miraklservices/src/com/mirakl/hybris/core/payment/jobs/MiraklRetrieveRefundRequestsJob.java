package com.mirakl.hybris.core.payment.jobs;

import static com.google.common.primitives.Ints.checkedCast;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MAX_REFUND_ORDERS_PAGE_SIZE;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderRefundsWithPagination;
import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundOrder;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.payment.refund.MiraklGetOrderRefundsRequest;
import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.model.MiraklRetrieveRefundRequestsCronJobModel;
import com.mirakl.hybris.core.payment.services.MiraklRefundService;
import com.mirakl.hybris.core.util.PaginationUtils;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklRetrieveRefundRequestsJob extends AbstractJobPerformable<MiraklRetrieveRefundRequestsCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklRetrieveRefundRequestsJob.class);

  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected MiraklRefundService miraklRefundService;
  protected ConfigurationService configurationService;

  protected Converter<MiraklRefundOrder, List<MiraklRefundRequestData>> miraklRefundsRequestDataConverter;

  @Override
  public PerformResult perform(MiraklRetrieveRefundRequestsCronJobModel cronJob) {
    int page = 0;
    Integer pagesNeeded = null;
    List<MiraklRefundOrder> ordersToSave = new ArrayList<>();
    do {
      MiraklOrderRefundsWithPagination orderRefundsWithPagination =
          mmpApi.getOrderRefunds(buildMiraklGetOrderRefundsRequest(page++));
      if (pagesNeeded == null) {
        pagesNeeded = getNumberOfPages(checkedCast(orderRefundsWithPagination.getTotalCount()), getMaxResultsByPage());
      }
      ordersToSave.addAll(orderRefundsWithPagination.getOrders().getOrder());
    } while (page < pagesNeeded);
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Retrieved refunds from Mirakl using PA12: %d", ordersToSave.size()));
    }
    for (List<MiraklRefundRequestData> miraklRefundRequestData : miraklRefundsRequestDataConverter.convertAll(ordersToSave)) {
      for (MiraklRefundRequestData refund : miraklRefundRequestData) {
        performSave(refund);
      }
    }

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }


  protected void performSave(MiraklRefundRequestData refund) {
    try {
      miraklRefundService.saveReceivedRefundRequest(refund);
    } catch (Exception e) {
      handleSaveException(refund, e);
    }
  }

  protected void handleSaveException(MiraklRefundRequestData refund, Exception e) {
    LOG.error(format("Error while saving refund [%s], this refund will be ignored", refund.getRefundId()), e);
  }

  private MiraklGetOrderRefundsRequest buildMiraklGetOrderRefundsRequest(int pageNumber) {
    return PaginationUtils.applyMiraklFullPagination(new MiraklGetOrderRefundsRequest(), true, getMaxResultsByPage(),
        pageNumber * getMaxResultsByPage());
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt(MAX_REFUND_ORDERS_PAGE_SIZE, 100);
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.mmpApi = miraklApi;
  }

  @Required
  public void setMiraklRefundService(MiraklRefundService miraklRefundService) {
    this.miraklRefundService = miraklRefundService;
  }

  @Required
  public void setMiraklRefundRequestDataConverter(
      Converter<MiraklRefundOrder, List<MiraklRefundRequestData>> miraklRefundsRequestDataConverter) {
    this.miraklRefundsRequestDataConverter = miraklRefundsRequestDataConverter;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}

