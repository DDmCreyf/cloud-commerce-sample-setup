package com.mirakl.hybris.core.order.factories;

import java.util.List;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.client.mmp.front.request.shipping.MiraklOfferQuantityShippingTypeTuple;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MiraklGetShippingRatesRequestFactory {

  /**
   * Creates a shipping rates request (used for the SH02 call)
   *
   * @param order
   * @param offerTuples
   * @param shippingZoneCode
   * @return shipping rates request
   */
  MiraklGetShippingRatesRequest createShippingRatesRequest(AbstractOrderModel order, List<MiraklOfferQuantityShippingTypeTuple> offerTuples, String shippingZoneCode);
}
