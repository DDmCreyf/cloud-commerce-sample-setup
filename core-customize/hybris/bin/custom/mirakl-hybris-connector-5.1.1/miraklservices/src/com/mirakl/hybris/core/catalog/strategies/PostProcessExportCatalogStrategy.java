package com.mirakl.hybris.core.catalog.strategies;

import java.io.IOException;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

public interface PostProcessExportCatalogStrategy {


  /**
   * Contains logic to be executed after the export is performed
   * 
   * @param context the export context
   * @throws IOException
   */
  void postProcess(MiraklExportCatalogCronJobModel cronJob, MiraklExportCatalogContext context) throws IOException;

}
