package com.mirakl.hybris.facades.order.converters;

import static com.mirakl.client.core.internal.util.Preconditions.checkNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeErrorEnum;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Converter creating {@link ShippingOfferDiscrepancyData} with insufficient stock entries, invalid shipping type/zone or missing
 * offer for marketplace entries found in the {@link AbstractOrderModel}
 */
public class ShippingOfferDiscrepancyConverter
    extends AbstractPopulatingConverter<AbstractOrderEntryModel, ShippingOfferDiscrepancyData> {

  protected static final String INSUFFICIENT_QUANTITY_MESSAGE = "shipping.discrepancy.offer.insufficientQuantity";

  protected ShippingFeeService shippingFeeService;
  protected Map<MiraklOrderShippingFeeErrorEnum, String> errorCodes;
  protected Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

  @Override
  public ShippingOfferDiscrepancyData convert(AbstractOrderEntryModel source) throws ConversionException {
    String offerId = source.getOfferId();
    if (offerId == null) {
      return null;
    }
    AbstractOrderModel order = source.getOrder();
    checkNotNull(order);
    MiraklOrderShippingFees miraklOrderShippingFees = shippingFeeService.getStoredShippingFees(order);
    if (miraklOrderShippingFees == null) {
      return null;
    }

    Optional<MiraklOrderShippingFeeError> feeError = shippingFeeService.extractShippingFeeError(offerId, miraklOrderShippingFees);
    if (!feeError.isPresent()) {
      Optional<MiraklOrderShippingFeeOffer> feeOffer =
          shippingFeeService.extractShippingFeeOffer(offerId, miraklOrderShippingFees);
      return feeOffer.isPresent() ? getMissingQuantityData(feeOffer.get(), source) : null;
    } else {
      return getOfferErrorData(feeError.get(), source);
    }
  }

  protected ShippingOfferDiscrepancyData getMissingQuantityData(MiraklOrderShippingFeeOffer feeOffer,
      AbstractOrderEntryModel source) {
    long missingQuantity = getMissingQuantity(feeOffer, source);
    if (missingQuantity > 0) {
      ShippingOfferDiscrepancyData offerDiscrepancy = new ShippingOfferDiscrepancyData();
      offerDiscrepancy.setEntry(orderEntryConverter.convert(source));
      offerDiscrepancy.setMessage(INSUFFICIENT_QUANTITY_MESSAGE);
      offerDiscrepancy.setMissingQuantity(missingQuantity);

      return offerDiscrepancy;
    }
    return null;
  }

  protected long getMissingQuantity(MiraklOrderShippingFeeOffer feeOffer, AbstractOrderEntryModel source) {
    return source.getQuantity() - feeOffer.getQuantity().longValue();
  }

  protected ShippingOfferDiscrepancyData getOfferErrorData(MiraklOrderShippingFeeError feeError, AbstractOrderEntryModel source) {
    ShippingOfferDiscrepancyData offerDiscrepancy = new ShippingOfferDiscrepancyData();

    offerDiscrepancy.setEntry(orderEntryConverter.convert(source));
    offerDiscrepancy.setMissingQuantity(source.getQuantity());
    offerDiscrepancy.setMessage(errorCodes.get(feeError.getErrorCode()));

    return offerDiscrepancy;
  }

  @Required
  public void setErrorCodes(Map<MiraklOrderShippingFeeErrorEnum, String> errorCodes) {
    this.errorCodes = errorCodes;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setOrderEntryConverter(Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter) {
    this.orderEntryConverter = orderEntryConverter;
  }
}
