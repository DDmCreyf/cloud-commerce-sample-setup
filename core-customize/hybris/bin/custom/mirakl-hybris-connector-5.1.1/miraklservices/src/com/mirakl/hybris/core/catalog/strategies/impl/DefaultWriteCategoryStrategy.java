package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.WriteCategoryStrategy;
import com.mirakl.hybris.core.enums.MiraklCatalogCategoryExportHeader;

import de.hybris.platform.category.model.CategoryModel;

public class DefaultWriteCategoryStrategy implements WriteCategoryStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultWriteCategoryStrategy.class);

  protected MiraklExportCatalogService exportCatalogService;

  @Override
  public void handleEvent(ExportableCategoryEvent event) {
    MiraklExportCatalogContext context = event.getContext();
    if (context.getExportConfig().isExportCategories()
        && !exportCatalogService.isRootAndIgnoredCategory(event.getCategory(), context)) {
      try {
        writeCategory(event, context);
      } catch (IOException e) {
        LOG.error(format("Unable to export category [%s]", event.getCategory().getCode()), e);
      }
    }
  }

  protected void writeCategory(ExportableCategoryEvent event, MiraklExportCatalogContext context) throws IOException {
    context.getWriter().writeCategory(buildLine(event.getCategory(), context));
    context.removeMiraklCategoryCode(event.getCategory().getCode());
  }

  protected Map<String, String> buildLine(CategoryModel source, MiraklExportCatalogContext context) {
    Map<String, String> target = new HashMap<>();
    target.put(MiraklCatalogCategoryExportHeader.HIERARCHY_CODE.getCode(), source.getCode());
    target.put(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode(),
        source.getName(context.getExportConfig().getDefaultLocale()));
    CategoryModel currentParentCategory = context.getCurrentParentCategory();
    if (currentParentCategory != null) {
      target.put(MiraklCatalogCategoryExportHeader.HIERARCHY_PARENT_CODE.getCode(),
          exportCatalogService.getCategoryExportCode(currentParentCategory, context));
    }

    for (Locale additionalLocale : context.getExportConfig().getAdditionalLocales()) {
      target.put(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode(additionalLocale), source.getName(additionalLocale));
    }

    return target;
  }

  @Required
  public void setExportCatalogService(MiraklExportCatalogService exportCatalogService) {
    this.exportCatalogService = exportCatalogService;
  }
}
