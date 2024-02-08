package com.mirakl.hybris.core.comparators;

import java.util.Comparator;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.enums.OfferState;

public class OfferStateComparator<T> implements Comparator<ComparableOfferData<T>> {

  private OfferState priorityOfferState;

  public OfferStateComparator(OfferState priorityOfferState) {
    this.priorityOfferState = priorityOfferState;
  }

  @Override
  public int compare(ComparableOfferData<T> offer1, ComparableOfferData<T> offer2) {
    if (priorityOfferState.equals(offer1.getState()) && !offer1.getState().equals(offer2.getState())) {
      return -1;
    }
    if (priorityOfferState.equals(offer2.getState()) && !offer1.getState().equals(offer2.getState())) {
      return 1;
    }
    return 0;
  }


}
