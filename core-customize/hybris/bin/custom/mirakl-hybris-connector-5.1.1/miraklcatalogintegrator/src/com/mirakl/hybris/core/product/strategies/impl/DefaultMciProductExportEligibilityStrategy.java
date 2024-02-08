package com.mirakl.hybris.core.product.strategies.impl;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.daos.MiraklProductDao;
import com.mirakl.hybris.core.product.strategies.MciProductExportEligibilityStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMciProductExportEligibilityStrategy implements MciProductExportEligibilityStrategy {

  protected MiraklProductDao miraklProductDao;
  protected ModelService modelService;

  @Override
  public Collection<ProductModel> getModifiedProductsEligibleForExport(CatalogVersionModel catalogVersion, Date modifiedAfter) {
    return miraklProductDao.findModifiedProductsWithNoVariantType(modifiedAfter, catalogVersion);
  }

  @Override
  public Collection<ProductModel> getAllProductsEligibleForExport(CatalogVersionModel catalogVersion) {
    return miraklProductDao.findModifiedProductsWithNoVariantType(null, catalogVersion);
  }

  @Required
  public void setMiraklProductDao(MiraklProductDao miraklProductDao) {
    this.miraklProductDao = miraklProductDao;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
