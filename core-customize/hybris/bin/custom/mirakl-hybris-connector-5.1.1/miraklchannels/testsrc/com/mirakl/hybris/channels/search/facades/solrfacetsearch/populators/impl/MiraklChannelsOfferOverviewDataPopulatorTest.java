package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklChannelsOfferOverviewDataPopulatorTest {

  private static final String CHANNEL_1_CODE = "channel-1-code";
  private static final String CHANNEL_2_CODE = "channel-2-code";

  @InjectMocks
  private MiraklChannelsOfferOverviewDataPopulator populator;

  @Mock
  private OfferModel offer;
  @Mock
  private MiraklChannelModel channel1, channel2;

  @Before
  public void setUp() throws Exception {
    when(channel1.getCode()).thenReturn(CHANNEL_1_CODE);
    when(channel2.getCode()).thenReturn(CHANNEL_2_CODE);
  }

  @Test
  public void shouldPopulateChannelCodes() {
    when(offer.getChannels()).thenReturn(Sets.newHashSet(channel1, channel2));

    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getChannelCodes()).containsOnly(CHANNEL_1_CODE, CHANNEL_2_CODE);
  }

}
