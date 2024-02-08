package com.mirakl.hybris.core.product.strategies;

import java.util.Collection;
import java.util.Date;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface MciProductExportEligibilityStrategy {

  /**
   * Finds products eligible for the incremental P21 export.
   *
   * @param catalogVersion catalog version used for the export
   * @param modifiedAfter date used for incremental export
   * @return a Collection of eligible products
   */
  Collection<ProductModel> getModifiedProductsEligibleForExport(CatalogVersionModel catalogVersion, Date modifiedAfter);

  /**
   * Finds products eligible for the full P21 export.
   *
   * @param catalogVersion catalog version used for the export
   * @return a Collection of eligible products
   */
  Collection<ProductModel> getAllProductsEligibleForExport(CatalogVersionModel catalogVersion);

}
