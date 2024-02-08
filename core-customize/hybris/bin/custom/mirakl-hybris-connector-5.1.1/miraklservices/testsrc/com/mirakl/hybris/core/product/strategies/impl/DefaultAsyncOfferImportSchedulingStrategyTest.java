package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklOffersExportAsyncTrackingResult;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.offer.async.export.MiraklOffersExportAsyncRequest;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskType;
import com.mirakl.hybris.core.model.MiraklImportOffersCronJobModel;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAsyncOfferImportSchedulingStrategyTest {

    @InjectMocks
    private DefaultAsyncOfferImportSchedulingStrategy strategy;

    @Mock
    private MiraklMarketplacePlatformFrontApi mmpApi;

    @Mock
    private MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode;

    private static final String ASYNC_OFFER_EXPORT_TRACKING_ID = "1234-5679";
    private MiraklOffersExportAsyncTrackingResult offersExportAsyncTrackingResult;
    
    @Before
    public void setUp() {
        offersExportAsyncTrackingResult = new MiraklOffersExportAsyncTrackingResult();
        offersExportAsyncTrackingResult.setTrackingId(ASYNC_OFFER_EXPORT_TRACKING_ID);
    }

    @Test
    public void testGetAsyncTaskTypeFullImport() {
        MiraklImportOffersCronJobModel cronJob = new MiraklImportOffersCronJobModel();
        cronJob.setFullImport(true);

        MiraklAsyncTaskType result = strategy.getAsyncTaskType(cronJob);

        assertEquals(MiraklAsyncTaskType.OFFER_IMPORT_FULL, result);
    }

    @Test
    public void testGetAsyncTaskTypeDeltaImport() {
        MiraklImportOffersCronJobModel cronJob = new MiraklImportOffersCronJobModel();
        cronJob.setFullImport(false);

        MiraklAsyncTaskType result = strategy.getAsyncTaskType(cronJob);

        assertEquals(MiraklAsyncTaskType.OFFER_IMPORT_DELTA, result);
    }

    @Test
    public void testCreateAsyncTaskInMiraklFullImport() {
        MiraklImportOffersCronJobModel cronJob = new MiraklImportOffersCronJobModel();
        cronJob.setFullImport(true);
        cronJob.setIncludeInactiveOffers(true);

        when(realtimeOfferPriceRetrievalMode.isActive()).thenReturn(false);

        when(mmpApi.createOffersExportAsync(any(MiraklOffersExportAsyncRequest.class)))
                .thenReturn(offersExportAsyncTrackingResult);

        String trackingId = strategy.createAsyncTaskInMirakl(cronJob);

        assertNotNull(trackingId);
        verify(mmpApi).createOffersExportAsync(any(MiraklOffersExportAsyncRequest.class));
        assertFalse(trackingId.isEmpty());
    }

    @Test
    public void testCreateAsyncTaskInMiraklDeltaImport() {
        MiraklImportOffersCronJobModel cronJob = new MiraklImportOffersCronJobModel();
        cronJob.setFullImport(false);
        Date tenDaysAgo = DateUtils.addDays(new Date(), -10);
        cronJob.setLastImportTime(tenDaysAgo);

        when(realtimeOfferPriceRetrievalMode.isActive()).thenReturn(true);

        when(mmpApi.createOffersExportAsync(any(MiraklOffersExportAsyncRequest.class)))
                .thenReturn(offersExportAsyncTrackingResult);

        String trackingId = strategy.createAsyncTaskInMirakl(cronJob);

        assertNotNull(trackingId);
        verify(mmpApi).createOffersExportAsync(any(MiraklOffersExportAsyncRequest.class));
        assertFalse(trackingId.isEmpty());
        verify(realtimeOfferPriceRetrievalMode).isActive();
    }

    @Test
    public void testSetMmpApi() {
        MiraklMarketplacePlatformFrontApi mockApi = mock(MiraklMarketplacePlatformFrontApi.class);
        strategy.setMmpApi(mockApi);

        assertSame(mockApi, strategy.mmpApi);
    }
}
