package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

public interface ProductCodeGenerationStrategy {

  /**
   * Generates a unique code for the given product
   *
   * @param composedType the composed type of the product (mainly used to know if it is a variant or not)
   * @param rawProduct the raw product from the shop
   * @param baseProduct the base product of the variant
   * @param context the product import file context
   * @return the generated code
   */
  String generateCode(ComposedTypeModel composedType, MiraklRawProductModel rawProduct, ProductModel baseProduct,
      ProductImportFileContextData context);

}
