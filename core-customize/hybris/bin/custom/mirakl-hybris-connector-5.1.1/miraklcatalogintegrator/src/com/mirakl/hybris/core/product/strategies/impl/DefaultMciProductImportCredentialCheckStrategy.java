package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

public class DefaultMciProductImportCredentialCheckStrategy extends AbstractProductImportCredentialCheckStrategy {

  protected boolean isProductCreationAllowed(ProductImportData data, ProductImportFileContextData context) {
    return true;
  }

  protected boolean isProductUpdateAllowed(ProductImportData data, ProductImportFileContextData context) {
    return true;
  }

}
