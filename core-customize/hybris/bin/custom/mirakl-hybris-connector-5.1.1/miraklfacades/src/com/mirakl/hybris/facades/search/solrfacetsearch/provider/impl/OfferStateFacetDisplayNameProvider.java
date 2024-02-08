package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.OfferState;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

public class OfferStateFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider {

  protected EnumerationService enumerationService;

  @Override
  public String getDisplayName(SearchQuery searchQuery, IndexedProperty indexedProperty, String offerStateCode) {
    OfferState offerState = enumerationService.getEnumerationValue(OfferState.class, offerStateCode);
    return enumerationService.getEnumerationName(offerState);
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }
}
