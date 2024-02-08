package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.core.model.product.ProductModel;

public interface CoreAttributeOwnerStrategy {

  /**
   * Determines the product owner of the given {@link MiraklCoreAttributeModel}
   *
   * @param coreAttribute the core attribute from which the product will be deduced
   * @param data the product related data
   * @param context the product import file context data
   * @return the {@link ProductModel} owning the {@link MiraklCoreAttributeModel}
   */
  ProductModel determineOwner(MiraklCoreAttributeModel coreAttribute, ProductImportData data,
      ProductImportFileContextData context);

}
