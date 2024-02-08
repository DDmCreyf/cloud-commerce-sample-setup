package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static com.mirakl.hybris.fulfilmentprocess.constants.MiraklfulfilmentprocessConstants.MARKETPLACE_CONSIGNMENT_SUBPROCESS_NAME;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

public class StartMarketplaceConsignmentProcessesAction extends AbstractProceduralAction<OrderProcessModel> {

  protected BusinessProcessService businessProcessService;

  @Override
  public void executeAction(OrderProcessModel orderProcess) throws RetryLaterException, Exception {
    Set<MarketplaceConsignmentModel> marketplaceConsignments = orderProcess.getOrder().getMarketplaceConsignments();
    if (isNotEmpty(marketplaceConsignments)) {
      int index = 0;
      for (final MarketplaceConsignmentModel consignment : marketplaceConsignments) {
        final MarketplaceConsignmentProcessModel subProcess = businessProcessService
            .createProcess(format("mkp_%s_%s", orderProcess.getCode(), ++index), MARKETPLACE_CONSIGNMENT_SUBPROCESS_NAME);
        subProcess.setParentProcess(orderProcess);
        subProcess.setConsignment(consignment);
        save(subProcess);
        businessProcessService.startProcess(subProcess);
      }
    }
  }

  @Required
  public void setBusinessProcessService(BusinessProcessService businessProcessService) {
    this.businessProcessService = businessProcessService;
  }

}
