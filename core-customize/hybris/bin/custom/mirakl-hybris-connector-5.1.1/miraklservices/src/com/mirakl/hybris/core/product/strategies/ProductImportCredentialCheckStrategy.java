package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface ProductImportCredentialCheckStrategy {

  /**
   * Custom logic allowing to authorize or not the creation of a new product. By default, all product creations are authorized.
   * 
   * @param data the product import data
   * @param context the product import file context
   * @throws ProductImportException when the creation of the product is refused
   */
  void checkProductCreationCredentials(ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException;

  /**
   * Custom logic allowing to authorize or not the update of an existing product. By default, all product updates are authorized.
   * 
   * @param data the product import data
   * @param context the product import file context
   * @throws ProductImportException when the update of the product is refused
   */
  void checkProductUpdateCredentials(ProductImportData data, ProductImportFileContextData context) throws ProductImportException;

}
