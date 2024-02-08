package com.mirakl.hybris.facades.shop;

import com.mirakl.hybris.beans.EvaluationPageData;
import com.mirakl.hybris.beans.ShopData;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;


public interface ShopFacade {
  /**
   * Returns the Shop data matching the given id
   *
   * @param id the shop id
   * @return Shop data
   */
  ShopData getShopForId(String id);

  /**
   * Returns the evaluations for the designated shop
   *
   * @param id the id of the shop
   * @param pageableData filled with the page size and the current page
   * @return the requested page of evaluations
   */
  EvaluationPageData getShopEvaluationPage(String id, PageableData pageableData);
}
