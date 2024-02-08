package com.mirakl.hybris.core.product.daos;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface ShopSkuDao extends GenericDao<ShopSkuModel> {

  /**
   * Returns the ShopSkuModel with the specified checksum
   *
   * @param checksum the checksum of the shop sku
   * @param shop the shop to search
   * @param catalogVersion the catalog version to search in
   * @return The ShopSkuModel with the specified checksum
   */
  ShopSkuModel findShopSkuByChecksum(String checksum, ShopModel shop, CatalogVersionModel catalogVersion);

  /**
   * Returns the ShopSkuModel with the specified sku
   *
   * @param sku the sku given by the shop
   * @param shop the shop to search
   * @param catalogVersion the catalog version to search in
   * @return The ShopSkuModel with the specified sku
   */
  ShopSkuModel findShopSkuBySku(String sku, ShopModel shop, CatalogVersionModel catalogVersion);

}
