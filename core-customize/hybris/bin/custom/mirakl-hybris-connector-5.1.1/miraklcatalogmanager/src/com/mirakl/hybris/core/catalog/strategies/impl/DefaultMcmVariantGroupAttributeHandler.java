package com.mirakl.hybris.core.catalog.strategies.impl;

import java.util.Locale;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultMcmVariantGroupAttributeHandler extends AbstractMcmCoreAttributeHandler<MiraklCoreAttributeModel> {

  @Override
  public void setValue(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    data.getRootBaseProductToUpdate().setMiraklVariantGroupCode(attribute.getValue());
  }

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute,
      ProductDataSheetExportContextData context) {
    ProductModel currentProduct = product;
    boolean isVariant = false;
    while (currentProduct instanceof VariantProductModel) {
      currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();
      isVariant = true;
    }
    if(isVariant && currentProduct.getMiraklVariantGroupCode() == null){
      return currentProduct.getCode();
    } else {
      return currentProduct.getMiraklVariantGroupCode();
    }
  }

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute, Locale locale,
      ProductDataSheetExportContextData context) {
    return getValue(product, coreAttribute, context);
  }

}
