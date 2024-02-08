package com.mirakl.hybris.channels.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrder;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCreateOrderChannelPopulator implements Populator<OrderModel, MiraklCreateOrder> {

  protected ShippingFeeService shippingFeeService;


  @Override
  public void populate(OrderModel orderModel, MiraklCreateOrder miraklCreateOrder) throws ConversionException {
    validateParameterNotNullStandardMessage("orderModel", orderModel);
    validateParameterNotNullStandardMessage("miraklCreateOrder", miraklCreateOrder);

    miraklCreateOrder.setChannelCode(getChannelCode(orderModel));
  }

  protected String getChannelCode(OrderModel orderModel) {
    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFees(orderModel);

    if (shippingFees != null && isNotEmpty(shippingFees.getOrders())
        && isNotBlank(shippingFees.getOrders().get(0).getChannelCode())) {
      return shippingFees.getOrders().get(0).getChannelCode();
    }

    return null;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }
}
