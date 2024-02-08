package com.mirakl.hybris.advancedpricingchannels.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.populators.OfferPricingCriteriaBuilderPopulator;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import com.mirakl.hybris.channels.model.MiraklChannelModel;

public class ChannelOfferPricingCriteriaBuilderPopulator implements OfferPricingCriteriaBuilderPopulator {

  protected MiraklChannelResolvingStrategy channelResolvingStrategy;

  @Override
  public void populate(OfferPricingCriteriaBuilder builder) {
    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();
    builder.withChannelCode(currentChannel != null ? currentChannel.getCode() : null);
  }

  @Required
  public void setChannelResolvingStrategy(MiraklChannelResolvingStrategy channelResolvingStrategy) {
    this.channelResolvingStrategy = channelResolvingStrategy;
  }

}
