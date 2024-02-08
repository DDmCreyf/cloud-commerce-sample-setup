package com.mirakl.hybris.core.product.strategies.impl;

import java.util.Set;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.ProductIdentificationStrategy;

import de.hybris.platform.core.model.product.ProductModel;

public class DefaultProductIdentificationStrategy implements ProductIdentificationStrategy {

  @Override
  public void identifyProduct(ProductImportData data) throws ProductImportException {
    Set<ProductModel> productsResolvedByUID = data.getProductsResolvedByUID();
    ProductModel productResolvedBySku = data.getProductResolvedBySku();

    if (productsResolvedByUID.size() > 1) {
      throw new ProductImportException(data.getRawProduct(),
          "Unable to identify a product. Found more than one product matching the same unique identifier");
    }

    if (productsResolvedByUID.size() == 1) {
      ProductModel productResolvedByUid = productsResolvedByUID.iterator().next();
      data.setIdentifiedProduct(productResolvedByUid);
      return;
    }

    if (productResolvedBySku != null) {
      data.setIdentifiedProduct(productResolvedBySku);
    }
  }

}
