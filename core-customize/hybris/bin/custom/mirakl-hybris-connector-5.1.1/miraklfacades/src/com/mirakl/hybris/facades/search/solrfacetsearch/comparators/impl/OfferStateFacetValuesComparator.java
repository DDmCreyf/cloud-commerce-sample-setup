package com.mirakl.hybris.facades.search.solrfacetsearch.comparators.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import de.hybris.platform.solrfacetsearch.search.FacetValue;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class OfferStateFacetValuesComparator implements Comparator<FacetValue> {


  /**
   * 0 is the highest offer state priority
   */
  protected static final int LOWEST_OFFER_STATE_PRIORITY = 1000;

  @Value("#{'${mirakl.offers.state.facetpriority}'.split(',')}")
  protected List<String> prioritizedOfferStates;

  @Override
  public int compare(FacetValue facet1, FacetValue facet2) {
    Integer facet1Priority = getFacetPriority(facet1);
    Integer facet2Priority = getFacetPriority(facet2);

    return facet2Priority.compareTo(facet1Priority);
  }

  protected Integer getFacetPriority(FacetValue facetValue) {
    if (prioritizedOfferStates.contains(facetValue.getName())) {
      return prioritizedOfferStates.indexOf(facetValue.getName());
    }
    return LOWEST_OFFER_STATE_PRIORITY;
  }
}
