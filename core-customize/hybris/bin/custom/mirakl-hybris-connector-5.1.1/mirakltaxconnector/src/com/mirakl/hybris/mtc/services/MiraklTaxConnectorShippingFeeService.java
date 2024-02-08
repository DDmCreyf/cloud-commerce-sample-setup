package com.mirakl.hybris.mtc.services;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MiraklTaxConnectorShippingFeeService extends ShippingFeeService {

  /**
   * Retrieves the shipping fees with taxes for the marketplace entries of an order.
   *
   * @param order the order for which the shipping fees are retrieved
   * @return {@link MiraklOrderShippingFeeOffer} if the taxes where computed successfully, null otherwise
   * @throws MiraklApiException if the address could not be converter or the calculation has errors
   */
  MiraklOrderShippingFees getShippingFeesWithTaxes(AbstractOrderModel order);
}
