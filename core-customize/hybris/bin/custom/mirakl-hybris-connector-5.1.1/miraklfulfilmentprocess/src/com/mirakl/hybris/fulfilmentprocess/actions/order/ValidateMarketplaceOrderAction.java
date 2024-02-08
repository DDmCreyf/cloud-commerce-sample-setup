package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;

public class ValidateMarketplaceOrderAction extends AbstractProceduralAction<OrderProcessModel> {

  protected MiraklOrderService miraklOrderService;

  @Override
  public void executeAction(OrderProcessModel orderProcess) throws RetryLaterException, Exception {
    if (isNotEmpty(orderProcess.getOrder().getMarketplaceConsignments())) {
      miraklOrderService.validateOrder(orderProcess.getOrder());
    }
  }

  @Required
  public void setMiraklOrderService(MiraklOrderService miraklOrderService) {
    this.miraklOrderService = miraklOrderService;
  }

}
