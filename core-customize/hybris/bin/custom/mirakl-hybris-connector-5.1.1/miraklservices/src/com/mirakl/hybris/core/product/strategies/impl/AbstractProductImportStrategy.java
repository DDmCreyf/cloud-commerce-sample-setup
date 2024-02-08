package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.PRODUCTS_IMPORT_MAX_DURATION;
import static com.mirakl.hybris.core.util.CronJobUtils.archiveFile;
import static java.lang.Double.valueOf;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StopWatch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mirakl.hybris.beans.ProductImportErrorData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;
import com.mirakl.hybris.core.product.services.MiraklRawProductImportService;
import com.mirakl.hybris.core.product.services.MiraklRawProductService;
import com.mirakl.hybris.core.product.services.ProductImportService;
import com.mirakl.hybris.core.product.strategies.CleanupRawProductsStrategy;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;
import com.mirakl.hybris.core.product.strategies.ProductImportResultHandler;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

public abstract class AbstractProductImportStrategy implements PerformJobStrategy<MiraklProductImportCronJobModel> {

  private static final Logger LOG = Logger.getLogger(AbstractProductImportStrategy.class);

  protected ProductImportService productImportService;
  protected MiraklRawProductImportService rawProductImportService;
  protected CatalogVersionService catalogVersionService;
  protected UserService userService;
  protected CommonI18NService commonI18NService;
  protected MiraklRawProductService rawProductService;
  protected ConfigurationService configurationService;
  protected DefaultPostProcessProductFileImportStrategy postProcessProductFileImportStrategy;
  protected ProductImportResultHandler productImportResultHandler;
  protected Converter<MiraklProductImportCronJobModel, ProductImportGlobalContextData> globalContextConverter;
  protected Converter<Pair<ProductImportGlobalContextData, File>, ProductImportFileContextData> fileContextConverter;
  protected CleanupRawProductsStrategy cleanupRawProductsStrategy;
  protected ModelService modelService;
  protected TenantAwareThreadFactory tenantAwareThreadFactory;
  private String baseDirectory;

  @Override
  public PerformResult perform(MiraklProductImportCronJobModel cronJob) {
    LOG.info("Started a product import..");
    final StopWatch productImportWatch = new StopWatch();
    productImportWatch.start();

    final File[] inputFiles = getInputFiles(cronJob);
    if (isEmpty(inputFiles)) {
      LOG.info("No files pending to be imported. Finished product import.");
      return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    final ProductImportGlobalContextData globalContext = globalContextConverter.convert(cronJob);

    for (final File inputFile : inputFiles) {
      ExecutorService serviceExecutor = newFixedThreadPool(globalContext.getNumberOfWorkers(), getThreadFactory(cronJob));
      final CountDownLatch resultHandlerLatch = new CountDownLatch(1);
      ProductImportFileContextData context = null;
      String importId = null;

      try {
        context = fileContextConverter.convert(Pair.of(globalContext, inputFile));

        getResultHandlerThread(resultHandlerLatch, context).start();

        LOG.info(format("Number of threads: [%s]", globalContext.getNumberOfWorkers()));

        LOG.info(format("Processing input file [%s]", inputFile));

        LOG.info(format("Started saving raw products from file [%s]", inputFile));
        importId = rawProductImportService.importRawProducts(inputFile, context);
        LOG.info(format("Finished saving raw products from file [%s]. Import Id: [%s]", inputFile, importId));

        LOG.info(format("Started importing variants from file [%s] (Import Id [%s])", inputFile, importId));
        importProductsWithVariants(context, serviceExecutor, importId);

        LOG.info(format("Started importing products with no variant info from file [%s] (Import Id [%s])", inputFile, importId));
        importProductsWithNoVariants(context, serviceExecutor, importId);

        archiveFile(cronJob.getArchiveDirectory(), getBaseDirectory(), inputFile);

      } catch (Exception e) {
        handleFileImportError(inputFile, e);
      } finally {
        shutdownWorkers(serviceExecutor, resultHandlerLatch, context);
        if (importId != null) {
          postProcessProductFileImport(context, importId);
          cleanupRawProducts(cronJob, importId);
        }
      }
    }

    productImportWatch.stop();
    LOG.info(format("Finished product import. Total time = %f seconds.", valueOf(productImportWatch.getTotalTimeSeconds())));

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected abstract File[] getInputFiles(MiraklProductImportCronJobModel cronJob);

  protected abstract void importProductsWithVariants(ProductImportFileContextData context, ExecutorService serviceExecutor,
      String importId);

  protected abstract void importProductsWithNoVariants(ProductImportFileContextData context, ExecutorService serviceExecutor,
      String importId);

  protected Thread getResultHandlerThread(final CountDownLatch countDownLatch, final ProductImportFileContextData context) {
    Thread thread = tenantAwareThreadFactory.newThread(new Runnable() {

      @Override
      public void run() {
        try {
          productImportResultHandler.handleImportResults(context);
        } finally {
          countDownLatch.countDown();
        }
      }
    });
    thread.setName("Mirakl Product Import - Result handler");
    return thread;
  }

  protected void shutdownWorkers(ExecutorService serviceExecutor, final CountDownLatch resultHandlerLatch,
      ProductImportFileContextData context) {
    serviceExecutor.shutdown();
    try {
      serviceExecutor.awaitTermination(configurationService.getConfiguration().getInt(PRODUCTS_IMPORT_MAX_DURATION), MINUTES);
    } catch (InterruptedException e) {
      LOG.warn("Interruption occurred while waiting for the workers to finish", e);
      serviceExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }

    if (context != null) {
      try {
        context.getImportResultQueue().put(buildTerminationSignal());
        resultHandlerLatch.await();
      } catch (InterruptedException e) {
        LOG.warn(format("Interruption occurred while waiting for the result handler to complete processing file [%s]",
            context.getFullFilename()), e);
        Thread.currentThread().interrupt();
      }
    }
  }

  protected void handleFileImportError(File inputFile, Exception e) {
    LOG.error(format("Unable to process input file [%s]", inputFile), e);
  }

  protected ThreadFactory getThreadFactory(final MiraklProductImportCronJobModel cronJob) {
    final PK userPK = cronJob.getSessionUser().getPk();
    final PK languagePK = cronJob.getSessionLanguage().getPk();
    final PK currencyPK = cronJob.getSessionCurrency().getPk();
    final PK classificationSystemPK = cronJob.getSystemVersion().getPk();
    final PK catalogVersionPK = cronJob.getCatalogVersion().getPk();


    TenantAwareThreadFactory threadFactory = new TenantAwareThreadFactory(Registry.getCurrentTenant()) {

      @Override
      protected void afterPrepareThread() {
        userService.setCurrentUser(modelService.<UserModel>get(userPK));
        commonI18NService.setCurrentLanguage(modelService.<LanguageModel>get(languagePK));
        commonI18NService.setCurrentCurrency(modelService.<CurrencyModel>get(currencyPK));
        ClassificationSystemVersionModel classificationSystemVersion = modelService.get(classificationSystemPK);
        CatalogVersionModel catalogVersion = modelService.get(catalogVersionPK);
        catalogVersionService.setSessionCatalogVersions(newArrayList(classificationSystemVersion, catalogVersion));
      }
    };

    return new ThreadFactoryBuilder() //
        .setThreadFactory(threadFactory) //
        .setNameFormat("Mirakl Product Import - %d") //
        .build();
  }

  protected void postProcessProductFileImport(ProductImportFileContextData context, String importId) {
    postProcessProductFileImportStrategy.postProcess(context, importId);
  }

  protected ProductImportErrorData buildTerminationSignal() {
    ProductImportErrorData terminationSignal = new ProductImportErrorData();
    terminationSignal.setTerminationSignal(true);

    return terminationSignal;
  }

  protected void cleanupRawProducts(MiraklProductImportCronJobModel cronJob, String importId) {
    if (cronJob.isCleanupRawProducts()) {
      cleanupRawProductsStrategy.cleanForImport(importId);
    }
  }

  public String getBaseDirectory() {
    return isNotBlank(baseDirectory) ? baseDirectory : configurationService.getConfiguration().getString("HYBRIS_DATA_DIR");
  }

  @Required
  public void setProductImportService(ProductImportService productImportService) {
    this.productImportService = productImportService;
  }

  @Required
  public void setRawProductImportService(MiraklRawProductImportService rawProductImportService) {
    this.rawProductImportService = rawProductImportService;
  }

  @Required
  public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
    this.catalogVersionService = catalogVersionService;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setRawProductService(MiraklRawProductService rawProductService) {
    this.rawProductService = rawProductService;
  }

  @Required
  public void setFileContextConverter(
      Converter<Pair<ProductImportGlobalContextData, File>, ProductImportFileContextData> fileContextConverter) {
    this.fileContextConverter = fileContextConverter;
  }

  @Required
  public void setGlobalContextConverter(
      Converter<MiraklProductImportCronJobModel, ProductImportGlobalContextData> globalContextConverter) {
    this.globalContextConverter = globalContextConverter;
  }

  @Required
  public void setPostProcessProductFileImportStrategy(
      DefaultPostProcessProductFileImportStrategy postProcessProductFileImportStrategy) {
    this.postProcessProductFileImportStrategy = postProcessProductFileImportStrategy;
  }

  @Required
  public void setProductImportResultHandler(ProductImportResultHandler productImportResultHandler) {
    this.productImportResultHandler = productImportResultHandler;
  }

  @Required
  public void setCleanupRawProductsStrategy(CleanupRawProductsStrategy cleanupRawProductsStrategy) {
    this.cleanupRawProductsStrategy = cleanupRawProductsStrategy;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setTenantAwareThreadFactory(TenantAwareThreadFactory tenantAwareThreadFactory) {
    this.tenantAwareThreadFactory = tenantAwareThreadFactory;
  }

  public void setBaseDirectory(String baseDirectory) {
    this.baseDirectory = baseDirectory;
  }
}
