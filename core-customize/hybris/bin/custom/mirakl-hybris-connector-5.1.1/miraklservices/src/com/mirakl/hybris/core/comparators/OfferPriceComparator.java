package com.mirakl.hybris.core.comparators;

import java.util.Comparator;

import com.mirakl.hybris.beans.ComparableOfferData;

public class OfferPriceComparator<T> implements Comparator<ComparableOfferData<T>> {

  @Override
  public int compare(ComparableOfferData<T> offer1, ComparableOfferData<T> offer2) {
    if (offer1.getTotalPrice() == null) {
      return 1;
    }
    if (offer2.getTotalPrice() == null) {
      return -1;
    }
    return offer1.getTotalPrice().compareTo(offer2.getTotalPrice());
  }
}
