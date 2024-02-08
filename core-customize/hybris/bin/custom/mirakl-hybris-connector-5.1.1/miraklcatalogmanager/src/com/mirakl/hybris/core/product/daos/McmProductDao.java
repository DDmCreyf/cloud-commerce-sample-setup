package com.mirakl.hybris.core.product.daos;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface McmProductDao extends GenericDao<ProductModel> {

  /**
   * Finds products with the requested Mirakl variant group code (MCM)
   *
   * @param variantGroupCode Variant group code
   * @param catalogVersion the catalog version to search in
   * @return a product, if any
   */
  ProductModel findProductForMiraklVariantGroupCode(String variantGroupCode, CatalogVersionModel catalogVersion);

  /**
   * Finds products with the requested checksum
   *
   * @param checksum checksum of the product
   * @param catalogVersion catalog version to search in
   * @return a product, if any
   */
  ProductModel findProductForChecksum(String checksum, CatalogVersionModel catalogVersion);

}
