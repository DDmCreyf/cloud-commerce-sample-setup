package com.mirakl.hybris.core.catalog.populators.impl;

import static java.lang.String.format;
import static java.util.Collections.emptySet;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklCatalogService;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ExportCatalogContextPopulator
    implements Populator<Pair<MiraklExportCatalogConfig, ExportCatalogWriter>, MiraklExportCatalogContext> {

  private static final Logger LOG = Logger.getLogger(ExportCatalogContextPopulator.class);

  protected MiraklCatalogService miraklCatalogService;

  @Override
  public void populate(Pair<MiraklExportCatalogConfig, ExportCatalogWriter> source, MiraklExportCatalogContext target)
      throws ConversionException {
    MiraklExportCatalogConfig config = source.getLeft();
    target.setExportConfig(config);
    target.setMiraklAttributeCodes(getAttributeCodesFromMirakl(config));
    target.setMiraklCategoryCodes(getCategoryCodesFromMirakl(config));
    target.setMiraklValueCodes(getValueCodesFromMirakl(config));
    target.setWriter(source.getRight());
    target.setVisitedClassIds(new HashSet<>());
    target.setExportedValueListCodes(new HashSet<>());
  }

  protected Set<String> getCategoryCodesFromMirakl(MiraklExportCatalogConfig exportConfig) {
    if (!exportConfig.isExportCategories()) {
      return emptySet();
    }
    LOG.info("Retrieving Catalog Categories from Mirakl for diff purposes..");
    Set<String> categoryCodes = miraklCatalogService.getMiraklCategoryCodes();
    LOG.info(format("Found [%s] Catalog Categories", categoryCodes.size()));

    return categoryCodes;
  }

  protected Set<Pair<String, String>> getAttributeCodesFromMirakl(MiraklExportCatalogConfig exportConfig) {
    if (!exportConfig.isExportAttributes()) {
      return emptySet();
    }
    LOG.info("Retrieving Attributes from Mirakl for diff purposes..");
    Set<Pair<String, String>> attributes = miraklCatalogService.getMiraklAttributeCodes();
    LOG.info(format("Found [%s] Attributes", attributes.size()));

    return attributes;
  }

  protected Set<Pair<String, String>> getValueCodesFromMirakl(MiraklExportCatalogConfig exportConfig) {
    if (!exportConfig.isExportValueLists()) {
      return emptySet();
    }
    LOG.info("Retrieving Value Lists from Mirakl for diff purposes..");
    Set<Pair<String, String>> valueLists = miraklCatalogService.getMiraklValueCodes();
    LOG.info(format("Found [%s] Values", valueLists.size()));

    return valueLists;
  }

  @Required
  public void setMiraklCatalogService(MiraklCatalogService miraklCatalogService) {
    this.miraklCatalogService = miraklCatalogService;
  }
}
