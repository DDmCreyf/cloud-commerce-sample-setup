package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklCleanupRawProductsCronjobModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

import de.hybris.platform.jobs.maintenance.MaintenanceCleanupStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMaintenanceCleanupRawProductsStrategy
    implements MaintenanceCleanupStrategy<MiraklRawProductModel, MiraklCleanupRawProductsCronjobModel> {

  private static final Logger LOG = Logger.getLogger(DefaultMaintenanceCleanupRawProductsStrategy.class);

  protected static final String RAW_PRODUCTS_TO_CLEAN_BEFORE_DATE_QUERY =
      "SELECT {mrp:" + MiraklRawProductModel.PK + "} FROM {" + MiraklRawProductModel._TYPECODE + " AS mrp} WHERE {mrp:"
          + MiraklRawProductModel.CREATIONTIME + "} <= ?" + MiraklRawProductModel.CREATIONTIME;

  protected static final String ALL_RAW_PRODUCTS_QUERY =
      "SELECT {mrp:" + MiraklRawProductModel.PK + "} FROM {" + MiraklRawProductModel._TYPECODE + " AS mrp}";

  protected ModelService modelService;

  @Override
  public FlexibleSearchQuery createFetchQuery(MiraklCleanupRawProductsCronjobModel job) {

    if (job.getDaysBeforeDeletion() == null) {
      LOG.info("No days specified. Cleaning up all Raw Products");
      return new FlexibleSearchQuery(ALL_RAW_PRODUCTS_QUERY);
    }

    Date cleanupLimitDate = new LocalDate().minusDays(job.getDaysBeforeDeletion()).toDate();
    LOG.info(format("Cleaning up all Raw Products created before [%s]", cleanupLimitDate));
    return new FlexibleSearchQuery(RAW_PRODUCTS_TO_CLEAN_BEFORE_DATE_QUERY,
        singletonMap(MiraklRawProductModel.CREATIONTIME, cleanupLimitDate));

  }

  @Override
  public void process(List<MiraklRawProductModel> elements) {
    modelService.removeAll(elements);

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Removed [%s] Raw Products", elements.size()));
    }
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
