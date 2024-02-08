package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.valueOf;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.ProductCodeGenerationStrategy;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

public class DefaultProductCodeGenerationStrategy implements ProductCodeGenerationStrategy {

  protected KeyGenerator keyGenerator;

  @Override
  public String generateCode(ComposedTypeModel composedType, MiraklRawProductModel rawProduct, ProductModel baseProduct,
      ProductImportFileContextData context) {
    return valueOf(keyGenerator.generate());
  }

  @Required
  public void setKeyGenerator(KeyGenerator keyGenerator) {
    this.keyGenerator = keyGenerator;
  }

}
