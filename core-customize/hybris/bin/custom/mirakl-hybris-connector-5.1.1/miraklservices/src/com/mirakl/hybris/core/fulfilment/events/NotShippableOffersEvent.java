package com.mirakl.hybris.core.fulfilment.events;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event published in case an order contains entries not shippable
 */
public class NotShippableOffersEvent extends AbstractEvent {

  private static final long serialVersionUID = 4344276532412378237L;

  protected List<MiraklOfferNotShippable> notShippableOffers;

  protected OrderModel order;

  public NotShippableOffersEvent(List<MiraklOfferNotShippable> notShippableOffers, OrderModel order) {
    super();
    validateParameterNotNullStandardMessage("order", order);
    validateParameterNotNullStandardMessage("notShippableOffers", notShippableOffers);
    this.notShippableOffers = notShippableOffers;
    this.order = order;
  }

  public List<MiraklOfferNotShippable> getNotShippableOffers() {
    return notShippableOffers;
  }

  public OrderModel getOrder() {
    return order;
  }


}
