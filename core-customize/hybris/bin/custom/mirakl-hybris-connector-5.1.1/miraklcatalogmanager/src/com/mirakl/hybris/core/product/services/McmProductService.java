package com.mirakl.hybris.core.product.services;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface McmProductService {

  /**
   * Get the product matching the given Mirakl variant group code in the specified catalog (MCM)
   *
   * @param variantGroupCode Variant group code of the product
   * @param catalogVersion Catalog version where the product was saved
   * @return The product matching the given parameters
   */
  ProductModel getProductForMiraklVariantGroupCode(String variantGroupCode, CatalogVersionModel catalogVersion);

  /**
   * Get the product matching the given checksum. The checksum is calculated using the Mirakl product lines (MCM)
   *
   * @param checksum checksum of the imported Mirakl product
   * @param catalogVersion catalog version of the product
   * @return A product matching the given checksum, if any
   */
  ProductModel getProductForChecksum(String checksum, CatalogVersionModel catalogVersion);

}
