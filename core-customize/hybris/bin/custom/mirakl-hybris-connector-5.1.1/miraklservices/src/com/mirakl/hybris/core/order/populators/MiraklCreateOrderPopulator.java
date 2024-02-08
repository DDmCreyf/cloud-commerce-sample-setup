package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrderCustomer;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrder;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderOffer;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderPaymentInfo;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklCreateOrderPopulator implements Populator<OrderModel, MiraklCreateOrder> {

  protected ShippingZoneStrategy shippingZoneStrategy;
  protected Converter<PaymentInfoModel, MiraklCreateOrderPaymentInfo> miraklCreateOrderPaymentInfoConverter;
  protected Converter<OrderModel, MiraklOrderCustomer> miraklOrderCustomerConverter;
  protected Converter<AbstractOrderEntryModel, MiraklCreateOrderOffer> miraklCreateOrderOfferConverter;
  protected boolean scoringAlreadyDone;


  @Override
  public void populate(OrderModel orderModel, MiraklCreateOrder miraklCreateOrder) throws ConversionException {
    validateParameterNotNullStandardMessage("orderModel", orderModel);
    validateParameterNotNullStandardMessage("miraklCreateOrder", miraklCreateOrder);

    miraklCreateOrder.setCommercialId(orderModel.getCode());
    miraklCreateOrder.setScored(scoringAlreadyDone);
    miraklCreateOrder.setCustomer(miraklOrderCustomerConverter.convert(orderModel));
    miraklCreateOrder.setPaymentInfo(miraklCreateOrderPaymentInfoConverter.convert(orderModel.getPaymentInfo()));
    miraklCreateOrder.setShippingZoneCode(shippingZoneStrategy.getShippingZoneCode(orderModel));
    miraklCreateOrder.setOffers(miraklCreateOrderOfferConverter.convertAll(orderModel.getMarketplaceEntries()));
  }

  @Required
  public void setShippingZoneStrategy(ShippingZoneStrategy shippingZoneStrategy) {
    this.shippingZoneStrategy = shippingZoneStrategy;
  }

  @Required
  public void setMiraklCreateOrderPaymentInfoConverter(
      Converter<PaymentInfoModel, MiraklCreateOrderPaymentInfo> miraklCreateOrderPaymentInfoConverter) {
    this.miraklCreateOrderPaymentInfoConverter = miraklCreateOrderPaymentInfoConverter;
  }

  @Required
  public void setMiraklOrderCustomerConverter(Converter<OrderModel, MiraklOrderCustomer> miraklOrderCustomerConverter) {
    this.miraklOrderCustomerConverter = miraklOrderCustomerConverter;
  }

  @Required
  public void setMiraklCreateOrderOfferConverter(
      Converter<AbstractOrderEntryModel, MiraklCreateOrderOffer> miraklCreateOrderOfferConverter) {
    this.miraklCreateOrderOfferConverter = miraklCreateOrderOfferConverter;
  }

  @Required
  public void setScoringAlreadyDone(boolean scoringAlreadyDone) {
    this.scoringAlreadyDone = scoringAlreadyDone;
  }
}
