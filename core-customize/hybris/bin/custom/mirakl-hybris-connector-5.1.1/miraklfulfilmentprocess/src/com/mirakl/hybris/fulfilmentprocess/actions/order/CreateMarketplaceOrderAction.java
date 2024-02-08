package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;

public class CreateMarketplaceOrderAction extends AbstractProceduralAction<OrderProcessModel> {

  private static final Logger LOG = Logger.getLogger(CreateMarketplaceOrderAction.class);

  protected MiraklOrderService miraklOrderService;

  protected EventService eventService;

  @Override
  public void executeAction(OrderProcessModel orderProcessModel) throws RetryLaterException {
    final OrderModel order = orderProcessModel.getOrder();

    if (isEmpty(order.getMarketplaceEntries())) {
      LOG.info(format("No marketplace entries within order [%s]. Skipping call to OR01..", order.getCode()));
      return;
    }

    MiraklCreatedOrders marketplaceOrders = miraklOrderService.createMarketplaceOrders(order);
    handleNotShippableOffers(marketplaceOrders, order);
  }

  protected void handleNotShippableOffers(MiraklCreatedOrders marketplaceOrders, OrderModel orderModel) {
    List<MiraklOfferNotShippable> notShippableOffers = marketplaceOrders.getOffersNotShippable();
    if (CollectionUtils.isNotEmpty(notShippableOffers)) {
      eventService.publishEvent(new NotShippableOffersEvent(notShippableOffers, orderModel));
    }
  }

  @Required
  public void setMiraklOrderService(MiraklOrderService miraklOrderService) {
    this.miraklOrderService = miraklOrderService;
  }

  @Required
  public void setEventService(EventService eventService) {
    this.eventService = eventService;
  }
}
