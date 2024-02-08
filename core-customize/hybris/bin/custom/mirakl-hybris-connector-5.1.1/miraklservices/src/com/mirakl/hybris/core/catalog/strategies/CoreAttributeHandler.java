package com.mirakl.hybris.core.catalog.strategies;

import java.util.List;
import java.util.Map;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface CoreAttributeHandler<T extends MiraklCoreAttributeModel> {

  /**
   * Returns attribute values. Used by VL01 to export value lists.
   * 
   * @param coreAttribute The core attribute
   * @param context the export context
   * @return a list of maps, each of them containing the column name and its value
   */
  List<Map<String, String>> getValues(T coreAttribute, MiraklExportCatalogContext context);


  /**
   * Imports a value for a given core attribute
   * 
   * @param receivedValue the received attribute value
   * @param data The product related data
   * @param context The file import context
   * @throws ProductImportException if a problem is encountered during value set
   */
  void setValue(AttributeValueData receivedValue, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException;


}
