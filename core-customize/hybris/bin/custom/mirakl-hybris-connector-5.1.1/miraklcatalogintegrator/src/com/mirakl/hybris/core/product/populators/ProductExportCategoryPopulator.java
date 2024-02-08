package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklProductExportHeader.CATEGORY_CODE;

import java.util.Map;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductExportCategoryPopulator extends AbstractProductExportWithFallbackPopulator {

  @Override
  protected void populateAttributesIfNotPresent(ProductModel source, Map<String, String> target) throws ConversionException {
    if (target.get(CATEGORY_CODE.getCode()) == null) {
      target.put(CATEGORY_CODE.getCode(), getCategoryCode(source));
    }

  }

}
