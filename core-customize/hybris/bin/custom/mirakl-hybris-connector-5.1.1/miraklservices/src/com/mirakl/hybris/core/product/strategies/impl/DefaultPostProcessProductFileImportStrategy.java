package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.strategies.PostProcessProductFileImportStrategy;

public class DefaultPostProcessProductFileImportStrategy implements PostProcessProductFileImportStrategy {

  @Override
  public void postProcess(ProductImportFileContextData context, String importId) {
    // Default is empty. Used as an extension point

  }

}
