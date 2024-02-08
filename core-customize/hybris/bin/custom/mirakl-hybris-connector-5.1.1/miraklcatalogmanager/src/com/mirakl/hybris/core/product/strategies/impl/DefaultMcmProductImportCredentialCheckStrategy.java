package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.logging.log4j.util.Strings.isBlank;

import org.apache.log4j.Logger;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMcmProductImportCredentialCheckStrategy extends AbstractProductImportCredentialCheckStrategy {

  private static final Logger LOG = getLogger(DefaultMcmProductImportCredentialCheckStrategy.class.getName());

  @Override
  protected boolean isProductCreationAllowed(ProductImportData data, ProductImportFileContextData context) {
    return true;
  }

  @Override
  protected boolean isProductUpdateAllowed(ProductImportData data, ProductImportFileContextData context) {
    ProductModel identifiedProduct = data.getIdentifiedProduct();
    if (identifiedProduct == null) {
      return true;
    }
    if (!(isBlank(identifiedProduct.getMiraklProductId()) || identifiedProduct.getCode().equals(data.getRawProduct().getSku())
        && identifiedProduct.getMiraklProductId().equals(data.getRawProduct().getMiraklProductId()))) {
      LOG.error(String.format(
          "Can not update identified product: Incoherent SKU [local=%s, imported=%s] & Mirakl IDs [local=%s, imported=%s].",
          identifiedProduct.getCode(), data.getRawProduct().getSku(), identifiedProduct.getMiraklProductId(),
          data.getRawProduct().getMiraklProductId()));
      return false;
    }
    return true;
  }

}
