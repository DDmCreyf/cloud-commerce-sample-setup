package com.mirakl.hybris.core.product.strategies;

public interface ProductExportAttributeValueFormattingStrategy<SOURCE, TARGET> {

  /**
   * Formats a value in the format required by the product export
   * 
   * @param source value to format
   * @return formatted value to export
   */
  TARGET formatValueForExport(SOURCE source);
}
