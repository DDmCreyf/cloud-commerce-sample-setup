package com.mirakl.hybris.core.catalog.strategies;

import java.util.Collection;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface ClassificationAttributeUpdateStrategy {

  /**
   * Updates all the classification attributes of the matching product
   *
   * @param attributeValues The attributes and their new values
   * @param data the product related data
   * @param context the product import file context data
   * @throws ProductImportException if an error occurred during the attribute update
   */
  void updateAttributes(Collection<AttributeValueData> attributeValues, ProductImportData data,
      ProductImportFileContextData context) throws ProductImportException;

}
