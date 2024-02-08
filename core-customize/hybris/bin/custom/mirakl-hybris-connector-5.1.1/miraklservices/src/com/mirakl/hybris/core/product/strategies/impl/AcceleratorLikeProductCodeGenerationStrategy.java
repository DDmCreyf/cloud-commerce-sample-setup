package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.valueOf;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.ProductCodeGenerationStrategy;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.variants.model.VariantTypeModel;

public class AcceleratorLikeProductCodeGenerationStrategy implements ProductCodeGenerationStrategy {

  protected KeyGenerator keyGenerator;

  @Override
  public String generateCode(ComposedTypeModel composedType, MiraklRawProductModel rawProduct, ProductModel baseProduct,
      ProductImportFileContextData context) {
    StringBuilder code = new StringBuilder(baseProduct == null ? valueOf(keyGenerator.generate()) : baseProduct.getCode());
    if (composedType instanceof VariantTypeModel) {
      Map<String, Set<String>> declaredVariantAttributesPerTypeCode =
          context.getGlobalContext().getDeclaredVariantAttributesPerType();
      for (String variantAttribute : declaredVariantAttributesPerTypeCode.get(composedType.getCode())) {
        code.append("_").append(rawProduct.getValues().get(variantAttribute));
      }
    }
    return code.toString();
  }

  @Required
  public void setKeyGenerator(KeyGenerator keyGenerator) {
    this.keyGenerator = keyGenerator;
  }
}
