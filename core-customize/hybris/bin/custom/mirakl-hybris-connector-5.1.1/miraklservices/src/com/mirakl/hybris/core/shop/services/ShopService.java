package com.mirakl.hybris.core.shop.services;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluations;
import com.mirakl.hybris.core.model.ShopModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

public interface ShopService {

  /**
   * Returns the model of the shop matching the given identifier
   * 
   * @param id Mirakl identifier of the shop
   * @return a <tt>ShopModel</tt> if any, <tt>null</tt> otherwise
   */
  ShopModel getShopForId(String id);

  /**
   * Synchronous call to Mirakl APIs to get the requested evaluation page for a shop
   *
   * @param id Mirakl identifier of the shop
   * @param pageableData filled with the page size and the current page
   * @return the filled <tt>MiraklEvaluations</tt> if any, empty otherwise
   */
  MiraklEvaluations getEvaluations(String id, PageableData pageableData);

  /**
   * Store shop custom fields.
   *
   * @param customFields the shop custom fields
   * @param shop the shop
   */
  void storeShopCustomFields(List<MiraklAdditionalFieldValue> customFields, ShopModel shop);

  /**
   * Load shop custom fields.
   *
   * @param shop the shop
   * @return the consignment entry custom fields
   */
  List<MiraklAdditionalFieldValue> loadShopCustomFields(ShopModel shop);
}
