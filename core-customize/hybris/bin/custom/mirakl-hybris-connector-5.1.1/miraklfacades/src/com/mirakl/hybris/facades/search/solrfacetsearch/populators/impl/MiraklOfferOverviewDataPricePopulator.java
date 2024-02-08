package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;
import com.mirakl.hybris.facades.product.helpers.PriceDataFactoryHelper;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklOfferOverviewDataPricePopulator implements Populator<OfferModel, OfferOverviewData> {

  protected OfferService offerService;
  protected OfferPricingSelectionStrategy offerPricingSelectionStrategy;
  protected PriceDataFactoryHelper priceDataFactoryHelper;

  @Override
  public void populate(OfferModel source, OfferOverviewData target) throws ConversionException {
    List<OfferPricingData> offerPricingsData = getOfferPricingsData(source);
    if (isEmpty(offerPricingsData)) {
      return;
    }
    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offerPricingsData);

    if (offerPricing.getPrice() != null) {
      target.setPrice(priceDataFactoryHelper.createPrice(offerPricing.getPrice()));
    }
    if (offerPricing.getUnitOriginPrice() != null) {
      target.setOriginPrice(priceDataFactoryHelper.createPrice(offerPricing.getUnitOriginPrice()));
    }
    if (source.getMinShippingPrice() != null) {
      target.setMinShippingPrice(priceDataFactoryHelper.createPrice(source.getMinShippingPrice()));
    }

    BigDecimal totalPrice = getTotalPrice(source, offerPricing);
    if (totalPrice != null) {
      target.setTotalPrice(priceDataFactoryHelper.createPrice(totalPrice));
    }

    target.setAllOfferPricingsJSON(getAllOfferPricingsJSON(source, offerPricingsData));
  }

  protected List<OfferPricingData> getOfferPricingsData(OfferModel source) {
    return offerService.loadAllOfferPricings(source);
  }

  protected String getAllOfferPricingsJSON(OfferModel offer, List<OfferPricingData> offerPricingsData) {
    return offer.getAllOfferPricingsJSON();
  }

  protected BigDecimal getTotalPrice(OfferModel offer, OfferPricingData offerPricing) {
    BigDecimal price = offerPricing.getPrice();
    if (price == null) {
      return null;
    }
    return offer.getMinShippingPrice() != null ? price.add(offer.getMinShippingPrice()) : price;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setOfferPricingSelectionStrategy(OfferPricingSelectionStrategy offerPricingSelectionStrategy) {
    this.offerPricingSelectionStrategy = offerPricingSelectionStrategy;
  }

  @Required
  public void setPriceDataFactoryHelper(PriceDataFactoryHelper priceDataFactoryHelper) {
    this.priceDataFactoryHelper = priceDataFactoryHelper;
  }

}
