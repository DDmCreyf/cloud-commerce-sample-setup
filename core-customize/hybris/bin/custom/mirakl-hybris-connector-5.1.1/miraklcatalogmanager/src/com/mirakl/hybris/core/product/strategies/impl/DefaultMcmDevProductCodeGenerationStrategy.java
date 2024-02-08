package com.mirakl.hybris.core.product.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.ProductCodeGenerationStrategy;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

public class DefaultMcmDevProductCodeGenerationStrategy implements ProductCodeGenerationStrategy {

  private ProductCodeGenerationStrategy fallbackStrategy;

  // Generating a code based on the MiraklID.
  // This way, several developers can work on the same mirakl environment without creating any collisions.
  @Override
  public String generateCode(ComposedTypeModel composedType, MiraklRawProductModel rawProduct, ProductModel baseProduct,
      ProductImportFileContextData context) {
    if (MiraklCatalogSystem.MCM.equals(context.getGlobalContext().getMiraklCatalogSystem())) {
      return String.format("mp-%s-%s", rawProduct.getMiraklProductId(), composedType.getCode());
    }
    return fallbackStrategy.generateCode(composedType, rawProduct, baseProduct, context);
  }

  @Required
  public void setFallbackStrategy(ProductCodeGenerationStrategy fallbackStrategy) {
    this.fallbackStrategy = fallbackStrategy;
  }
}
