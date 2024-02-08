package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.DeleteCatalogEntriesStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklCatalogCategoryExportHeader;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.util.services.CsvService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultDeleteCatalogEntriesStrategy implements DeleteCatalogEntriesStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultDeleteCatalogEntriesStrategy.class);

  private static final String MIRAKL_DELETE_FLAG = "delete";
  private static final String MIRAKL_DELETED_CATEGORY_LABEL = "Deleted category";

  protected CsvService csvService;

  @Override
  public void writeRemovedCategories(MiraklExportCatalogContext context) {
    if (!context.getExportConfig().isExportCategories()) {
      return;
    }
    Set<String> categoriesToBeDeleted = context.getMiraklCategoryCodes();
    LOG.info(format("Found [%s] Categories to delete.", categoriesToBeDeleted.size()));

    for (String categoryCode : categoriesToBeDeleted) {
      Map<String, String> categoryLine = getCategoryLine(categoryCode);
      try {
        context.getWriter().writeCategory(categoryLine);
      } catch (IOException e) {
        LOG.error(format("Impossible to write category for deletion: [%s]", categoryLine), e);
      }
    }
  }

  @Override
  public void writeRemovedAttributes(MiraklExportCatalogContext context) {
    if (!context.getExportConfig().isExportAttributes()) {
      return;
    }
    Set<Pair<String, String>> attributesToBeDeleted = context.getMiraklAttributeCodes();
    LOG.info(format("Found [%s] Attributes to delete.", attributesToBeDeleted.size()));

    for (Pair<String, String> attribute : attributesToBeDeleted) {
      Map<String, String> attributeLine = getAttributeLine(attribute);
      try {
        context.getWriter().writeAttribute(attributeLine);
      } catch (IOException e) {
        LOG.error(format("Impossible to write attribute for deletion: [%s]", attributeLine), e);
      }
    }
  }

  @Override
  public void writeRemovedValues(MiraklExportCatalogContext context) {
    if (!context.getExportConfig().isExportValueLists()) {
      return;
    }
    Set<Pair<String, String>> valueCodesToBeDeleted = context.getMiraklValueCodes();
    LOG.info(format("Found [%s] Values to delete.", valueCodesToBeDeleted.size()));

    for (Pair<String, String> value : valueCodesToBeDeleted) {
      Map<String, String> valueLine = getValueLine(value);
      try {
        context.getWriter().writeAttributeValue(valueLine);
      } catch (IOException e) {
        LOG.error(format("Impossible to write value for deletion: [%s]", valueLine), e);
      }
    }
  }

  protected Map<String, String> getCategoryLine(String categoryCode) {
    Map<String, String> line = new HashMap<>();
    line.put(MiraklCatalogCategoryExportHeader.HIERARCHY_CODE.getCode(), categoryCode);
    line.put(MiraklCatalogCategoryExportHeader.HIERARCHY_LABEL.getCode(), MIRAKL_DELETED_CATEGORY_LABEL);
    line.put(MiraklCatalogCategoryExportHeader.UPDATE_DELETE.getCode(), MIRAKL_DELETE_FLAG);
    return line;
  }

  protected Map<String, String> getAttributeLine(Pair<String, String> attribute) {
    Map<String, String> line = new HashMap<>();
    line.put(MiraklAttributeExportHeader.CODE.getCode(), attribute.getLeft());
    line.put(MiraklAttributeExportHeader.HIERARCHY_CODE.getCode(), attribute.getRight());
    line.put(MiraklAttributeExportHeader.UPDATE_DELETE.getCode(), MIRAKL_DELETE_FLAG);
    return line;
  }

  protected Map<String, String> getValueLine(Pair<String, String> value) {
    Map<String, String> line = new HashMap<>();
    line.put(MiraklValueListExportHeader.VALUE_CODE.getCode(), value.getLeft());
    line.put(MiraklValueListExportHeader.LIST_CODE.getCode(), value.getRight());
    line.put(MiraklValueListExportHeader.UPDATE_DELETE.getCode(), MIRAKL_DELETE_FLAG);
    return line;
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }
}
