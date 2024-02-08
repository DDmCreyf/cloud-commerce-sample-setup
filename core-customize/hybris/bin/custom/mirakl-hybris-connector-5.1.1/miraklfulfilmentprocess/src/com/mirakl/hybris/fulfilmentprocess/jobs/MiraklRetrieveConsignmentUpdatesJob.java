package com.mirakl.hybris.fulfilmentprocess.jobs;

import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static com.mirakl.hybris.fulfilmentprocess.constants.MiraklfulfilmentprocessConstants.MAX_CONSIGNMENT_UPDATE_PAGE_SIZE;
import static java.lang.String.format;

import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrders;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.order.MiraklGetOrdersRequest;
import com.mirakl.hybris.core.model.MiraklRetrieveConsignmentUpdatesCronJobModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.util.PaginationUtils;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklRetrieveConsignmentUpdatesJob extends AbstractJobPerformable<MiraklRetrieveConsignmentUpdatesCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklRetrieveConsignmentUpdatesJob.class);

  protected MiraklMarketplacePlatformFrontApi miraklFrontApi;

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  protected ConfigurationService configurationService;


  @Override
  public PerformResult perform(MiraklRetrieveConsignmentUpdatesCronJobModel cronJob) {
    int page = 0;
    Integer pagesNeeded = null;
    Date lastUpdateTime = cronJob.getLastUpdateTime();
    do {
      MiraklGetOrdersRequest orderUpdatesRequest = buildGetOrdersRequest(cronJob.getLastUpdateTime(), page++);
      MiraklOrders miraklOrders = miraklFrontApi.getOrders(orderUpdatesRequest);
      for (MiraklOrder miraklOrder : miraklOrders.getOrders()) {
        performUpdate(miraklOrder);
        lastUpdateTime = getMostRecentUpdateTime(lastUpdateTime, miraklOrder.getLastUpdatedDate());
      }
      pagesNeeded = calculateNeededPages(miraklOrders.getTotalCount(), pagesNeeded);
    } while (page < pagesNeeded);

    postPerform(cronJob, lastUpdateTime);

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected MiraklGetOrdersRequest buildGetOrdersRequest(Date lastExecutionDate, int page) {
    MiraklGetOrdersRequest orderUpdatesRequest = new MiraklGetOrdersRequest();
    orderUpdatesRequest.setStartUpdateDate(lastExecutionDate);
    return PaginationUtils.applyMiraklFullPagination(orderUpdatesRequest, true, getMaxResultsByPage(),
        getMaxResultsByPage() * page);
  }

  protected void performUpdate(MiraklOrder miraklOrder) {
    try {
      marketplaceConsignmentService.receiveConsignmentUpdate(miraklOrder);
    } catch (Exception e) {
      handleUpdateException(miraklOrder, e);
    }
  }

  protected void postPerform(MiraklRetrieveConsignmentUpdatesCronJobModel cronJob, Date lastUpdateTime) {
    cronJob.setLastUpdateTime(lastUpdateTime);
    modelService.save(cronJob);
  }

  protected void handleUpdateException(MiraklOrder miraklOrder, Exception e) {
    LOG.error(format("Unable to update conignment[%s]", miraklOrder.getId()), e);
  }

  protected Date getMostRecentUpdateTime(Date actualLastUpdateTime, Date receivedUpdateTime) {
    if (actualLastUpdateTime == null || actualLastUpdateTime.before(receivedUpdateTime)
        || actualLastUpdateTime.equals(receivedUpdateTime)) {
      return new DateTime(receivedUpdateTime).plusSeconds(1).toDate();
    }
    return actualLastUpdateTime;
  }

  protected Integer calculateNeededPages(Integer neededPages, long totalCount) {
    return calculateNeededPages(totalCount, neededPages);
  }

  protected Integer calculateNeededPages(long totalCount, Integer calculatedNeededPages) {
    Integer neededPages = calculatedNeededPages;
    if (neededPages == null) {
      neededPages = getNumberOfPages(totalCount, getMaxResultsByPage());
      if (LOG.isDebugEnabled() && neededPages > 0) {
        LOG.debug(format("Total consignment updates [%s], Page size:[%s], Pages needed:[%s] page(s)", totalCount,
            getMaxResultsByPage(), neededPages));
      }
    }
    return neededPages;
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt(MAX_CONSIGNMENT_UPDATE_PAGE_SIZE, 100);
  }

  @Required
  public void setMiraklFrontApi(MiraklMarketplacePlatformFrontApi miraklFrontApi) {
    this.miraklFrontApi = miraklFrontApi;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
