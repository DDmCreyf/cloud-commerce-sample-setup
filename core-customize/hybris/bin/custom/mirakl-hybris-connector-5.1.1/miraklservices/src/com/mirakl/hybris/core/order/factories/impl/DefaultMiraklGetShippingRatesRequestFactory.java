package com.mirakl.hybris.core.order.factories.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.client.mmp.front.request.shipping.MiraklOfferQuantityShippingTypeTuple;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestFactory;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestPopulator;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public class DefaultMiraklGetShippingRatesRequestFactory implements MiraklGetShippingRatesRequestFactory {

  protected List<MiraklGetShippingRatesRequestPopulator> requestPopulators;

  @Override
  public MiraklGetShippingRatesRequest createShippingRatesRequest(AbstractOrderModel order, List<MiraklOfferQuantityShippingTypeTuple> offerTuples,
      String shippingZoneCode) {
    MiraklGetShippingRatesRequest shippingRatesRequest = new MiraklGetShippingRatesRequest(offerTuples, shippingZoneCode);

    if (isNotEmpty(requestPopulators)) {
      for (MiraklGetShippingRatesRequestPopulator populator : requestPopulators) {
        populator.populate(order, shippingRatesRequest);
      }
    }

    return shippingRatesRequest;
  }

  @Required
  public void setRequestPopulators(List<MiraklGetShippingRatesRequestPopulator> requestPopulators) {
    this.requestPopulators = requestPopulators;
  }

}
