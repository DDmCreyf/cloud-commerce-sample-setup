package com.mirakl.hybris.core.shop.services;

import java.util.Collection;
import java.util.Date;

import com.mirakl.hybris.core.model.ShopModel;

public interface ShopImportService {

  /**
   * Incremental import of the Mirakl shops
   *
   * @param updatedSince Date of the last incremental update.
   * @return a <tt>Collection</tt> containing the imported shops
   */
  Collection<ShopModel> importShopsUpdatedSince(Date updatedSince);

  /**
   * Import all the shops from Mirakl
   *
   * @return a <tt>Collection</tt> containing the imported shops
   */
  Collection<ShopModel> importAllShops();
}
