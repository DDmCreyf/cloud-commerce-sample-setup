package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OffersSummaryData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklChannelsOffersSummaryDataPopulatorTest {

  private static final String CHANNEL_CODE_1 = "channel-code-1";
  private static final String CHANNEL_CODE_2 = "channel-code-2";

  @InjectMocks
  private MiraklChannelsOffersSummaryDataPopulator populator;

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private ProductModel product;
  @Mock
  private OfferOverviewData offerWithoutChannels1, offerWithoutChannels2, offerWithChannels1, offerWithChannels2,
      offerWithChannels3;
  private List<OfferOverviewData> offers;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(offerWithChannels1.getChannelCodes()).thenReturn(newHashSet(CHANNEL_CODE_1, CHANNEL_CODE_2));
    when(offerWithChannels2.getChannelCodes()).thenReturn(newHashSet(CHANNEL_CODE_2));
    when(offerWithChannels3.getChannelCodes()).thenReturn(newHashSet(CHANNEL_CODE_1));
    offers = asList(offerWithoutChannels1, offerWithChannels1, offerWithoutChannels2, offerWithChannels2, offerWithChannels3);
  }

  @Test
  public void populateSublistOfOffersWhenMoreThanLimit() {
    int maxOfferNumber = 2;
    when(configuration.getInteger(eq(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT), any(Integer.class)))
        .thenReturn(maxOfferNumber);

    OffersSummaryData target = new OffersSummaryData();
    populator.populate(offers, target);


    assertThat(target.getOffersWithChannels()).hasSize(maxOfferNumber);
    assertThat(target.getOffersWithChannels()).containsExactly(offerWithChannels1, offerWithChannels2);
  }

  @Test
  public void populateAllOffersWhenLessThanLimit() {
    int maxOfferNumber = 5;
    when(configuration.getInteger(eq(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT), any(Integer.class)))
        .thenReturn(maxOfferNumber);

    OffersSummaryData target = new OffersSummaryData();
    populator.populate(offers, target);

    assertThat(target.getOffersWithChannels()).hasSize(3);
    assertThat(target.getOffersWithChannels()).containsExactly(offerWithChannels1, offerWithChannels2, offerWithChannels3);
  }

  @Test
  public void populateEmptyListWhenLimitIsZero() {
    int maxOfferNumber = 0;
    when(configuration.getInteger(eq(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT), any(Integer.class)))
        .thenReturn(maxOfferNumber);

    OffersSummaryData target = new OffersSummaryData();
    populator.populate(offers, target);

    assertThat(target.getOffersWithChannels()).isEmpty();
  }

  @Test
  public void populateEmptyListWhenNoChannels() {
    int maxOfferNumber = 5;
    offers = asList(offerWithoutChannels1, offerWithoutChannels2);
    when(configuration.getInteger(eq(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT), any(Integer.class)))
        .thenReturn(maxOfferNumber);

    OffersSummaryData target = new OffersSummaryData();
    populator.populate(offers, target);

    assertThat(target.getOffersWithChannels()).isEmpty();
  }

  @Test
  public void populateEmptyListWhenNoOffers() {
    int maxOfferNumber = 0;
    when(configuration.getInteger(eq(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT), any(Integer.class)))
        .thenReturn(maxOfferNumber);

    OffersSummaryData target = new OffersSummaryData();
    populator.populate(emptyList(), target);

    assertThat(target.getOffersWithChannels()).isEmpty();
  }


}
