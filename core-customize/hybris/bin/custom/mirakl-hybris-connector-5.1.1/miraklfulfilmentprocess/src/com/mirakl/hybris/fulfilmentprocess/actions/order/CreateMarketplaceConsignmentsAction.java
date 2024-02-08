package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static java.lang.String.format;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

public class CreateMarketplaceConsignmentsAction extends AbstractProceduralAction<OrderProcessModel> {

  private static final Logger LOG = Logger.getLogger(CreateMarketplaceConsignmentsAction.class);

  protected MiraklOrderService miraklOrderService;

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public void executeAction(OrderProcessModel orderProcessModel) throws RetryLaterException {
    final OrderModel order = orderProcessModel.getOrder();
    MiraklCreatedOrders miraklCreatedOrders = miraklOrderService.loadCreatedOrders(order);
    if (miraklCreatedOrders == null) {
      LOG.info(format("No marketplace orders stored within order [%s]. Skipping marketplace consignments creation..",
          order.getCode()));
      return;
    }

    Set<MarketplaceConsignmentModel> consignments =
        marketplaceConsignmentService.createMarketplaceConsignments(order, miraklCreatedOrders);

    LOG.info(format("%s marketplace consignments successfully created for order [%s]", consignments.size(), order.getCode()));
  }

  @Required
  public void setMiraklOrderService(MiraklOrderService miraklOrderService) {
    this.miraklOrderService = miraklOrderService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }
}
