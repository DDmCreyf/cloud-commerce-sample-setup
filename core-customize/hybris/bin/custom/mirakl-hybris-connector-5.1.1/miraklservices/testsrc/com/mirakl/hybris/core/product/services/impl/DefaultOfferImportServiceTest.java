package com.mirakl.hybris.core.product.services.impl;

import static com.mirakl.hybris.core.utils.MiraklStreamTestUtils.getMiraklStream;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.core.internal.MiraklStream;
import com.mirakl.client.core.internal.util.DateFormatter;
import com.mirakl.client.mmp.domain.offer.MiraklExportOffer;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.offer.MiraklOffersExportRequest;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.strategies.OfferImportErrorHandler;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferImportServiceTest {

  private static final String LAST_REQUEST_DATE_KEY = "last_request_date";
  private static final String NEW_OFFER_ID = "newOfferId";
  private static final String EXISTING_OFFER_ID = "existingOfferId";
  private static final int SINGLETON_LIST_SIZE = 1;
  private static final String INCLUDE_INACTIVE_OFFERS_QUERY_PARAM = "include_inactive_offers";

  @Spy
  @InjectMocks
  private DefaultOfferImportService testObj = new DefaultOfferImportService();

  @Mock
  private MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  @Mock
  private MiraklExportOffer miraklExportOfferMock;
  @Mock
  private Date lastImportDateMock, jobStartTimeMock;
  @Mock
  private Converter<MiraklExportOffer, OfferModel> offerConverter;
  @Mock
  private ModelService modelServiceMock;
  @Mock
  private MiraklExportOffer newMiraklExportOfferMock, existingMiraklExportOfferMock;
  @Mock
  private OfferModel newOfferModelMock, existingOfferModelMock, oldOfferModelMock;
  @Mock
  private OfferDao offerDaoMock;
  @Mock
  private OfferImportErrorHandler errorHandlerMock;
  @Captor
  private ArgumentCaptor<MiraklOffersExportRequest> miraklOffersExportRequestArgumentCaptor;
  @Captor
  private ArgumentCaptor<MiraklStream<MiraklExportOffer>> miraklStreamArgumentCaptor;

  @Before
  public void setUp() throws IOException {
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenReturn(getMiraklStream(miraklExportOfferMock));
    when(newMiraklExportOfferMock.getId()).thenReturn(NEW_OFFER_ID);
    when(existingMiraklExportOfferMock.getId()).thenReturn(EXISTING_OFFER_ID);
    when(existingMiraklExportOfferMock.isDeleted()).thenReturn(false);
    when(existingMiraklExportOfferMock.isActive()).thenReturn(true);
    when(offerConverter.convert(newMiraklExportOfferMock)).thenReturn(newOfferModelMock);
    when(offerDaoMock.findOfferById(EXISTING_OFFER_ID)).thenReturn(existingOfferModelMock);
    when(offerDaoMock.findUndeletedOffersModifiedBeforeDate(jobStartTimeMock)).thenReturn(singletonList(oldOfferModelMock));
  }

  @Test
  public void importOffersUpdatedWithLastImportDate() {
    Collection<OfferModel> importedOffers = testObj.importOffersUpdatedSince(lastImportDateMock);

    verify(miraklOperatorApi).exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture());
    verify(testObj).importOffers(miraklStreamArgumentCaptor.capture());
    MiraklStream<MiraklExportOffer> miraklStream = miraklStreamArgumentCaptor.getValue();
    assertThat(miraklStream.iterator().next()).isEqualTo(miraklExportOfferMock);

    MiraklOffersExportRequest miraklOffersExportRequest = miraklOffersExportRequestArgumentCaptor.getValue();
    assertThat(miraklOffersExportRequest).isNotNull();
    assertThat(miraklOffersExportRequest.getQueryParams().keySet()).containsOnly(LAST_REQUEST_DATE_KEY);
    assertThat(miraklOffersExportRequest.getQueryParams().get(LAST_REQUEST_DATE_KEY))
        .isEqualTo(DateFormatter.formatDate(lastImportDateMock));
    assertThat(importedOffers).hasSize(SINGLETON_LIST_SIZE);
  }

  @Test
  public void importAllOffers() {
    Collection<OfferModel> importedOffers = testObj.importAllOffers(jobStartTimeMock, true);

    verify(miraklOperatorApi).exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture());
    verify(testObj).setMissingOffersDeleted(jobStartTimeMock, new ArrayList<OfferModel>(importedOffers));

    MiraklOffersExportRequest miraklOffersExportRequest = miraklOffersExportRequestArgumentCaptor.getValue();
    assertThat(miraklOffersExportRequest).isNotNull();
    assertThat(miraklOffersExportRequest.getQueryParams().get(INCLUDE_INACTIVE_OFFERS_QUERY_PARAM))
        .isEqualTo(Boolean.TRUE.toString());
    assertThat(importedOffers).hasSize(SINGLETON_LIST_SIZE);
  }

  @Test(expected = MiraklApiException.class)
  public void importOffersThrowsMiraklApiException() {
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));

    testObj.importAllOffers(jobStartTimeMock, false);
  }

  @Test
  public void fullUpdateOffersSetsOfferModelsAsDeletedIfNotInMiraklExportOfferList() {
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenReturn(getMiraklStream(newMiraklExportOfferMock));

    testObj.importAllOffers(jobStartTimeMock, false);

    verify(offerConverter).convert(newMiraklExportOfferMock);
    verify(oldOfferModelMock).setDeleted(true);
    verify(newOfferModelMock, never()).setDeleted(anyBoolean());
    verify(modelServiceMock).save(oldOfferModelMock);
    verify(modelServiceMock).save(newOfferModelMock);
  }

  @Test
  public void updateOffersCreatesNewOfferAndUpdatesExisting() {
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenReturn(getMiraklStream(newMiraklExportOfferMock, existingMiraklExportOfferMock));

    testObj.importOffersUpdatedSince(lastImportDateMock);

    verify(offerConverter).convert(newMiraklExportOfferMock);
    verify(offerConverter).convert(existingMiraklExportOfferMock, existingOfferModelMock);
    verify(modelServiceMock).save(newOfferModelMock);
    verify(modelServiceMock).save(existingOfferModelMock);
  }

  @Test
  public void updateOffersUpdatesDeletedAndActiveFlagOnlyAndNotOtherAttributesIfMiraklExportOfferIsDeleted() {
    when(existingMiraklExportOfferMock.isDeleted()).thenReturn(true);
    when(existingMiraklExportOfferMock.isActive()).thenReturn(true);
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenReturn(getMiraklStream(existingMiraklExportOfferMock));

    testObj.importOffersUpdatedSince(lastImportDateMock);

    verify(offerConverter, never()).convert(existingMiraklExportOfferMock, existingOfferModelMock);
    verify(existingOfferModelMock).setDeleted(true);
    verify(existingOfferModelMock).setActive(true);
    verify(modelServiceMock).save(existingOfferModelMock);
  }

  @Test
  public void updateOffersUpdatesDeletedAndActiveFlagOnlyAndNotOtherAttributesIfMiraklExportOfferIsNotActive() {
    when(existingMiraklExportOfferMock.isDeleted()).thenReturn(false);
    when(existingMiraklExportOfferMock.isActive()).thenReturn(false);
    when(miraklOperatorApi.exportOffersAsStream(miraklOffersExportRequestArgumentCaptor.capture()))
        .thenReturn(getMiraklStream(existingMiraklExportOfferMock));

    testObj.importOffersUpdatedSince(lastImportDateMock);

    verify(offerConverter, never()).convert(existingMiraklExportOfferMock, existingOfferModelMock);
    verify(existingOfferModelMock).setDeleted(false);
    verify(existingOfferModelMock).setActive(false);
    verify(modelServiceMock).save(existingOfferModelMock);
  }

}
