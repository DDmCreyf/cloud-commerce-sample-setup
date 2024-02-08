package com.mirakl.hybris.core.product.strategies.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;
import com.mirakl.hybris.core.product.strategies.DownloadProductFilesDirectorySelectionStrategy;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractDownloadProductFilesStrategy implements PerformJobStrategy<MiraklDownloadProductFilesCronjobModel> {

  private static final Logger LOG = Logger.getLogger(AbstractDownloadProductFilesStrategy.class);

  protected ModelService modelService;
  protected ConfigurationService configurationService;
  protected DownloadProductFilesDirectorySelectionStrategy directorySelectionStrategy; 

  protected File getTargetDirectory(MiraklDownloadProductFilesCronjobModel cronJob) {
    return directorySelectionStrategy.getTargetDirectory(cronJob);
  }

  protected String getBaseDirectory() {
    return directorySelectionStrategy.getBaseDirectoryPath();
  }

  @Deprecated
  public void setBaseDirectory(String baseDirectory) {
    LOG.warn(String.format(
        "Overwriting the product files download base directory path to [%s]. Must be set directly on the [%s] configuration",
        baseDirectory, directorySelectionStrategy.getClass().getSimpleName()));
    this.directorySelectionStrategy.setBaseDirectoryPath(baseDirectory);
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setDirectorySelectionStrategy(DownloadProductFilesDirectorySelectionStrategy directorySelectionStrategy) {
    this.directorySelectionStrategy = directorySelectionStrategy;
  }

}
