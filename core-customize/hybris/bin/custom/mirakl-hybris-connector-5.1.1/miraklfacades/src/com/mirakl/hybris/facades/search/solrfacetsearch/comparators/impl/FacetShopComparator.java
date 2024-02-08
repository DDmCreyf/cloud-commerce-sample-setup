package com.mirakl.hybris.facades.search.solrfacetsearch.comparators.impl;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

public class FacetShopComparator implements Comparator<FacetValue> {

  protected BaseSiteService baseSiteService;

  @Override
  public int compare(FacetValue facetValue1, FacetValue facetValue2) {
    BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
    if (currentBaseSite != null && currentBaseSite.getOperatorCode() != null) {
      if (currentBaseSite.getOperatorCode().equals(facetValue1.getName())) {
        return 1;
      }
      if (currentBaseSite.getOperatorCode().equals(facetValue2.getName())) {
        return -1;
      }
    }
    return 0;
  }

  @Required
  public void setBaseSiteService(BaseSiteService baseSiteService) {
    this.baseSiteService = baseSiteService;
  }

}
