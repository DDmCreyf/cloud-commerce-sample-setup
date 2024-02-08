package com.mirakl.hybris.advancedpricing.comparators;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.mirakl.hybris.advancedpricing.comparators.DefaultOfferPricingComparator.OfferPricingPredicate.CHANNEL;
import static com.mirakl.hybris.advancedpricing.comparators.DefaultOfferPricingComparator.OfferPricingPredicate.CUSTOMER_GROUP;
import static com.mirakl.hybris.advancedpricing.comparators.DefaultOfferPricingComparator.OfferPricingPredicate.CUSTOMER_ORGANIZATION;
import static com.mirakl.hybris.advancedpricing.comparators.DefaultOfferPricingComparator.OfferPricingPredicate.DATE;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mirakl.hybris.beans.OfferPricingData;

public class DefaultOfferPricingComparator implements Comparator<OfferPricingData> {

  @Override
  public int compare(OfferPricingData o1, OfferPricingData o2) {
    for (List<OfferPricingPredicate> predicates : getOrderedSortingPredicates()) {
      int result = verifyPredicates(new OfferPricingWrapper(o1), new OfferPricingWrapper(o2), predicates);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  protected List<List<OfferPricingPredicate>> getOrderedSortingPredicates() {
    List<List<OfferPricingPredicate>> sortingPredicates = new ArrayList<>();
    sortingPredicates.add(asList(CUSTOMER_ORGANIZATION, CHANNEL, DATE));
    sortingPredicates.add(asList(CUSTOMER_ORGANIZATION, CHANNEL));
    sortingPredicates.add(asList(CUSTOMER_GROUP, CHANNEL, DATE));
    sortingPredicates.add(asList(CUSTOMER_GROUP, CHANNEL));
    sortingPredicates.add(asList(CUSTOMER_ORGANIZATION, DATE));
    sortingPredicates.add(asList(CUSTOMER_ORGANIZATION));
    sortingPredicates.add(asList(CUSTOMER_GROUP, DATE));
    sortingPredicates.add(asList(CUSTOMER_GROUP));
    sortingPredicates.add(asList(CHANNEL, DATE));
    sortingPredicates.add(asList(CHANNEL));
    sortingPredicates.add(asList(DATE));

    return sortingPredicates;
  }

  protected int verifyPredicates(OfferPricingWrapper o1, OfferPricingWrapper o2,
      List<OfferPricingPredicate> offerPricingPredicates) {
    if (o1.hasPredicates(offerPricingPredicates) && !o2.hasPredicates(offerPricingPredicates)) {
      return -1;
    }
    if (o2.hasPredicates(offerPricingPredicates) && !o1.hasPredicates(offerPricingPredicates)) {
      return 1;
    }
    return 0;
  }

  protected static class OfferPricingWrapper {

    protected OfferPricingData offerPricing;
    protected Set<OfferPricingPredicate> matchedPredicates;

    protected OfferPricingWrapper(OfferPricingData offerPricing) {
      this.offerPricing = offerPricing;
      this.matchedPredicates = populateMatchedPredicates();
    }

    protected boolean hasPredicates(List<OfferPricingPredicate> predicates) {
      return matchedPredicates.containsAll(predicates);
    }

    private Set<OfferPricingPredicate> populateMatchedPredicates() {
      Set<OfferPricingPredicate> predicateTypes = new HashSet<>();
      if (offerPricing.getChannelCode() != null) {
        predicateTypes.add(OfferPricingPredicate.CHANNEL);
      }
      if (offerPricing.getCustomerOrganizationId() != null) {
        predicateTypes.add(OfferPricingPredicate.CUSTOMER_ORGANIZATION);
      }
      if (offerPricing.getCustomerGroupId() != null) {
        predicateTypes.add(OfferPricingPredicate.CUSTOMER_GROUP);
      }
      if (offerPricing.getStartDate() != null || offerPricing.getEndDate() != null) {
        predicateTypes.add(OfferPricingPredicate.DATE);
      }
      return copyOf(predicateTypes);
    }
  }

  protected enum OfferPricingPredicate {
    CHANNEL, DATE, CUSTOMER_ORGANIZATION, CUSTOMER_GROUP
  }

}
