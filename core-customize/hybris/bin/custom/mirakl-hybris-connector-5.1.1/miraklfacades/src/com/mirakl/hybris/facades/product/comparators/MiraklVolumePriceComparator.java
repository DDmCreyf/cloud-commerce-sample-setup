package com.mirakl.hybris.facades.product.comparators;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.util.AbstractComparator;

public class MiraklVolumePriceComparator extends AbstractComparator<PriceData> {

  @Override
  protected int compareInstances(final PriceData price1, final PriceData price2) {
    if (price1 == null || price1.getMinQuantity() == null) {
      return BEFORE;
    }
    if (price2 == null || price2.getMinQuantity() == null) {
      return AFTER;
    }

    return compareValues(price1.getMinQuantity().longValue(), price2.getMinQuantity().longValue());
  }
}
