package com.mirakl.hybris.core.product.services;

import java.io.File;
import java.util.Date;
import java.util.Set;

import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.enums.MarketplaceProductAcceptanceStatus;



public interface McmProductFileDownloadService {

  /**
   * Downloads the master product datasheets file from Mirakl (MCM) (CM51 API)
   *
   * @param since (optional) Used for incremental import. Leave empty to download all the products
   * @param acceptanceStatuses Acceptance statuses of the products
   * @param targetDirectory The directory in which the file should be downloaded
   * @return true if the download was performed successfully
   * 
   * @deprecated
   */
  @Deprecated
  boolean downloadProductDataSheetsFile(Date since, Set<MarketplaceProductAcceptanceStatus> acceptanceStatuses,
      File targetDirectory);

  /**
   * Downloads the master product datasheets file from Mirakl (MCM) (CM51 API)
   *
   * @param params the params to be used for the download
   * 
   * @return true if the download was performed successfully
   */ 
  boolean downloadProductDataSheetsFile(ProductDataSheetDownloadParams params);

}
