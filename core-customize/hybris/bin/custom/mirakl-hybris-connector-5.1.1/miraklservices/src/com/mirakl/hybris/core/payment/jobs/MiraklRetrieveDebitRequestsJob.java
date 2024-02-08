package com.mirakl.hybris.core.payment.jobs;

import static com.google.common.primitives.Ints.checkedCast;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.MAX_DEBIT_ORDERS_PAGE_SIZE;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.payment.debit.MiraklDebitOrder;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderDebitsWithPagination;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.payment.debit.MiraklGetOrderDebitsRequest;
import com.mirakl.hybris.core.model.MiraklRetrieveDebitRequestsCronJobModel;
import com.mirakl.hybris.core.payment.services.MiraklDebitService;
import com.mirakl.hybris.core.util.PaginationUtils;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklRetrieveDebitRequestsJob extends AbstractJobPerformable<MiraklRetrieveDebitRequestsCronJobModel> {
  private static final Logger LOG = Logger.getLogger(MiraklRetrieveDebitRequestsJob.class);

  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected MiraklDebitService miraklDebitService;
  protected ConfigurationService configurationService;
  protected Converter<MiraklDebitOrder, MiraklOrderPayment> miraklOrderPaymentConverter;

  @Override
  public PerformResult perform(MiraklRetrieveDebitRequestsCronJobModel cronJob) {
    int page = 0;
    Integer pagesNeeded = null;
    List<MiraklDebitOrder> ordersToSave = new ArrayList<>();
    do {
      MiraklOrderDebitsWithPagination orderDebitsWithPagination = mmpApi.getOrderDebits(buildMiraklGetOrderDebitsRequest(page++));
      if (pagesNeeded == null) {
        pagesNeeded = getNumberOfPages(checkedCast(orderDebitsWithPagination.getTotalCount()), getMaxResultsByPage());
      }
      ordersToSave.addAll(orderDebitsWithPagination.getOrders().getOrder());
    } while (page < pagesNeeded);
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Retrieved debits from Mirakl using PA11: %d", ordersToSave.size()));
    }
    for (MiraklOrderPayment debit : miraklOrderPaymentConverter.convertAll(ordersToSave)) {
      performSave(debit);
    }

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected void performSave(MiraklOrderPayment debit) {
    try {
      miraklDebitService.saveReceivedDebitRequest(debit);
    } catch (Exception e) {
      handleSaveException(debit, e);
    }
  }

  protected void handleSaveException(MiraklOrderPayment debit, Exception e) {
    LOG.error(format(format("Error while saving debit for order [%s], this debit will be ignored", debit.getOrderId(), e)));
  }

  private MiraklGetOrderDebitsRequest buildMiraklGetOrderDebitsRequest(int pageNumber) {
    return PaginationUtils.applyMiraklFullPagination(new MiraklGetOrderDebitsRequest(), true, getMaxResultsByPage(),
        pageNumber * getMaxResultsByPage());
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt(MAX_DEBIT_ORDERS_PAGE_SIZE, 100);
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.mmpApi = miraklApi;
  }

  @Required
  public void setMiraklDebitService(MiraklDebitService miraklDebitService) {
    this.miraklDebitService = miraklDebitService;
  }

  @Required
  public void setMiraklOrderPaymentConverter(Converter<MiraklDebitOrder, MiraklOrderPayment> miraklOrderPaymentConverter) {
    this.miraklOrderPaymentConverter = miraklOrderPaymentConverter;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
