package com.mirakl.hybris.core.product.services;

import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface MciProductService {

  /**
   * Get the product matching the given variant group code in the specified catalog. (MCI)
   *
   * @param shop The shop which sent the product
   * @param variantGroupCode The variant group code given by the shop to the product
   * @param catalogVersion The catalog version where the product was saved
   * @return The product matching the given parameters
   */
  ProductModel getProductForShopVariantGroupCode(ShopModel shop, String variantGroupCode, CatalogVersionModel catalogVersion);

}
