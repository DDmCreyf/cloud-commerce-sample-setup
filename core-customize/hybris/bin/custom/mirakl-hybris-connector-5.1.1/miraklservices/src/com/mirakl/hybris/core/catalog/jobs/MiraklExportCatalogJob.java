package com.mirakl.hybris.core.catalog.jobs;

import static com.mirakl.hybris.core.enums.MiraklExportType.*;
import static java.lang.String.format;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.beans.MiraklExportCatalogResultData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.PostProcessExportCatalogStrategy;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklExportCatalogJob extends AbstractJobPerformable<MiraklExportCatalogCronJobModel> implements BeanFactoryAware {

  private static final Logger LOG = Logger.getLogger(MiraklExportCatalogJob.class);

  protected MiraklExportCatalogService miraklExportCatalogService;
  protected PostProcessExportCatalogStrategy postProcessExportCatalogStrategy;
  protected Converter<MiraklExportCatalogCronJobModel, MiraklExportCatalogConfig> exportConfigConverter;
  protected Converter<Pair<MiraklExportCatalogConfig, ExportCatalogWriter>, MiraklExportCatalogContext> exportCatalogContextConverter;
  protected ExportJobReportService exportJobReportService;
  protected BeanFactory beanFactory;

  @Override
  public PerformResult perform(MiraklExportCatalogCronJobModel cronJob) {
    LOG.info("Started Catalog Export..");

    MiraklExportCatalogConfig exportConfig = exportConfigConverter.convert(cronJob);
    LOG.info(format("Export configuration: [%s]", reflectionToString(exportConfig)));

    try (ExportCatalogWriter writer = (ExportCatalogWriter) beanFactory.getBean("exportCatalogWriter", exportConfig)) {
      MiraklExportCatalogContext context = exportCatalogContextConverter.convert(new ImmutablePair<>(exportConfig, writer));
      MiraklExportCatalogResultData trackingIds = miraklExportCatalogService.export(context);
      createJobReports(trackingIds);
      postProcessExportCatalogStrategy.postProcess(cronJob, context);
    } catch (Exception e) {
      LOG.error("An error occurred during the catalog export", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    LOG.info("Finished Catalog Export.");
    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected void createJobReports(MiraklExportCatalogResultData trackingIds) {
    if (trackingIds.getMiraklValueListImportTracking() != null) {
      exportJobReportService.createMiraklJobReport(trackingIds.getMiraklValueListImportTracking(), VALUE_LIST_EXPORT);
    }
    if (trackingIds.getMiraklCatalogCategoryImportTracking() != null) {
      exportJobReportService.createMiraklJobReport(trackingIds.getMiraklCatalogCategoryImportTracking(), CATALOG_CATEGORY_EXPORT);
    }
    if (trackingIds.getMiraklAttributeImportTracking() != null) {
      exportJobReportService.createMiraklJobReport(trackingIds.getMiraklAttributeImportTracking(), ATTRIBUTE_EXPORT);
    }
  }

  @Required
  public void setMiraklExportCatalogService(MiraklExportCatalogService miraklExportCatalogService) {
    this.miraklExportCatalogService = miraklExportCatalogService;
  }

  @Required
  public void setExportConfigConverter(
      Converter<MiraklExportCatalogCronJobModel, MiraklExportCatalogConfig> exportConfigConverter) {
    this.exportConfigConverter = exportConfigConverter;
  }

  @Required
  public void setPostProcessExportCatalogStrategy(PostProcessExportCatalogStrategy postProcessExportCatalogStrategy) {
    this.postProcessExportCatalogStrategy = postProcessExportCatalogStrategy;
  }

  @Required
  public void setExportJobReportService(ExportJobReportService exportJobReportService) {
    this.exportJobReportService = exportJobReportService;
  }

  @Required
  public void setExportCatalogContextConverter(Converter<Pair<MiraklExportCatalogConfig, ExportCatalogWriter>, MiraklExportCatalogContext> exportCatalogContextConverter) {
    this.exportCatalogContextConverter = exportCatalogContextConverter;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }
}
