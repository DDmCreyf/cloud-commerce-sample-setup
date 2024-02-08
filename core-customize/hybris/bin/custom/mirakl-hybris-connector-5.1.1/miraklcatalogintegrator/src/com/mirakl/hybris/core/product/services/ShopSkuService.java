package com.mirakl.hybris.core.product.services;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.model.ShopSkuModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface ShopSkuService {

  /**
   * Searches for a shop sku having a given checksum
   * 
   * @param checksum the checksum to search
   * @param shop the shop
   * @param catalogVersion the catalog version to search in
   * @return a {@link ShopSkuModel} matching the given checksum or null otherwise
   */
  ShopSkuModel getShopSkuForChecksum(String checksum, ShopModel shop, CatalogVersionModel catalogVersion);

  /**
   * Searches for a shop sku having a given sku
   * 
   * @param sku the sku to search
   * @param shop the shop
   * @param catalogVersion the catalog version to search in
   * @return a {@link ShopSkuModel} matching the given sku or null otherwise
   */
  ShopSkuModel getShopSkuForSku(String sku, ShopModel shop, CatalogVersionModel catalogVersion);

  /**
   * Removes the {@link ShopSkuModel} from a product
   *
   * @param sku the sku code of the product
   * @param shop the shop which created the sku
   * @param product the product sent by the shop
   */
  void removeShopSkuFromProduct(String sku, ShopModel shop, ProductModel product);

  /**
   * Adds a {@link ShopSkuModel} to a product
   *
   * @param shopSku the {@link ShopSkuModel} of the product
   * @param product the product
   * @return the {@link ShopSkuModel}
   */
  ShopSkuModel addShopSkuToProduct(ShopSkuModel shopSku, ProductModel product);

  /**
   * Gets the {@link ShopSkuModel} given by a shop to a product
   *
   * @param shop the shop which created the sku
   * @param product the product sent by the shop
   * @return the {@link ShopSkuModel}
   */
  ShopSkuModel getShopSku(ShopModel shop, ProductModel product);


}
