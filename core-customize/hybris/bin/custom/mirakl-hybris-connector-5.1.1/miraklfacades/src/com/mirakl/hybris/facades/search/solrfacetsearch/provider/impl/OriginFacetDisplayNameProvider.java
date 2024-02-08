package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.ProductOrigin;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

public class OriginFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider {

  protected EnumerationService enumerationService;

  @Override
  public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue) {
    if (facetValue == null) {
      return "";
    }
    ProductOrigin productOrigin = enumerationService.getEnumerationValue(ProductOrigin.class, facetValue);
    return enumerationService.getEnumerationName(productOrigin);
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

}
