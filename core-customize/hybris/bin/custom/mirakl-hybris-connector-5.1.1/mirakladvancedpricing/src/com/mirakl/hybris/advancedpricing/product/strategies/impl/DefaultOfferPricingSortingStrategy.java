package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.strategies.OfferPricingSortingStrategy;
import com.mirakl.hybris.beans.OfferPricingData;

public class DefaultOfferPricingSortingStrategy implements OfferPricingSortingStrategy {

  protected List<Comparator<OfferPricingData>> orderedComparators;

  @SuppressWarnings("unchecked")
  @Override
  public List<OfferPricingData> sort(List<OfferPricingData> offerPricings) {
    ComparatorChain comparatorChain = new ComparatorChain();

    for (Comparator<OfferPricingData> comparator : orderedComparators) {
      comparatorChain.addComparator(comparator);
    }

    List<OfferPricingData> sortedCopy = new ArrayList<>(offerPricings);
    Collections.sort(sortedCopy, comparatorChain);

    return sortedCopy;
  }

  @Required
  public void setOrderedComparators(List<Comparator<OfferPricingData>> orderedComparators) {
    this.orderedComparators = orderedComparators;
  }
}
