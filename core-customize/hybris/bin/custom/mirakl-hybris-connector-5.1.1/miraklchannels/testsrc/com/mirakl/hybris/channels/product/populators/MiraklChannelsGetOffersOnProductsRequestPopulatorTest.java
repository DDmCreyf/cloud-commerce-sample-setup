package com.mirakl.hybris.channels.product.populators;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklChannelsGetOffersOnProductsRequestPopulatorTest {
  private final static String CHANNEL_TEST = "Channel-test";
  private final static String PRODUCT_CODE = "PRODUCT_CODE";
  @InjectMocks
  private MiraklChannelsGetOffersOnProductsRequestPopulator populator;
  @Mock
  private MiraklChannelResolvingStrategy channelResolvingStrategy;

  @Test
  public void populateWithAChannel() {
    final MiraklChannelModel channel = new MiraklChannelModel();
    channel.setCode(CHANNEL_TEST);
    when(channelResolvingStrategy.resolveCurrentChannel()).thenReturn(channel);

    final MiraklGetOffersOnProductsRequest getOffersOnProductsRequest = new MiraklGetOffersOnProductsRequest(Collections.singleton(PRODUCT_CODE));

    populator.populate(getOffersOnProductsRequest);

    Assertions.assertThat(getOffersOnProductsRequest.getPricingChannelCode()).isEqualTo(CHANNEL_TEST);

    String actualChannelCodes = getOffersOnProductsRequest.getQueryParams().get("channel_codes");
    Assertions.assertThat(actualChannelCodes).isNotNull();
    Assertions.assertThat(actualChannelCodes).isEqualTo(CHANNEL_TEST);
  }

  @Test
  public void populateWithNoChannel() {
    when(channelResolvingStrategy.resolveCurrentChannel()).thenReturn(null);

    final MiraklGetOffersOnProductsRequest getOffersOnProductsRequest = new MiraklGetOffersOnProductsRequest(Collections.singleton(PRODUCT_CODE));

    populator.populate(getOffersOnProductsRequest);

    Assertions.assertThat(getOffersOnProductsRequest.getPricingChannelCode()).isNull();
    Assertions.assertThat(getOffersOnProductsRequest.getQueryParams().get("channel_codes")).isNull();

  }
}
