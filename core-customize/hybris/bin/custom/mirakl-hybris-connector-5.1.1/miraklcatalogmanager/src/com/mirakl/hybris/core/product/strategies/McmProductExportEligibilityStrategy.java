package com.mirakl.hybris.core.product.strategies;

import java.util.Collection;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;

import de.hybris.platform.core.model.product.ProductModel;

public interface McmProductExportEligibilityStrategy {

  /**
   * Finds products eligible for the CM21 export.
   *
   * @param context export context
   * @return a Collection of eligible products
   */
  Collection<ProductModel> getProductDataSheetsEligibleForExport(ProductDataSheetExportContextData context);


}
