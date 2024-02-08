package com.mirakl.hybris.core.product.services;

import java.util.Collection;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public interface ProductImportService {

  /**
   * Imports the raw products into Hybris using the data provided by the {@link ProductImportFileContextData}. The
   * {@link MiraklRawProductModel} are entries from the product import staging table.
   *
   * @param variants the raw products to be imported
   * @param context the product import file context
   */
  void importProducts(Collection<MiraklRawProductModel> variants, ProductImportFileContextData context);

}
