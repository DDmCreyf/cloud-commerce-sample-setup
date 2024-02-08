package com.mirakl.hybris.core.product.jobs;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.jobs.AbstractJobPerformableWithStrategies;
import com.mirakl.hybris.core.jobs.strategies.ExportProductsCatalogResolutionStrategy;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.enumeration.EnumerationService;

public class MiraklExportSellableProductsJob
    extends AbstractJobPerformableWithStrategies<MiraklCatalogSystem, MiraklExportSellableProductsCronJobModel> {

  protected EnumerationService enumerationService;
  protected List<ExportProductsCatalogResolutionStrategy> catalogResolutionStrategies;

  @Override
  protected MiraklCatalogSystem getStrategyKey(MiraklExportSellableProductsCronJobModel cronJob) {
    MiraklCatalogSystem miraklCatalogSystem = resolveCatalogSystem(cronJob);
    if (miraklCatalogSystem == null) {
      throw new IllegalStateException(format(
          "Unable to resolve the Mirakl Catalog System to use for cronjob [%s]. Please configure it at your Hybris Catalog level.",
          cronJob.getCode()));
    }

    return miraklCatalogSystem;
  }

  protected MiraklCatalogSystem resolveCatalogSystem(MiraklExportSellableProductsCronJobModel cronJob) {
    List<MiraklCatalogSystem> enumerationValues = enumerationService.getEnumerationValues(MiraklCatalogSystem.class);

    if (isEmpty(enumerationValues)) {
      throw new IllegalStateException("No Mirakl Catalog System defined.");
    } else if (enumerationValues.size() == 1) {
      return enumerationValues.get(0);
    }

    for (ExportProductsCatalogResolutionStrategy resolutionStrategy : catalogResolutionStrategies) {
      CatalogModel catalog = resolutionStrategy.resolveCatalog(cronJob);
      if (catalog != null) {
        return catalog.getMiraklCatalogSystem();
      }
    }

    throw new IllegalStateException(format("Unable to resolve the catalog to use for the cronjob [%s]", cronJob.getCode()));
  }

  @Required
  public void setCatalogResolutionStrategies(List<ExportProductsCatalogResolutionStrategy> catalogResolutionStrategies) {
    this.catalogResolutionStrategies = catalogResolutionStrategies;
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }



}
