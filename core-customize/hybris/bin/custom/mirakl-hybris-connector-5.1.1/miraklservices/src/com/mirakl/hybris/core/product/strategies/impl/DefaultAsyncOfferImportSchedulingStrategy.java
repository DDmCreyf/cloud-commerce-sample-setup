package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklOffersExportAsyncTrackingResult;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.offer.async.export.MiraklOffersExportAsyncRequest;
import com.mirakl.client.request.common.MiraklContentType;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractAsyncTaskSchedulingStrategy;
import com.mirakl.hybris.core.model.MiraklImportOffersCronJobModel;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import org.springframework.beans.factory.annotation.Required;

public class DefaultAsyncOfferImportSchedulingStrategy extends AbstractAsyncTaskSchedulingStrategy<MiraklImportOffersCronJobModel> {

  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode;

  @Override
  protected MiraklAsyncTaskType getAsyncTaskType(MiraklImportOffersCronJobModel cronjob) {
    return cronjob.isFullImport() ? MiraklAsyncTaskType.OFFER_IMPORT_FULL : MiraklAsyncTaskType.OFFER_IMPORT_DELTA;
  }

  @Override
  protected String createAsyncTaskInMirakl(MiraklImportOffersCronJobModel cronjob) {
    MiraklOffersExportAsyncRequest request = new MiraklOffersExportAsyncRequest();
    request.setLastRequestDate(cronjob.isFullImport() ? null : cronjob.getLastImportTime());
    request.setExportType(MiraklContentType.JSON);
    if (cronjob.isFullImport()) {
      request.setIncludeInactiveOffers(cronjob.isIncludeInactiveOffers());
    }
    //Exclude customer specific prices from the import if the offer price data is retrieved in real-time from Mirakl
    request.setExcludeCustomerSpecificPrices(realtimeOfferPriceRetrievalMode.isActive());

    MiraklOffersExportAsyncTrackingResult response = mmpApi.createOffersExportAsync(request);
    return response.getTrackingId();
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
  }

  @Required
  public void setRealtimeOfferPriceRetrievalMode(MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode) {
    this.realtimeOfferPriceRetrievalMode = realtimeOfferPriceRetrievalMode;
  }
}
