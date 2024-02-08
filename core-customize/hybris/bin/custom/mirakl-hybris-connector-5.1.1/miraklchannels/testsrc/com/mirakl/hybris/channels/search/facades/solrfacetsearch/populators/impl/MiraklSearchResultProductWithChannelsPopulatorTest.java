package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.facades.constants.MiraklfacadesConstants.OFFERS_SUMMARY_SOLR_PROPERTY_NAME;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferStateSummaryData;
import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklSearchResultProductWithChannelsPopulatorTest {
  private static final String CHANNEL_CODE = "channel-code";
  private static final String JSON_CONTENT = "json-content";
  private static final String OFFER_STATE_CODE = "offer-state-code";

  @InjectMocks
  private MiraklSearchResultProductWithChannelsPopulator populator;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private MiraklChannelService miraklChannelService;
  @Mock
  private Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryDataConverter;
  @Mock
  private MiraklChannelModel miraklChannel;
  @Mock
  private OffersSummaryData offersSummaryData, recalculatedOfferSummaryData;
  @Mock
  private OfferStateSummaryData offerStateSummaryData;
  @Mock
  private SearchResultValueData searchResultValueData;
  @Mock
  private OfferOverviewData overview1, overview2;
  private List<OfferOverviewData> offersOverviews;
  @Captor
  private ArgumentCaptor<List<OfferOverviewData>> offersOverviewsCaptor;

  @Before
  public void setUp() throws Exception {
    offersOverviews = Arrays.asList(overview1, overview2);
    when(jsonMarshallingService.fromJson(JSON_CONTENT, OffersSummaryData.class)).thenReturn(offersSummaryData);
    when(searchResultValueData.getValues())
        .thenReturn(Collections.<String, Object>singletonMap(OFFERS_SUMMARY_SOLR_PROPERTY_NAME, JSON_CONTENT));
    when(miraklChannel.getCode()).thenReturn(CHANNEL_CODE);
    when(overview1.getChannelCodes()).thenReturn(Collections.singleton(CHANNEL_CODE));
  }

  @Test
  public void shouldRebuildOffersSummaryWhenMiraklChannel() {
    when(miraklChannelService.getCurrentMiraklChannel()).thenReturn(miraklChannel);
    when(offersSummaryData.getOffersWithChannels()).thenReturn(offersOverviews);
    when(offersSummaryDataConverter.convert(offersOverviewsCaptor.capture())).thenReturn(recalculatedOfferSummaryData);

    ProductData result = new ProductData();
    populator.populate(searchResultValueData, result);

    List<OfferOverviewData> filteredOverviews = offersOverviewsCaptor.getValue();
    assertThat(filteredOverviews).hasSize(1);
    assertThat(filteredOverviews).containsOnly(overview1);
    assertThat(result.getOffersSummary()).isEqualTo(recalculatedOfferSummaryData);
  }

}
