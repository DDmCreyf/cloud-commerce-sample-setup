package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

public interface CoreAttributeHandlerResolver {

  /**
   * Determines the spring bean handling the import or the export of the given {@link MiraklCoreAttributeModel}
   * 
   * @param attribute
   * @param data
   * @param context
   * @return
   */
  <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel attribute,
      ProductImportData data, ProductImportFileContextData context);

  /**
   * Determines the spring bean handling the import or the export of the given {@link MiraklCoreAttributeModel}
   * 
   * @param attribute
   * @param context
   * @return
   */
  <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel attribute,
      MiraklExportCatalogContext context);

  /**
   * Determines the spring bean handling the import or the export of the given {@link MiraklCoreAttributeModel}
   * 
   * @param coreAttribute the {@link MiraklCoreAttributeModel} to get the handler from
   * @param miraklCatalogSystem
   * @return
   */
  <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel coreAttribute,
      MiraklCatalogSystem miraklCatalogSystem);
}
