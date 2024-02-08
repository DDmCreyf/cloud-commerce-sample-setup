package com.mirakl.hybris.core.catalog.strategies.impl;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultReplicatedAttributeHandler extends DefaultCoreAttributeHandler {

  @Override
  public void setValue(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    ProductModel owner = data.getProductToUpdate();
    while (owner instanceof VariantProductModel) {
      setValue(attribute, owner, data, context);
      markItemsToSave(data, owner);
      owner = ((VariantProductModel) owner).getBaseProduct();
    }
    setValue(attribute, owner, data, context);
    markItemsToSave(data, owner);
  }

}
