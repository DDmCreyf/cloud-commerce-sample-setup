package com.mirakl.hybris.core.product.services.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.product.services.McmProductExportService;
import com.mirakl.hybris.core.product.strategies.McmProductExportEligibilityStrategy;
import com.mirakl.hybris.core.product.strategies.McmProductExportStrategy;

import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMcmProductExportService implements McmProductExportService {

  protected McmProductExportStrategy mcmProductExportStrategy;
  protected McmProductExportEligibilityStrategy eligibilityStrategy;

  @Override
  public int exportProductDataSheets(ProductDataSheetExportContextData context) throws IOException {
    Collection<ProductModel> products = eligibilityStrategy.getProductDataSheetsEligibleForExport(context);
    return mcmProductExportStrategy.exportProductDataSheets(products, context);
  }

  @Required
  public void setEligibilityStrategy(McmProductExportEligibilityStrategy eligibilityStrategy) {
    this.eligibilityStrategy = eligibilityStrategy;
  }

  @Required
  public void setMcmProductExportStrategy(McmProductExportStrategy mcmProductExportStrategy) {
    this.mcmProductExportStrategy = mcmProductExportStrategy;
  }

}
