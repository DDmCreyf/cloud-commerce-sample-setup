package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.util.CronJobUtils.fetchInputFiles;
import static com.mirakl.hybris.core.util.CronJobUtils.getOrCreateDirectory;
import static java.util.Collections.singleton;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public class DefaultMcmProductImportStrategy extends AbstractProductImportStrategy {

  @Override
  protected File[] getInputFiles(MiraklProductImportCronJobModel cronJob) {
    return fetchInputFiles(getOrCreateDirectory(cronJob.getInputDirectory(), getBaseDirectory()));
  }

  @Override
  protected void importProductsWithVariants(final ProductImportFileContextData context, ExecutorService serviceExecutor,
      final String importId) {
    for (final String variantGroupCode : rawProductService.getMiraklVariantGroupCodesForImportId(importId)) {
      serviceExecutor.execute(new Runnable() {

        @Override
        public void run() {
          List<MiraklRawProductModel> rawProducts =
              rawProductService.getRawProductsForImportIdAndVariantGroupCode(importId, variantGroupCode);
          productImportService.importProducts(rawProducts, context);
        }
      });
    }
  }

  @Override
  protected void importProductsWithNoVariants(final ProductImportFileContextData context, ExecutorService serviceExecutor,
      final String importId) {
    for (final MiraklRawProductModel rawProduct : rawProductService.getRawProductsWithNoVariantGroupForImportId(importId)) {
      serviceExecutor.execute(new Runnable() {

        @Override
        public void run() {
          productImportService.importProducts(singleton(rawProduct), context);
        }
      });
    }
  }
}
