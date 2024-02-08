package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportFileContextData;

public interface PostProcessProductFileImportStrategy {

  /**
   * Called after a product is imported. Can be used to add custom logic after the import
   * 
   * @param context the file import context
   * @param importId ther importId
   */
  void postProcess(ProductImportFileContextData context, String importId);

}
