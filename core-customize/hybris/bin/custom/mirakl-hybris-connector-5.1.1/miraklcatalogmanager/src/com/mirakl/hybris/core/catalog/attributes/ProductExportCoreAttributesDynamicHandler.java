package com.mirakl.hybris.core.catalog.attributes;

import java.util.Set;

import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;

public class ProductExportCoreAttributesDynamicHandler
    extends AbstractCatalogJobCoreAttributesDynamicHandler<MiraklExportSellableProductsCronJobModel> {

  @Override
  public Set<MiraklCoreAttributeModel> get(MiraklExportSellableProductsCronJobModel cronJob) {
    return getCoreAttributes(cronJob.getCoreAttributeConfiguration());
  }

  @Override
  public void set(MiraklExportSellableProductsCronJobModel cronJob, Set<MiraklCoreAttributeModel> coreAttributes) {
    setCoreAttributes(cronJob.getCoreAttributeConfiguration(), coreAttributes);
  }

}
