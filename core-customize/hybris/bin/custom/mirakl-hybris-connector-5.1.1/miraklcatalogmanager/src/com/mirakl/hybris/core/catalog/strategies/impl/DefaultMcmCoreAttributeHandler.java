package com.mirakl.hybris.core.catalog.strategies.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.ProductExportAttributeValueFormattingStrategy;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultMcmCoreAttributeHandler extends AbstractMcmCoreAttributeHandler<MiraklCoreAttributeModel> {

  protected ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy;

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute,
      ProductDataSheetExportContextData context) {
    if (!isAttributePresentOnType(coreAttribute.getCode(), product.getItemtype(), context)) {
      return null;
    }

    ProductModel currentProduct = product;
    Object value = modelService.getAttributeValue(currentProduct, coreAttribute.getCode());

    while (value == null && currentProduct instanceof VariantProductModel) {
      currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();
      value = getValue(currentProduct, coreAttribute, context);
    }
    return formattingStrategy.formatValueForExport(value);
  }

  @Override
  public String getValue(ProductModel product, MiraklCoreAttributeModel coreAttribute, Locale locale,
      ProductDataSheetExportContextData context) {
    if (!coreAttribute.isLocalized()
        || !isAttributePresentOnType(coreAttribute.getCode(), product.getItemtype(), context)) {
      return null;
    }

    ProductModel currentProduct = product;
    String attributeValue = modelService.getAttributeValue(currentProduct, coreAttribute.getCode(), locale);
    while (attributeValue == null && currentProduct instanceof VariantProductModel) {
      currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();
      attributeValue = getValue(currentProduct, coreAttribute, locale, context);
    }

    return attributeValue;
  }

  @Required
  public void setFormattingStrategy(ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy) {
    this.formattingStrategy = formattingStrategy;
  }



}
