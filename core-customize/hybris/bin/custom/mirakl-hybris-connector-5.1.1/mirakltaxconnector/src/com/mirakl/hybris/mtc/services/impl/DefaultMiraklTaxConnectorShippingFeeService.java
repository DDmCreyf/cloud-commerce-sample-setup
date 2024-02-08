package com.mirakl.hybris.mtc.services.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.client.mmp.front.request.shipping.MiraklOfferQuantityShippingTypeTuple;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestFactory;
import com.mirakl.hybris.core.order.services.impl.DefaultShippingFeeService;
import com.mirakl.hybris.mtc.services.MiraklTaxConnectorShippingFeeService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMiraklTaxConnectorShippingFeeService extends DefaultShippingFeeService
    implements MiraklTaxConnectorShippingFeeService {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklTaxConnectorShippingFeeService.class);
  protected MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  protected MiraklGetShippingRatesRequestFactory miraklGetShippingRatesRequestFactory;

  @Override
  public MiraklOrderShippingFees getShippingFees(AbstractOrderModel order) {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order) || order.getDeliveryAddress() == null) {
      return super.getShippingFees(order);
    }
    return getShippingFeesWithTaxes(order);
  }

  @Override
  public MiraklOrderShippingFees getShippingFees(AbstractOrderModel order, String shippingZoneCode) {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order) || order.getDeliveryAddress() == null) {
      return super.getShippingFees(order, shippingZoneCode);
    }
    return getShippingFeesWithTaxes(order);
  }

  @Override
  public MiraklOrderShippingFees getShippingFeesWithTaxes(AbstractOrderModel order) {
    if (order == null || !miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)
        || order.getDeliveryAddress() == null) {
      return super.getShippingFees(order);
    }
    List<MiraklOfferQuantityShippingTypeTuple> offerTuples = getOfferTuples(order);
    AddressModel deliveryAddress = getAddressModelFromOrder(order);
    if (deliveryAddress == null || isEmpty(offerTuples)) {
      return null;
    }
    return getMiraklOrderShippingFees(order, offerTuples, deliveryAddress);
  }

  protected MiraklOrderShippingFees getMiraklOrderShippingFees(AbstractOrderModel order,
      List<MiraklOfferQuantityShippingTypeTuple> offerTuples, AddressModel deliveryAddress) {
    try {
      MiraklGetShippingRatesRequest request = miraklGetShippingRatesRequestFactory.createShippingRatesRequest(order, offerTuples,
          deliveryAddress.getCountry().getIsocode());
      return miraklOperatorApi.getShippingRates(request);
    } catch (MiraklApiException miraklApiException) {
      String message = format("Unable to calculate shipping rate for order [%s] with shipping zone code [%s]", order.getCode(),
          deliveryAddress.getCountry().getIsocode());
      LOG.info(message, miraklApiException);
      throw toMiraklApiException(message);
    } catch (ConversionException | IllegalArgumentException exception) {
      String message = format("Unable to convert the order address for order [%s] with shipping zone code [%s]", order.getCode(),
          deliveryAddress.getCountry().getIsocode());
      LOG.info(message, exception);
      throw toMiraklApiException(message);
    }
  }

  private static MiraklApiException toMiraklApiException(String message) {
    MiraklErrorResponseBean miraklErrorResponseBean = new MiraklErrorResponseBean();
    miraklErrorResponseBean.setMessage(message);
    return new MiraklApiException(miraklErrorResponseBean);
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
  public void setMiraklGetShippingRatesRequestFactory(MiraklGetShippingRatesRequestFactory miraklGetShippingRatesRequestFactory) {
    this.miraklGetShippingRatesRequestFactory = miraklGetShippingRatesRequestFactory;
  }
}
