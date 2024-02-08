package com.mirakl.hybris.core.catalog.populators.impl;

import java.util.HashSet;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ExportCatalogContextCopyPopulator implements Populator<MiraklExportCatalogContext, MiraklExportCatalogContext> {

  @Override
  public void populate(MiraklExportCatalogContext source, MiraklExportCatalogContext copy) throws ConversionException {
    copy.setAdditionalData(source.getAdditionalData());
    copy.setWriter(source.getWriter());
    copy.setExportConfig(source.getExportConfig());
    copy.setMiraklAttributeCodes(source.getMiraklAttributeCodes());
    copy.setMiraklCategoryCodes(source.getMiraklCategoryCodes());
    copy.setMiraklValueCodes(source.getMiraklValueCodes());
    copy.setVisitedClassIds(new HashSet<>(source.getVisitedClassIds()));
    copy.setExportedValueListCodes(source.getExportedValueListCodes());
  }
}
