package com.mirakl.hybris.core.util.feature;

import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;

import java.util.function.Function;

public enum MiraklPriceFeature {

  ADVANCED_PRICING(priceEntry -> priceEntry.isAdvanced()),

  CHANNEL_PRICING(priceEntry -> priceEntry.isChannelSpecific());

  private final Function<MiraklAsyncOfferPriceDecorator, Boolean> isRequired;

  MiraklPriceFeature(Function<MiraklAsyncOfferPriceDecorator, Boolean> isRequired) {
    this.isRequired = isRequired;
  }

  public boolean isRequired(MiraklAsyncOfferPriceDecorator priceEntry) {
    return isRequired.apply(priceEntry);
  }
}
