package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.PostProcessProductLineImportStrategy;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMciSamplePostProcessProductLineImportStrategy implements PostProcessProductLineImportStrategy {

  @Override
  public void postProcess(ProductImportData data, MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    if (MiraklCatalogSystem.MCI.equals(context.getGlobalContext().getMiraklCatalogSystem())) {
      applyApprovalStatus(data);
    }
  }

  protected void applyApprovalStatus(ProductImportData data) {
    ProductModel product = data.getProductToUpdate();
    product.setApprovalStatus(ArticleApprovalStatus.APPROVED);
    data.getModelsToSave().add(product);
  }

}
