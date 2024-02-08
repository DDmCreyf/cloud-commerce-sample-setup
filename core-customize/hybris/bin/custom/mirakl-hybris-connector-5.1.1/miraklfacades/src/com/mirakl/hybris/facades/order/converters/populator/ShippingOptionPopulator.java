package com.mirakl.hybris.facades.order.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;
import com.mirakl.client.mmp.domain.shipping.MiraklShippingTypeWithConfiguration;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklShippingFeeType;

import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Shipping option populator for {@link DeliveryOrderEntryGroupData}. Sets selected shipping option and available shipping options
 */
public class ShippingOptionPopulator implements Populator<MiraklOrderShippingFee, DeliveryOrderEntryGroupData> {

  private PriceDataFactory priceDataFactory;

  @Override
  public void populate(MiraklOrderShippingFee miraklOrderShippingFee, DeliveryOrderEntryGroupData deliveryOrderEntryGroupData)
      throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOrderShippingFee", miraklOrderShippingFee);
    validateParameterNotNullStandardMessage("deliveryOrderEntryGroupData", deliveryOrderEntryGroupData);

    deliveryOrderEntryGroupData.setShopId(miraklOrderShippingFee.getShopId());
    deliveryOrderEntryGroupData.setShopName(miraklOrderShippingFee.getShopName());
    deliveryOrderEntryGroupData.setLeadTimeToShip(miraklOrderShippingFee.getLeadtimeToShip());

    List<DeliveryModeData> availableShippingOptions = getAvailableShippingOptions(miraklOrderShippingFee);
    deliveryOrderEntryGroupData.setAvailableShippingOptions(availableShippingOptions);
    deliveryOrderEntryGroupData.setSelectedShippingOption(
        getSelectedShippingOption(miraklOrderShippingFee.getSelectedShippingType(), availableShippingOptions));
  }

  protected List<DeliveryModeData> getAvailableShippingOptions(MiraklOrderShippingFee miraklShippingFee) {
    List<DeliveryModeData> shippingOptions = Lists.newArrayList();
    for (MiraklShippingFeeType miraklShippingFeeType : miraklShippingFee.getShippingTypes()) {
      DeliveryModeData deliveryModeData = new DeliveryModeData();
      deliveryModeData.setCode(miraklShippingFeeType.getCode());
      deliveryModeData.setName(miraklShippingFeeType.getLabel());
      deliveryModeData.setDeliveryCost(getShippingPrice(miraklShippingFee, miraklShippingFeeType));

      shippingOptions.add(deliveryModeData);
    }
    return shippingOptions;
  }

  protected PriceData getShippingPrice(MiraklOrderShippingFee miraklShippingFee, MiraklShippingFeeType miraklShippingFeeType) {
    try {
      return priceDataFactory.create(PriceDataType.BUY, miraklShippingFeeType.getTotalShippingPrice(),
          miraklShippingFee.getCurrencyIsoCode().name());
    } catch (AmbiguousIdentifierException | UnknownIdentifierException e) {
      throw new ConversionException(format("Cannot create price data with value [%s] and currency [%s]",
          miraklShippingFeeType.getTotalShippingPrice(), miraklShippingFee.getCurrencyIsoCode()), e);
    }
  }

  private DeliveryModeData getSelectedShippingOption(MiraklShippingTypeWithConfiguration selectedShippingType,
      List<DeliveryModeData> availableShippingOptions) {
    for (DeliveryModeData availableShippingOption : availableShippingOptions) {
      if (selectedShippingType.getCode().equals(availableShippingOption.getCode())) {
        return availableShippingOption;
      }
    }
    return null;
  }

  @Required
  public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }
}
