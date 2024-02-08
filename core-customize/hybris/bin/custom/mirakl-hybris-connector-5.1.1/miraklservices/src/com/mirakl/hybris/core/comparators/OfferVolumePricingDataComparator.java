package com.mirakl.hybris.core.comparators;

import com.mirakl.hybris.beans.OfferVolumePricingData;

import de.hybris.platform.commerceservices.util.AbstractComparator;

public class OfferVolumePricingDataComparator extends AbstractComparator<OfferVolumePricingData> {

  public static final OfferVolumePricingDataComparator INSTANCE = new OfferVolumePricingDataComparator();

  @Override
  protected int compareInstances(final OfferVolumePricingData price1, final OfferVolumePricingData price2) {
    if (price1 == null || price1.getQuantityThreshold() == null) {
      return BEFORE;
    }
    if (price2 == null || price2.getQuantityThreshold() == null) {
      return AFTER;
    }

    return compareValues(price1.getQuantityThreshold().longValue(), price2.getQuantityThreshold().longValue());
  }

}
