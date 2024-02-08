package com.mirakl.hybris.core.catalog.attributes;

import java.util.Set;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ProductImportCoreAttributesDynamicHandler
    extends AbstractCatalogJobCoreAttributesDynamicHandler<MiraklProductImportCronJobModel> {

  @Override
  public Set<MiraklCoreAttributeModel> get(MiraklProductImportCronJobModel productImportJob) {
    return getCoreAttributes(productImportJob.getCoreAttributeConfiguration());
  }

  @Override
  public void set(MiraklProductImportCronJobModel productImportJob, Set<MiraklCoreAttributeModel> coreAttributes) {
    setCoreAttributes(productImportJob.getCoreAttributeConfiguration(), coreAttributes);
  }

}
