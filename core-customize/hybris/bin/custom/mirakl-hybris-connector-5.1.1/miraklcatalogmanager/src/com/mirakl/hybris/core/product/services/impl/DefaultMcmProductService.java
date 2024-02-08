package com.mirakl.hybris.core.product.services.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.daos.McmProductDao;
import com.mirakl.hybris.core.product.services.McmProductService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultMcmProductService implements McmProductService {

  protected McmProductDao mcmProductDao;

  @Override
  public ProductModel getProductForMiraklVariantGroupCode(String variantGroupCode, CatalogVersionModel catalogVersion) {
    return mcmProductDao.findProductForMiraklVariantGroupCode(variantGroupCode, catalogVersion);
  }

  @Override
  public ProductModel getProductForChecksum(String checksum, CatalogVersionModel catalogVersion) {
    return mcmProductDao.findProductForChecksum(checksum, catalogVersion);
  }

  @Required
  public void setMcmProductDao(McmProductDao mcmProductDao) {
    this.mcmProductDao = mcmProductDao;
  }


}
