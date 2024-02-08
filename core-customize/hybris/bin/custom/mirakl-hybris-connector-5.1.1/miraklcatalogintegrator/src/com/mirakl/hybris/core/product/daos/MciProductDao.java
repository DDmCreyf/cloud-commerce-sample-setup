package com.mirakl.hybris.core.product.daos;

import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MciProductDao extends GenericDao<ProductModel> {

  /**
   * Finds a product for a given shop variant group code
   *
   * @param shop the shop selling the product
   * @param variantGroupCode the variant group code given by the shop to the product
   * @param catalogVersion the catalog version to search in
   * @return the product matching the search criteria
   */
  ProductModel findProductForShopVariantGroupCode(ShopModel shop, String variantGroupCode, CatalogVersionModel catalogVersion);


}
