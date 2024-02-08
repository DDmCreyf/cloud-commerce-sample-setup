package com.mirakl.hybris.core.catalog.attributes;

import java.util.Locale;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.core.model.product.ProductModel;

public interface McmCoreAttributeHandler<T extends MiraklCoreAttributeModel> extends CoreAttributeHandler<T> {

  /**
   * Returns an unlocalized product attribute value.
   *
   * @param product exported product
   * @param coreAttribute
   * @param context product export context
   * @return the attribute value
   */
  String getValue(ProductModel product, T coreAttribute, ProductDataSheetExportContextData context);

  /**
   * Returns an localized product attribute value
   * 
   * @param product exported product
   * @param coreAttribute
   * @param locale locale in which the value is retrieved
   * @param context product export context
   * @return a localized value
   */
  String getValue(ProductModel product, T coreAttribute, Locale locale, ProductDataSheetExportContextData context);


}
