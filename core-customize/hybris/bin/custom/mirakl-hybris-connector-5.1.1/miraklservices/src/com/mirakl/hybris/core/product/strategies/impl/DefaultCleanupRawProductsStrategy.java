package com.mirakl.hybris.core.product.strategies.impl;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.CleanupRawProductsStrategy;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultCleanupRawProductsStrategy implements CleanupRawProductsStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultCleanupRawProductsStrategy.class);

  protected static final String RAW_PRODUCTS_TO_CLEAN_FOR_IMPORT_QUERY =
      "SELECT {mrp:" + MiraklRawProductModel.PK + "} FROM {" + MiraklRawProductModel._TYPECODE + " AS mrp} WHERE {mrp:"
          + MiraklRawProductModel.IMPORTID + "} = ?" + MiraklRawProductModel.IMPORTID;

  protected FlexibleSearchService flexibleSearchService;
  protected ModelService modelService;

  @Override
  public void cleanForImport(String importId) {
    FlexibleSearchQuery rawProductsToCleanQuery = new FlexibleSearchQuery(RAW_PRODUCTS_TO_CLEAN_FOR_IMPORT_QUERY,
        Collections.singletonMap(MiraklRawProductModel.IMPORTID, importId));
    final SearchResult<MiraklRawProductModel> rawProducts = flexibleSearchService.search(rawProductsToCleanQuery);
    LOG.info(String.format("Cleaning up [%s] Mirakl Raw Products (Import Id [%s])", rawProducts.getTotalCount(), importId));
    modelService.removeAll(rawProducts.getResult());
  }

  @Required
  public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
    this.flexibleSearchService = flexibleSearchService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
