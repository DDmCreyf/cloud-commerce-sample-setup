package com.mirakl.hybris.core.shop.daos;

import com.mirakl.hybris.core.model.ShopModel;

import java.util.Collection;

public interface ShopDao {

  /**
   * Returns the ShopModel for the given Id
   * 
   * @param shopId
   * @return a ShopModel, or null if no shop was found
   * 
   */
  ShopModel findShopById(String shopId);

  /**
   * Returns a list of the shops having an offer for the specified product
   * 
   * @param productCode
   * @return a Collection of ShopModel
   * 
   */
  Collection<ShopModel> findShopsForProductCode(String productCode);
}
