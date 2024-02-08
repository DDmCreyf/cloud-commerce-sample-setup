package com.mirakl.hybris.channels.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;

public class DefaultChannelOfferPricingSelectionStrategy implements OfferPricingSelectionStrategy {
  protected OfferService offerService;
  protected MiraklChannelService miraklChannelService;

  @Override
  public OfferPricingData selectApplicableOfferPricing(OfferModel offer) {
    return selectApplicableOfferPricing(offerService.loadAllOfferPricings(offer));
  }

  @Override
  public OfferPricingData selectApplicableOfferPricing(List<OfferPricingData> offerPricings) {
    if (isEmpty(offerPricings)) {
      return null;
    }
    final MiraklChannelModel channel = miraklChannelService.getCurrentMiraklChannel();
    return getChannelPricing(offerPricings, channel);
  }

  protected OfferPricingData getChannelPricing(List<OfferPricingData> offerPricings, MiraklChannelModel channel) {
    OfferPricingData defaultPricing = null;

    for (OfferPricingData offerPricing : offerPricings) {
      if (offerPricing.getChannelCode() == null) {
        defaultPricing = offerPricing;
      } else if (channel != null && channel.getCode().equals(offerPricing.getChannelCode())) {
        return offerPricing;
      }
    }

    return defaultPricing;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
