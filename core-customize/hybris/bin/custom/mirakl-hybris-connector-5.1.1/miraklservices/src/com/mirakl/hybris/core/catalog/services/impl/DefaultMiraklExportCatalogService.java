package com.mirakl.hybris.core.catalog.services.impl;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.attribute.MiraklAttributeImportRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportStatusRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportStatusRequest;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.beans.MiraklExportCatalogResultData;
import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.DeleteCatalogEntriesStrategy;
import com.mirakl.hybris.core.catalog.strategies.ExportCoreAttributesStrategy;
import com.mirakl.hybris.core.catalog.strategies.PrepareCatalogExportStrategy;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;

public class DefaultMiraklExportCatalogService implements MiraklExportCatalogService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklExportCatalogService.class);
  protected static final String DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN = "%s [%s]";

  protected EventService eventService;
  protected ExportCoreAttributesStrategy exportCoreAttributesStrategy;
  protected PrepareCatalogExportStrategy prepareCatalogExportStrategy;
  protected DeleteCatalogEntriesStrategy deleteCatalogEntriesStrategy;
  protected MiraklCatalogIntegrationFrontApi mciApi;
  protected Converter<MiraklExportCatalogContext, MiraklExportCatalogContext> contextDuplicatorConverter;
  protected ConfigurationService configurationService;

  @Override
  public MiraklExportCatalogResultData export(MiraklExportCatalogContext context) throws IOException {
    prepareExport(context);
    lookupExportableCategories(context.getExportConfig().getRootCategory(), context);
    exportCoreAttributes(context);
    deleteOldCatalogEntries(context);
    return sendFilesToMirakl(context);
  }

  @Override
  public String formatAttributeExportName(String value, Locale locale) {
    if (locale == null || value == null) {
      return value;
    }
    String columnFormat = configurationService.getConfiguration()
        .getString(MiraklservicesConstants.CATALOG_EXPORT_LOCALIZED_ATTRIBUTE_PATTERN, DEFAULT_LOCALIZED_ATTRIBUTE_PATTERN);
    return format(columnFormat, value, locale.getLanguage());
  }

  @Override
  public boolean isRootAndIgnoredCategory(CategoryModel category, MiraklExportCatalogContext context) {
    return category.equals(context.getExportConfig().getRootCategory()) && context.getExportConfig().isExcludeRootCategory();
  }

  @Override
  public String getCategoryExportCode(CategoryModel currentCategory, MiraklExportCatalogContext context) {
    return isRootAndIgnoredCategory(currentCategory, context) ? "" : currentCategory.getCode();
  }

  protected void prepareExport(MiraklExportCatalogContext context) throws IOException {
    prepareCatalogExportStrategy.prepareExport(context);
  }

  protected void lookupExportableCategories(CategoryModel currentCategory, MiraklExportCatalogContext context)
      throws IOException {
    if (!currentCategory.isOperatorExclusive()) {
      eventService.publishEvent(new ExportableCategoryEvent(currentCategory, context));
      for (CategoryModel subCategory : currentCategory.getCategories()) {
        MiraklExportCatalogContext contextCopy = contextDuplicatorConverter.convert(context);
        contextCopy.setCurrentParentCategory(currentCategory);
        lookupExportableCategories(subCategory, contextCopy);
      }
    }
  }

  protected void exportCoreAttributes(MiraklExportCatalogContext context) {
    exportCoreAttributesStrategy.exportCoreAttributes(context);
  }

  protected void deleteOldCatalogEntries(MiraklExportCatalogContext context) {
    deleteCatalogEntriesStrategy.writeRemovedCategories(context);
    deleteCatalogEntriesStrategy.writeRemovedAttributes(context);
    deleteCatalogEntriesStrategy.writeRemovedValues(context);
  }

  protected MiraklExportCatalogResultData sendFilesToMirakl(MiraklExportCatalogContext context) throws IOException {
    MiraklExportCatalogResultData trackingIds = new MiraklExportCatalogResultData();
    if (context.getExportConfig().isDryRunMode()) {
      LOG.info("Dry Run Mode enabled. Export files will not be sent to Mirakl.");
      return trackingIds;
    }
    sendValueLists(context, trackingIds);
    sendCategories(context, trackingIds);
    sendAttributes(context, trackingIds);

    return trackingIds;
  }

  protected void sendValueLists(MiraklExportCatalogContext context, MiraklExportCatalogResultData trackingIds)
      throws IOException {
    if (context.getExportConfig().isExportValueLists()) {
      LOG.info("Sending Value Lists to Mirakl..");
      MiraklValueListImportRequest valueListImportRequest =
          new MiraklValueListImportRequest(context.getWriter().getValueListsFile());
      trackingIds.setMiraklValueListImportTracking(mciApi.importValueLists(valueListImportRequest).getImportId());
    }
  }

  protected void sendCategories(MiraklExportCatalogContext context, MiraklExportCatalogResultData trackingIds)
      throws IOException {
    if (context.getExportConfig().isExportCategories()) {
      LOG.info("Sending Catalog Categories to Mirakl..");
      MiraklHierarchyImportRequest hierarchyImportRequest =
          new MiraklHierarchyImportRequest(context.getWriter().getCategoriesFile());
      trackingIds.setMiraklCatalogCategoryImportTracking(mciApi.importHierarchies(hierarchyImportRequest).getImportId());
    }
  }

  protected void sendAttributes(MiraklExportCatalogContext context, MiraklExportCatalogResultData trackingIds)
      throws IOException {
    MiraklExportCatalogConfig config = context.getExportConfig();
    if (config.isExportAttributes()) {
      waitForMirakl(trackingIds, config.getImportTimeout(), config.getImportCheckInterval());
      LOG.info("Sending Attributes to Mirakl..");
      MiraklAttributeImportRequest attributeImportRequest =
          new MiraklAttributeImportRequest(context.getWriter().getAttributesFile());
      trackingIds.setMiraklAttributeImportTracking(mciApi.importAttributes(attributeImportRequest).getImportId());
    }
  }

  protected void waitForMirakl(MiraklExportCatalogResultData trackingIds, int importTimeout, int importCheckInterval) {
    DateTime uploadDeadline = DateTime.now().plusSeconds(importTimeout);
    while (uploadDeadline.isAfterNow()) {
      if (areHierarchiesImported(trackingIds.getMiraklCatalogCategoryImportTracking())
          && areValueListsImported(trackingIds.getMiraklValueListImportTracking())) {
        return;
      }
      try {
        LOG.info("Value Lists and/or Catalog Categories exports not finished yet.");
        LOG.info(format("Waiting [%s seconds] before checking again..", importCheckInterval));
        TimeUnit.SECONDS.sleep(importCheckInterval);
      } catch (InterruptedException e) {
        LOG.error("Interrupted thread during import timeout", e);
        Thread.currentThread().interrupt();
      }
    }
    LOG.warn("Import timeout duration exceeded");
  }

  protected boolean areValueListsImported(String valueListImportTrackingId) {
    return valueListImportTrackingId == null
        || mciApi.getValueListImportResult(new MiraklValueListImportStatusRequest(valueListImportTrackingId))
            .getImportStatus() != MiraklProcessTrackingStatus.WAITING;
  }

  protected boolean areHierarchiesImported(String hierarchyImportTrackingId) {
    return hierarchyImportTrackingId == null
        || mciApi.getHierarchyImportResult(new MiraklHierarchyImportStatusRequest(hierarchyImportTrackingId))
            .getImportStatus() != MiraklProcessTrackingStatus.WAITING;
  }

  @Required
  public void setEventService(EventService eventService) {
    this.eventService = eventService;
  }

  @Required
  public void setExportCoreAttributesStrategy(ExportCoreAttributesStrategy exportCoreAttributesStrategy) {
    this.exportCoreAttributesStrategy = exportCoreAttributesStrategy;
  }

  @Required
  public void setPrepareCatalogExportStrategy(PrepareCatalogExportStrategy prepareCatalogExportStrategy) {
    this.prepareCatalogExportStrategy = prepareCatalogExportStrategy;
  }

  @Required
  public void setDeleteCatalogEntriesStrategy(DeleteCatalogEntriesStrategy deleteCatalogEntriesStrategy) {
    this.deleteCatalogEntriesStrategy = deleteCatalogEntriesStrategy;
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }

  @Required
  public void setContextDuplicatorConverter(
      Converter<MiraklExportCatalogContext, MiraklExportCatalogContext> contextDuplicatorConverter) {
    this.contextDuplicatorConverter = contextDuplicatorConverter;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
