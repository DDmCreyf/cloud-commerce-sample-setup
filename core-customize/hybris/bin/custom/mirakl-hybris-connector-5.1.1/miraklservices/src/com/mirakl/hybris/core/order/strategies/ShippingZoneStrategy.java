package com.mirakl.hybris.core.order.strategies;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Strategy to extract shipping zone from {@link AbstractOrderModel}
 */
public interface ShippingZoneStrategy {

  /**
   * Returns shipping zone code from {@link AbstractOrderModel}
   *
   * @param order {@link AbstractOrderModel}
   * @return shipping zone code
   */
  String getShippingZoneCode(AbstractOrderModel order);

  /**
   * Returns the estimated shipping zone code from {@link CartModel}
   * The default implementation uses the address if defined, otherwise it falls back to the base store default shipping zone code
   *
   * @param cart {@link CartModel}
   * @return shipping zone code
   */
  String getEstimatedShippingZoneCode(CartModel cart);
}
