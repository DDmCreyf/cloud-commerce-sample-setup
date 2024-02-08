package com.mirakl.hybris.mtc.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.request.shipping.MiraklCustomerShippingToAddress;
import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestPopulator;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklTaxConnectorGetShippingRatesRequestPopulator implements MiraklGetShippingRatesRequestPopulator {
  protected MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  protected Converter<AddressModel, MiraklCustomerShippingToAddress> miraklCustomerShippingToAddressConverter;

  @Override
  public MiraklGetShippingRatesRequest populate(AbstractOrderModel order, MiraklGetShippingRatesRequest request) {
    if (miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)) {
      request.setComputeOrderTaxes(true);
      AddressModel deliveryAddress = getAddressModelFromOrder(order);
      request.setCustomerShippingToAddress(miraklCustomerShippingToAddressConverter.convert(deliveryAddress));
    }
    return request;
  }

  protected AddressModel getAddressModelFromOrder(AbstractOrderModel order) {
    AddressModel deliveryAddress = order.getDeliveryAddress();
    if (deliveryAddress == null || deliveryAddress.getCountry() == null) {
      return null;
    }
    return deliveryAddress;
  }

  @Required
  public void setMiraklTaxConnectorActivationStrategy(MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy) {
    this.miraklTaxConnectorActivationStrategy = miraklTaxConnectorActivationStrategy;
  }

  @Required
  public void setMiraklCustomerShippingToAddressConverter(Converter<AddressModel, MiraklCustomerShippingToAddress> miraklCustomerShippingToAddressConverter) {
    this.miraklCustomerShippingToAddressConverter = miraklCustomerShippingToAddressConverter;
  }
}
