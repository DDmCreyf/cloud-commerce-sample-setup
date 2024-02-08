package com.mirakl.hybris.core.product.services;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.mirakl.hybris.core.enums.MiraklProductImportStatus;

public interface MciProductFileDownloadService {

  /**
   * Get a list of available sellers' transformed product files id from Mirakl (MCI) (P51 API)
   *
   * @param since (optional) Only the files transformed after this date will be retrieved
   * @param shopId (optional) Only the files coming from this shop will be retrieved
   * @param statuses The status(es) of the product files to be retrieved
   * @return a list of product import file ids
   */
  List<String> getImportIds(Date since, String shopId, Collection<MiraklProductImportStatus> statuses);

  /**
   * Downloads the product files from mirakl based on their id (see {@link #getImportIds}) (MCI) (P46 API)
   *
   * @param fileIds A list of product file Ids
   * @param targetDirectory The directory in which the files should be downloaded
   * @return the count of files downloaded
   */
  int downloadProductFiles(List<String> fileIds, File targetDirectory);

}
