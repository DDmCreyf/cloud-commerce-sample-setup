package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.util.CronJobUtils.getOrCreateDirectory;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;
import com.mirakl.hybris.core.product.strategies.DownloadProductFilesDirectorySelectionStrategy;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DefaultDownloadProductFilesDirectorySelectionStrategy implements DownloadProductFilesDirectorySelectionStrategy {

  protected ConfigurationService configurationService;
  private String baseDirectory;

  @Override
  public String getBaseDirectoryPath() {
    return isNotBlank(baseDirectory) ? baseDirectory : configurationService.getConfiguration().getString("HYBRIS_DATA_DIR");
  }

  @Override
  public File getTargetDirectory(MiraklDownloadProductFilesCronjobModel cronJob) {
    return getOrCreateDirectory(cronJob.getDownloadDirectory() != null ? cronJob.getDownloadDirectory()
        : cronJob.getProductimportCronJob().getInputDirectory(), getBaseDirectoryPath());
  }

  @Override
  public void setBaseDirectoryPath(String baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }


}
