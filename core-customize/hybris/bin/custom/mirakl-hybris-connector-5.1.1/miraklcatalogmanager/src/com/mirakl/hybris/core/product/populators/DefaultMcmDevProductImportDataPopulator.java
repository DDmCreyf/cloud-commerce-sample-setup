package com.mirakl.hybris.core.product.populators;

import static org.apache.commons.lang.StringUtils.isBlank;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultMcmDevProductImportDataPopulator extends DefaultMcmProductImportDataPopulator {

  // Ignoring unknown SKUs as this situation can occur when several developers are working on a single Mirakl environment. 
  // Should not happen in production though.
  @Override
  protected void resolveProductBySku(ProductImportData target, MiraklRawProductModel rawProduct,
      CatalogVersionModel catalogVersion) {
    if (!isBlank(rawProduct.getSku())) {
      try {
        target.setProductResolvedBySku(productService.getProductForCode(catalogVersion, rawProduct.getSku()));
      } catch (UnknownIdentifierException ignored) {
        // Ignore unknown products
      }
    }
  }

}
