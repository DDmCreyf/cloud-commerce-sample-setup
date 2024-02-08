package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.lang.time.DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT;

import java.util.Date;

import com.mirakl.hybris.core.product.strategies.ProductExportAttributeValueFormattingStrategy;

public class DefaultProductExportAttributeValueFormattingStrategy
    implements ProductExportAttributeValueFormattingStrategy<Object, String> {

  @Override
  public String formatValueForExport(Object source) {
    if (source == null) {
      return null;
    }
    if (source instanceof Date) {
      return ISO_DATETIME_TIME_ZONE_FORMAT.format(source);
    }
    return String.valueOf(source);
  }

}
