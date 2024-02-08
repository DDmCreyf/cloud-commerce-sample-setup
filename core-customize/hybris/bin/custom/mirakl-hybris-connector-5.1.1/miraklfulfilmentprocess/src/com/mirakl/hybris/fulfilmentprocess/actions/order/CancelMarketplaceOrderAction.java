package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

public class CancelMarketplaceOrderAction extends AbstractProceduralAction<OrderProcessModel> {

  private static final Logger LOG = Logger.getLogger(CancelMarketplaceOrderAction.class);

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public void executeAction(OrderProcessModel orderProcess) throws RetryLaterException, Exception {
    OrderModel order = orderProcess.getOrder();
    for (MarketplaceConsignmentModel marketplaceConsignment : order.getMarketplaceConsignments()) {
      LOG.info(format("Sending cancellation to Mirakl for consignment [%s]", marketplaceConsignment.getCode()));
      marketplaceConsignmentService.cancelMarketplaceConsignment(marketplaceConsignment);
    }
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }
}
