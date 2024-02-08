package com.mirakl.hybris.core.product.services.impl;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.offer.state.MiraklOfferState;
import com.mirakl.client.mmp.front.request.offer.state.MiraklGetOfferStatesRequest;
import com.mirakl.hybris.core.enums.OfferState;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferStateImportServiceTest {

  private static final String OFFER_STATE_CODE = "1";
  private static final String OFFER_STATE_LABEL = "Test";

  @Spy
  @InjectMocks
  private DefaultOfferStateImportService testObj;

  @Mock
  private MiraklMarketplacePlatformFrontApi mmpFrontApi;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklOfferState miraklOfferState;
  @Mock
  private EnumerationValueModel enumerationValueModel;
  @Mock
  private OfferState offerState;

  @Captor
  private ArgumentCaptor<MiraklGetOfferStatesRequest> miraklOfferStatesExportRequestArgumentCaptor;
  @Captor
  private ArgumentCaptor<List<MiraklOfferState>> miraklOfferStatesListArgumentCaptor;

  private List<MiraklOfferState> offerStates;

  @Before
  public void setUp() throws IOException {
    offerStates = singletonList(miraklOfferState);
    when(mmpFrontApi.getOfferStateList(miraklOfferStatesExportRequestArgumentCaptor.capture())).thenReturn(offerStates);
    when(miraklOfferState.getCode()).thenReturn(OFFER_STATE_CODE);
    when(miraklOfferState.getLabel()).thenReturn(OFFER_STATE_LABEL);
    when(modelService.create(OfferState._TYPECODE)).thenReturn(enumerationValueModel);
    when(offerState.getCode()).thenReturn(OFFER_STATE_CODE);
  }

  @Test
  public void importAllOfferStatesWithNotExistingOfferState() {
    when(enumerationService.getEnumerationValue(OfferState._TYPECODE, OFFER_STATE_CODE))
        .thenThrow(new UnknownIdentifierException("")).thenReturn(offerState);

    Collection<OfferState> importedOfferStates = testObj.importAllOfferStates();

    verify(enumerationService, times(2 * offerStates.size())).getEnumerationValue(OfferState._TYPECODE, OFFER_STATE_CODE);
    verify(modelService, times(offerStates.size())).create(OfferState._TYPECODE);
    verify(modelService, times(offerStates.size())).save(any(EnumerationValueModel.class));

    verify(mmpFrontApi).getOfferStateList(miraklOfferStatesExportRequestArgumentCaptor.capture());
    verify(testObj).importOfferStates(miraklOfferStatesListArgumentCaptor.capture());
    List<MiraklOfferState> value = miraklOfferStatesListArgumentCaptor.getValue();
    assertThat(value.iterator().next()).isEqualTo(miraklOfferState);

    MiraklGetOfferStatesRequest miraklGetOfferStatesRequest = miraklOfferStatesExportRequestArgumentCaptor.getValue();
    assertThat(miraklGetOfferStatesRequest).isNotNull();
    assertThat(importedOfferStates).hasSize(offerStates.size());
    final OfferState offerState = importedOfferStates.iterator().next();
    assertThat(offerState.getCode()).isEqualTo(OFFER_STATE_CODE);
  }

  @Test
  public void importAllOfferStatesWithExistingOfferState() {
    when(enumerationService.getEnumerationValue(OfferState._TYPECODE, OFFER_STATE_CODE)).thenReturn(offerState);

    Collection<OfferState> importedOfferStates = testObj.importAllOfferStates();

    verify(enumerationService, times(offerStates.size())).getEnumerationValue(OfferState._TYPECODE, OFFER_STATE_CODE);
    verify(enumerationService, times(offerStates.size())).setEnumerationName(offerState, OFFER_STATE_LABEL);
    verify(modelService, times(0)).create(OfferState._TYPECODE);
    verify(modelService, times(0)).save(any(OfferState.class));

    verify(mmpFrontApi).getOfferStateList(miraklOfferStatesExportRequestArgumentCaptor.capture());
    verify(testObj).importOfferStates(miraklOfferStatesListArgumentCaptor.capture());
    List<MiraklOfferState> value = miraklOfferStatesListArgumentCaptor.getValue();
    assertThat(value.iterator().next()).isEqualTo(miraklOfferState);

    MiraklGetOfferStatesRequest miraklGetOfferStatesRequest = miraklOfferStatesExportRequestArgumentCaptor.getValue();
    assertThat(miraklGetOfferStatesRequest).isNotNull();
    assertThat(importedOfferStates).hasSize(offerStates.size());
    final OfferState offerState = importedOfferStates.iterator().next();
    assertThat(offerState.getCode()).isEqualTo(OFFER_STATE_CODE);
  }

}
