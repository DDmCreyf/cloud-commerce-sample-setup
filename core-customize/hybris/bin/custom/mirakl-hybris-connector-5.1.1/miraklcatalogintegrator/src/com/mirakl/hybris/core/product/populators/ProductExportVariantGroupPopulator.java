package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.VARIANT_GROUP_CODE;

import java.util.Map;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

public class ProductExportVariantGroupPopulator implements Populator<ProductModel, Map<String, String>> {

  @Override
  public void populate(ProductModel source, Map<String, String> target) throws ConversionException {
    target.put(VARIANT_GROUP_CODE.getCode(), getGroupingProduct(source).getCode());
  }

  protected ProductModel getGroupingProduct(ProductModel source) {
    return source instanceof VariantProductModel ? ((VariantProductModel) source).getBaseProduct() : source;
  }

}
