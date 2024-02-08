package com.mirakl.hybris.core.catalog.attributes;

import java.util.Set;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ExportCatalogCoreAttributesDynamicHandler
    extends AbstractCatalogJobCoreAttributesDynamicHandler<MiraklExportCatalogCronJobModel> {

  @Override
  public Set<MiraklCoreAttributeModel> get(MiraklExportCatalogCronJobModel exportCatalogJob) {
    return getCoreAttributes(exportCatalogJob.getCoreAttributeConfiguration());
  }

  @Override
  public void set(MiraklExportCatalogCronJobModel exportCatalogJob, Set<MiraklCoreAttributeModel> coreAttributes) {
    setCoreAttributes(exportCatalogJob.getCoreAttributeConfiguration(), coreAttributes);
  }
}
