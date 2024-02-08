package com.mirakl.hybris.core.product.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.ProductImportValidationStrategy;

public abstract class AbstractProductImportValidationStrategy implements ProductImportValidationStrategy {

  protected void checkNotNull(MiraklCoreAttributeModel coreAttribute, String message, MiraklRawProductModel rawProduct)
      throws ProductImportException {
    validateParameterNotNullStandardMessage("coreAttribute", coreAttribute);

    checkNotNull(coreAttribute.getCode(), message, rawProduct);
  }

  protected void checkNotNull(String attribute, String message, MiraklRawProductModel miraklRawProduct)
      throws ProductImportException {
    if (isBlank(miraklRawProduct.getValues().get(attribute))) {
      throw new ProductImportException(miraklRawProduct, message);
    }
  }

}
