package com.mirakl.hybris.core.product.strategies;

import java.io.File;

import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;

public interface DownloadProductFilesDirectorySelectionStrategy {

  /**
   * Returns the base directory used for the product files download
   * 
   * @return the base directory
   */
  String getBaseDirectoryPath();

  /**
   * Sets the base directory used for the product files download
   * 
   * @param baseDirectory the base directory used for the product files download
   */
  void setBaseDirectoryPath(String baseDirectory);

  /**
   * Returns the directory in which to save the downloaded product files
   * 
   * @param cronJob the download product files cronjob instance
   * @return the directory in which to save the downloaded product files
   */
  File getTargetDirectory(MiraklDownloadProductFilesCronjobModel cronJob);

}
