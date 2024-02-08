package com.mirakl.hybris.mtc.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingOptionsService;
import com.mirakl.hybris.core.order.services.impl.DefaultShippingOptionsService;
import com.mirakl.hybris.mtc.services.MiraklTaxConnectorShippingFeeService;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public class DefaultMiraklTaxConnectorShippingOptionsService extends DefaultShippingOptionsService
    implements ShippingOptionsService {

  protected MiraklTaxConnectorShippingFeeService shippingFeeService;

  @Override
  public void setShippingOptions(AbstractOrderModel order) {
    validateParameterNotNull(order, "AbstractOrder cannot be null for the shipping rates request");
    setShippingFeesForOrder(order, shippingFeeService.getShippingFeesWithTaxes(order));
  }

  // Needs to be defined here for testing purpose
  @Override
  protected void setShippingFeesForOrder(AbstractOrderModel order, MiraklOrderShippingFees shippingFees) {
    super.setShippingFeesForOrder(order, shippingFees);
  }

  @Required
  public void setMiraklTaxConnectorShippingFeeService(MiraklTaxConnectorShippingFeeService shippingFeeService) {
    super.setShippingFeeService(shippingFeeService);
    this.shippingFeeService = shippingFeeService;
  }
}
