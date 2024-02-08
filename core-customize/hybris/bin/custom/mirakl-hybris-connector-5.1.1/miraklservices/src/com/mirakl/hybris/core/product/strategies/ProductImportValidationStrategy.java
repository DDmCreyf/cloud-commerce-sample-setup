package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface ProductImportValidationStrategy {

  /**
   * Validates the product before importing it
   *
   * @param miraklRawProduct the raw product to validate
   * @param context the product import file context
   * @throws ProductImportException if the validation fails
   */
  void validate(MiraklRawProductModel miraklRawProduct, ProductImportFileContextData context) throws ProductImportException;

}
