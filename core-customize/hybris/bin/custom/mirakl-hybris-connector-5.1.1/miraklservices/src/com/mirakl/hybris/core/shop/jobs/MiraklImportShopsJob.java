package com.mirakl.hybris.core.shop.jobs;

import static java.lang.String.format;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.MiraklImportShopsCronjobModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopImportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

public class MiraklImportShopsJob extends AbstractJobPerformable<MiraklImportShopsCronjobModel> {

  protected ShopImportService shopImportService;

  private static final Logger LOG = Logger.getLogger(MiraklImportShopsJob.class);

  @Override
  public PerformResult perform(MiraklImportShopsCronjobModel cronjobModel) {

    Collection<ShopModel> importedShops;

    if (cronjobModel.isFullImport() || cronjobModel.getLastExecutionDate() == null) {
      LOG.info("Performing a FULL shops import");
      importedShops = shopImportService.importAllShops();
    } else {
      LOG.info(format("Importing shops updated after %s", cronjobModel.getLastExecutionDate()));
      importedShops = shopImportService.importShopsUpdatedSince(cronjobModel.getLastExecutionDate());
    }

    LOG.info(format("Imported %d shops", importedShops.size()));
    cronjobModel.setLastExecutionDate(cronjobModel.getStartTime());

    modelService.save(cronjobModel);

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setShopImportService(ShopImportService shopImportService) {
    this.shopImportService = shopImportService;
  }
}
