package com.mirakl.hybris.core.product.strategies.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;
import com.mirakl.hybris.core.product.services.McmProductFileDownloadService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMcmDownloadProductFilesStrategy extends AbstractDownloadProductFilesStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMcmDownloadProductFilesStrategy.class);

  protected McmProductFileDownloadService productFileDownloadService;
  protected Converter<MiraklDownloadProductFilesCronjobModel, ProductDataSheetDownloadParams> productDataSheetDownloadParamsConverter;

  @Override
  public PerformResult perform(MiraklDownloadProductFilesCronjobModel cronJob) {
    ProductDataSheetDownloadParams params = productDataSheetDownloadParamsConverter.convert(cronJob);
    if (productFileDownloadService.downloadProductDataSheetsFile(params)) {
      cronJob.setLastExecutionDate(cronJob.getStartTime());
      modelService.save(cronJob);
      return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
    LOG.error("The product file import job did not execute successfully");
    return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
  }

  @Required
  public void setProductFileDownloadService(McmProductFileDownloadService productFileDownloadService) {
    this.productFileDownloadService = productFileDownloadService;
  }

  @Required
  public void setProductDataSheetDownloadParamsConverter(
      Converter<MiraklDownloadProductFilesCronjobModel, ProductDataSheetDownloadParams> productDataSheetDownloadParamsConverter) {
    this.productDataSheetDownloadParamsConverter = productDataSheetDownloadParamsConverter;
  }

}
