package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

public class ShopFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider {

  protected ShopService shopService;

  @Override
  public String getDisplayName(SearchQuery paramSearchQuery, IndexedProperty paramIndexedProperty, String shopId) {
    BaseSiteModel baseSite = paramSearchQuery.getFacetSearchConfig().getIndexConfig().getBaseSite();
    if (shopId.equalsIgnoreCase(baseSite.getOperatorCode())) {
      return baseSite.getOperatorName();
    }

    ShopModel shop = shopService.getShopForId(shopId);
    if (shop != null) {
      return shop.getName();
    }
    return shopId;
  }

  @Required
  public void setShopService(ShopService shopService) {
    this.shopService = shopService;
  }

}
