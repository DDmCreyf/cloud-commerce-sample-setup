package com.mirakl.hybris.core.order.factories;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MiraklGetShippingRatesRequestPopulator {

  /**
   * Populates {@link MiraklGetShippingRatesRequest} in order to add additional informations if needed
   *
   * @param order   the order used to populate
   * @param request the request to be populated
   * @return the populated request
   */
  MiraklGetShippingRatesRequest populate(AbstractOrderModel order, MiraklGetShippingRatesRequest request);
}
