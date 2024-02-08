package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklRealtimeOfferService;
import com.mirakl.hybris.core.product.services.MiraklRealtimePriceService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

/**
 * Price service usable on Offer data, typed as MiraklOfferOnProduct, coming from the Mirakl API (ie, not coming from SAP CC)
 */
public class DefaultMiraklRealtimePriceService implements MiraklRealtimePriceService {

  protected MiraklRealtimeOfferService realtimeOfferService;
  protected MiraklPriceService priceService;
  protected Converter<MiraklOfferPricing, OfferPricingData> offerPricingDataConverter;
  protected JsonMarshallingService jsonMarshallingService;
  protected ModelService modelService;

  @Override
  public PriceValue getPriceValue(final AbstractOrderEntryModel entry) {
    return createPriceValue(offerPricingDataConverter.convert(getRealtimeOfferPricing(entry)), entry.getQuantity(),
        entry.getOrder().getCurrency().getIsocode(), entry.getOrder().getNet());
  }

  /**
   * @param offerOnProduct data from an Offer retrieved straight from the Mirakl API
   * @return the sum of offer price and the min shipping price if non-null
   */
  @Override
  public BigDecimal getOfferTotalPrice(final MiraklOfferOnProduct offerOnProduct) {
    BigDecimal offerTotalPrice;
    BigDecimal minShippingPrice = offerOnProduct.getMinShipping() == null ? null : offerOnProduct.getMinShipping().getPrice();

    if (minShippingPrice != null) {
      offerTotalPrice = minShippingPrice.add(offerOnProduct.getPrice());
    } else {
      offerTotalPrice = offerOnProduct.getPrice();
    }
    return offerTotalPrice;
  }

  @Override
  public MiraklOfferPricing getRealtimeOfferPricing(final AbstractOrderEntryModel entry) {
    return realtimeOfferService.getOffer(entry.getOfferId()).getApplicablePricing();
  }

  protected PriceValue createPriceValue(final OfferPricingData offerPricingData, final Long quantity, final String currencyIsocode,
      final Boolean net) {
    final OfferVolumePricingData volumePriceForQuantity = priceService.getVolumePriceForQuantity(offerPricingData, quantity);
    return volumePriceForQuantity != null ? new PriceValue(currencyIsocode, volumePriceForQuantity.getPrice().doubleValue(), net) : null;
  }

  @Required
  public void setRealtimeOfferService(MiraklRealtimeOfferService realtimeOfferService) {
    this.realtimeOfferService = realtimeOfferService;
  }
  @Required
  public void setOfferPricingDataConverter(final Converter<MiraklOfferPricing, OfferPricingData> offerPricingDataConverter) {
    this.offerPricingDataConverter = offerPricingDataConverter;
  }

  @Required
  public void setPriceService(final MiraklPriceService priceService) {
    this.priceService = priceService;
  }

  @Required
  public void setJsonMarshallingService(final JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setModelService(final ModelService modelService) {
    this.modelService = modelService;
  }
}
