package com.mirakl.hybris.channels.product.populators;

import static java.util.Collections.singleton;

import java.util.Optional;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import com.mirakl.hybris.core.product.populators.MiraklGetOffersOnProductsRequestPopulator;

public class MiraklChannelsGetOffersOnProductsRequestPopulator implements MiraklGetOffersOnProductsRequestPopulator {
  protected MiraklChannelResolvingStrategy channelResolvingStrategy;

  @Override
  public void populate(final MiraklGetOffersOnProductsRequest getOffersOnProductsRequest) {
    //channelCodes refers to the channels on which the offer is proposed,
    //pricingChannelCode refers to the channel for which the price is proposed
    Optional.ofNullable(channelResolvingStrategy.resolveCurrentChannel()).ifPresent(miraklChannelModel -> {
      getOffersOnProductsRequest.setPricingChannelCode(miraklChannelModel.getCode());
      getOffersOnProductsRequest.setChannelCodes(singleton(channelResolvingStrategy.resolveCurrentChannel().getCode()));
    });
  }

  public void setChannelResolvingStrategy(final MiraklChannelResolvingStrategy channelResolvingStrategy) {
    this.channelResolvingStrategy = channelResolvingStrategy;
  }
}
