package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.PostProcessProductLineImportStrategy;

public class DefaultPostProcessProductLineImportStrategy implements PostProcessProductLineImportStrategy {

  @Override
  public void postProcess(ProductImportData data, MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    // Default is empty. Used as an extension point
  }

}
