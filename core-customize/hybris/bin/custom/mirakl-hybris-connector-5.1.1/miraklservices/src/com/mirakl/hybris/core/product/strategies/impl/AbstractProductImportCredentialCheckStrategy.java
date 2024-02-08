package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.ProductImportCredentialCheckStrategy;

public abstract class AbstractProductImportCredentialCheckStrategy implements ProductImportCredentialCheckStrategy {

  @Override
  public void checkProductCreationCredentials(ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    if (!isProductCreationAllowed(data, context)) {
      throw new ProductImportException(data.getRawProduct(), "Creation is forbidden for this product");
    }
  }

  @Override
  public void checkProductUpdateCredentials(ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    if (!isProductUpdateAllowed(data, context)) {
      throw new ProductImportException(data.getRawProduct(), "Update is forbidden for this product");
    }
  }

  abstract boolean isProductCreationAllowed(ProductImportData data, ProductImportFileContextData context);

  abstract boolean isProductUpdateAllowed(ProductImportData data, ProductImportFileContextData context);

}
