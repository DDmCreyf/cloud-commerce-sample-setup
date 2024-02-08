package com.mirakl.hybris.core.order.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

public class MiraklCreateOrderOfferPopulator implements Populator<AbstractOrderEntryModel, MiraklCreateOrderOffer> {

  protected OfferService offerService;
  protected ShippingFeeService shippingFeeService;
  protected Converter<TaxValue, MiraklOrderTaxAmount> miraklOrderTaxAmountConverter;

  @Override
  public void populate(AbstractOrderEntryModel orderEntry, MiraklCreateOrderOffer miraklCreateOrderOffer)
      throws ConversionException {
    checkNotNull(orderEntry);
    checkNotNull(miraklCreateOrderOffer);

    String offerId = orderEntry.getOfferId();
    checkNotNull(offerId);

    AbstractOrderModel order = orderEntry.getOrder();
    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFees(order);
    checkNotNull(shippingFees);

    Optional<MiraklOrderShippingFeeOffer> shippingFeeOffer = shippingFeeService.extractShippingFeeOffer(offerId, shippingFees);
    if (!shippingFeeOffer.isPresent()) {
      throw new IllegalStateException(
          format("Unable to find offer [%s] within SH02 saved payload for order [%s]", offerId, order.getCode()));
    }

    Optional<MiraklOrderShippingFee> orderShippingFee = shippingFeeService.extractOrderShippingFeeForOffer(offerId, shippingFees);
    if (!orderShippingFee.isPresent()) {
      throw new IllegalStateException(format(
          "No logistic order containing offer [%s] found within SH02 saved payload for order [%s]", offerId, order.getCode()));
    }

    populateOfferDetails(miraklCreateOrderOffer, shippingFeeOffer.get(), orderShippingFee.get());
    if (isTrue(order.getNet())) {
      populateTaxes(miraklCreateOrderOffer, orderEntry, orderShippingFee.get());
    }
  }

  protected void populateOfferDetails(MiraklCreateOrderOffer miraklCreateOrderOffer, MiraklOrderShippingFeeOffer shippingFeeOffer,
      MiraklOrderShippingFee orderShippingFee) {
    miraklCreateOrderOffer.setId(shippingFeeOffer.getId());
    miraklCreateOrderOffer.setQuantity(shippingFeeOffer.getLineQuantity());
    miraklCreateOrderOffer.setPriceUnit(shippingFeeOffer.getPrice());
    miraklCreateOrderOffer.setPrice(shippingFeeOffer.getLinePrice());
    miraklCreateOrderOffer.setCurrencyIsoCode(orderShippingFee.getCurrencyIsoCode());
    miraklCreateOrderOffer.setLeadtimeToShip(orderShippingFee.getLeadtimeToShip());
    miraklCreateOrderOffer.setShippingPrice(shippingFeeOffer.getLineShippingPrice());
    miraklCreateOrderOffer.setShippingTypeCode(orderShippingFee.getSelectedShippingType().getCode());
  }

  protected void populateTaxes(MiraklCreateOrderOffer miraklCreateOrderOffer, AbstractOrderEntryModel orderEntry) {
    miraklCreateOrderOffer.setTaxes(miraklOrderTaxAmountConverter.convertAll(orderEntry.getTaxValues()));
  }

  protected void populateTaxes(MiraklCreateOrderOffer miraklCreateOrderOffer, AbstractOrderEntryModel marketplaceEntry,
      MiraklOrderShippingFee shippingFees) {
    populateTaxes(miraklCreateOrderOffer, marketplaceEntry);
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMiraklOrderTaxAmountConverter(Converter<TaxValue, MiraklOrderTaxAmount> miraklOrderTaxAmountConverter) {
    this.miraklOrderTaxAmountConverter = miraklOrderTaxAmountConverter;
  }
}
