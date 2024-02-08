package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;
import com.mirakl.hybris.core.product.services.MciProductFileDownloadService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DefaultMciDownloadProductFilesStrategy extends AbstractDownloadProductFilesStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMciDownloadProductFilesStrategy.class);

  protected MciProductFileDownloadService productFileDownloadService;

  @Override
  public PerformResult perform(MiraklDownloadProductFilesCronjobModel cronJob) {
    List<String> importIds = getImportIds(cronJob);
    File targetDirectory = getTargetDirectory(cronJob);
    int downloadedFileCount = productFileDownloadService.downloadProductFiles(importIds, targetDirectory);
    updateLastExecutionDate(cronJob);
    if (importIds.size() > downloadedFileCount) {
      LOG.error(
          String.format("Download incomplete: [%s/%s] product file(s) were downloaded", downloadedFileCount, importIds.size()));
    } else if (downloadedFileCount > 0) {
      LOG.info(String.format("Downloaded [%s] product file(s) successfully", downloadedFileCount));
    }
    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected List<String> getImportIds(MiraklDownloadProductFilesCronjobModel cronJob) {
    if (isNotEmpty(cronJob.getImportId())) {
      return Collections.singletonList(cronJob.getImportId());
    }
    if (cronJob.isFullDownload()) {
      return productFileDownloadService.getImportIds(null, null, null);
    }
    return productFileDownloadService.getImportIds(cronJob.getLastExecutionDate(), getShopId(cronJob),
        cronJob.getImportStatuses());
  }

  protected String getShopId(MiraklDownloadProductFilesCronjobModel cronJob) {
    return cronJob.getShop() == null ? null : cronJob.getShop().getId();
  }

  protected void updateLastExecutionDate(MiraklDownloadProductFilesCronjobModel cronJob) {
    if (cronJob.getShop() == null && isEmpty(cronJob.getImportId())) {
      cronJob.setLastExecutionDate(cronJob.getStartTime());
      modelService.save(cronJob);
    }
  }

  @Required
  public void setProductFileDownloadService(MciProductFileDownloadService productFileDownloadService) {
    this.productFileDownloadService = productFileDownloadService;
  }


}
