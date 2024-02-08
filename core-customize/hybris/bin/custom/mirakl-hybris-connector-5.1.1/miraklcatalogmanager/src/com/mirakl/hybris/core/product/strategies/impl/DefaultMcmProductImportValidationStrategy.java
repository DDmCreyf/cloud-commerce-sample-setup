package com.mirakl.hybris.core.product.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Map;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public class DefaultMcmProductImportValidationStrategy extends AbstractProductImportValidationStrategy {

  @Override
  public void validate(MiraklRawProductModel rawProduct, ProductImportFileContextData context) throws ProductImportException {
    validateParameterNotNullStandardMessage("rawProduct", rawProduct);
    validateParameterNotNullStandardMessage("context", context);

    Map<MiraklAttributeRole, String> coreAttributePerRole = context.getGlobalContext().getCoreAttributePerRole();
    checkNotNull(coreAttributePerRole.get(MiraklAttributeRole.CATEGORY_ATTRIBUTE), "Category attribute must be provided",
        rawProduct);
  }

}
