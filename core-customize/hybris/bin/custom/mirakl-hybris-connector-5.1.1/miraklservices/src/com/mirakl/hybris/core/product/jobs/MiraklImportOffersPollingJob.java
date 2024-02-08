package com.mirakl.hybris.core.product.jobs;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mirakl.client.core.internal.MiraklStream;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferStatus;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklPollOffersExportAsyncStatusError;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklPollOffersExportAsyncStatusResult;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.offer.async.export.MiraklOffersExportAsyncFileRequest;
import com.mirakl.client.mmp.request.offer.async.export.MiraklPollOffersExportAsyncRequest;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;
import com.mirakl.hybris.core.model.MiraklAsyncTaskPollerModel;
import com.mirakl.hybris.core.model.MiraklImportOfferPollerModel;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.strategies.AsyncOfferImportStrategy;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

public class MiraklImportOffersPollingJob extends AbstractMiraklAsyncTaskPoller<MiraklImportOfferPollerModel> {

  // Messages
  protected static final String TASK_FAILED_IN_MIRAKL =
      "The async task failed in Mirakl [type=%s, trackingId=%s, miraklErrorCode=%s, miraklErrorDetails='%s']";

  // Beans
  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected UserService userService;
  protected CommonI18NService commonI18NService;
  protected List<AsyncOfferImportStrategy> asyncOfferImportStrategies;
  protected OfferDao offerDao;

  @Override
  protected MiraklAsyncTaskType getAsyncTaskType(MiraklImportOfferPollerModel cronjob) {
    boolean fullImport = cronjob.getSchedulerInstance().isFullImport();
    return fullImport ? MiraklAsyncTaskType.OFFER_IMPORT_FULL : MiraklAsyncTaskType.OFFER_IMPORT_DELTA;
  }

  @Override
  protected MiraklAsyncTaskStatus processTask(MiraklImportOfferPollerModel cronJob, MiraklAsyncTaskModel task) throws Exception {
    MiraklPollOffersExportAsyncRequest request = new MiraklPollOffersExportAsyncRequest(task.getTrackingId());
    MiraklPollOffersExportAsyncStatusResult offerFiles = mmpApi.pollOffersExportAsyncStatus(request);

    if (isStillPending(offerFiles)) {
      return MiraklAsyncTaskStatus.PENDING;
    }

    if (isInError(offerFiles)) {
      MiraklPollOffersExportAsyncStatusError error = offerFiles.getError();
      LOG.error(format(TASK_FAILED_IN_MIRAKL, task.getType(), task.getTrackingId(), error.getCode(), error.getDetail()));
      return MiraklAsyncTaskStatus.ERROR;
    }

    int threadCount = cronJob.getNumberOfWorkers() != null ? cronJob.getNumberOfWorkers() : getRuntime().availableProcessors();
    ExecutorService threadPool = newFixedThreadPool(threadCount, buildThreadFactory(cronJob));
    Semaphore semaphore = new Semaphore(threadCount);

    final Set<String> processedIds = new HashSet<>();

    try {

      if (isNotEmpty(offerFiles.getUrls())) {
        LOG.info(format("Importing offers from %s OF54 files...", offerFiles.getUrls().size()));
        for (String fileUrl : offerFiles.getUrls()) {
          importOffers(fileUrl, threadPool, semaphore, processedIds);
        }
      }

      semaphore.acquire(threadCount);
      LOG.info(format("Processed %s offers", processedIds.size()));

      if (cronJob.getSchedulerInstance().isFullImport()) {
        semaphore.release(threadCount);
        Integer missingOfferCount = flagMissingOffersAsDeleted(cronJob.getStartTime(), processedIds, threadPool, semaphore);
        semaphore.acquire(threadCount);
        LOG.info(format("Deleted %s missing offers...", missingOfferCount));
      }

    } finally {
      threadPool.shutdownNow();
    }

    return MiraklAsyncTaskStatus.DONE;
  }

  protected boolean isStillPending(MiraklPollOffersExportAsyncStatusResult offerFiles) {
    return MiraklAsyncExportOfferStatus.PENDING.equals(offerFiles.getStatus());
  }

  protected boolean isInError(MiraklPollOffersExportAsyncStatusResult offerFiles) {
    return MiraklAsyncExportOfferStatus.FAILED.equals(offerFiles.getStatus());
  }

  protected ThreadFactory buildThreadFactory(final MiraklAsyncTaskPollerModel cronJob) {
    final PK userPK = cronJob.getSessionUser().getPk();
    final PK languagePK = cronJob.getSessionLanguage().getPk();
    final PK currencyPK = cronJob.getSessionCurrency().getPk();

    TenantAwareThreadFactory threadFactory = new TenantAwareThreadFactory(Registry.getCurrentTenant()) {
      @Override
      protected void afterPrepareThread() {
        userService.setCurrentUser(modelService.<UserModel>get(userPK));
        commonI18NService.setCurrentLanguage(modelService.<LanguageModel>get(languagePK));
        commonI18NService.setCurrentCurrency(modelService.<CurrencyModel>get(currencyPK));
      }
    };

    return new ThreadFactoryBuilder() //
        .setThreadFactory(threadFactory) //
        .setNameFormat("Mirakl Offer Import - %d") //
        .build();
  }

  protected void importOffers(String fileUrl, ExecutorService threadPool, Semaphore semaphore, Set<String> processedIds)
      throws IOException, InterruptedException {
    MiraklOffersExportAsyncFileRequest request = new MiraklOffersExportAsyncFileRequest(fileUrl);
    try (MiraklStream<MiraklAsyncExportOffer> offerStream = mmpApi.getOffersExportAsyncStream(request)) {
      for (MiraklAsyncExportOffer offer : offerStream) {
        processedIds.add(offer.getOfferId());
        runImportThread(threadPool, semaphore, offer);
      }
    }
  }

  protected void runImportThread(ExecutorService threadPool, Semaphore semaphore, MiraklAsyncExportOffer offer)
      throws InterruptedException {
    semaphore.acquire();
    threadPool.execute(new Runnable() {
      @Override
      public void run() {
        try {
          for (AsyncOfferImportStrategy asyncOfferImportStrategy : asyncOfferImportStrategies) {
            asyncOfferImportStrategy.importMiraklOffer(offer);
          }
        } finally {
          semaphore.release();
        }
      }
    });
  }

  protected Integer flagMissingOffersAsDeleted(Date startTime, Set<String> importedIds, ExecutorService threadPool,
      Semaphore semaphore) throws InterruptedException {
    List<OfferModel> missingOffers = offerDao.findUndeletedOffersModifiedBeforeDate(startTime);
    int missingOfferCount = 0;
    for (OfferModel offer : missingOffers) {
      if (importedIds.contains(offer.getId())) {
        continue;
      }
      missingOfferCount++;
      runInvalidationThread(threadPool, semaphore, offer);
    }
    return missingOfferCount;
  }

  protected void runInvalidationThread(ExecutorService threadPool, Semaphore semaphore, OfferModel offer)
      throws InterruptedException {
    semaphore.acquire();
    threadPool.execute(new Runnable() {
      @Override
      public void run() {
        try {
          for (AsyncOfferImportStrategy asyncOfferImportStrategy : asyncOfferImportStrategies) {
            asyncOfferImportStrategy.invalidateHybrisOffer(offer);
          }
          LOG.info(format("Offer [%s] set DELETED", offer.getId()));
        } finally {
          semaphore.release();
        }
      }
    });
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
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
  public void setAsyncOfferImportStrategies(List<AsyncOfferImportStrategy> asyncOfferImportStrategies) {
    this.asyncOfferImportStrategies = asyncOfferImportStrategies;
  }

  @Required
  public void setOfferDao(OfferDao offerDao) {
    this.offerDao = offerDao;
  }
}
