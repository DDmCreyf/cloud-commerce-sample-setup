package com.mirakl.hybris.advancedpricing.comparators;

import java.util.Comparator;

import com.mirakl.hybris.beans.OfferPricingData;

public class DefaultOfferPricingPriceComparator implements Comparator<OfferPricingData> {

  @Override
  public int compare(OfferPricingData o1, OfferPricingData o2) {
    return o1.getPrice().compareTo(o2.getPrice());
  }

}
