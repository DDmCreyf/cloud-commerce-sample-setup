package com.mirakl.hybris.advancedpricingchannels.search.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder.offerPricingCriteria;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.search.solrfacetsearch.populators.impl.MiraklOfferOverviewDataAdvancedPricePopulator;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.channels.channel.strategies.MiraklChannelResolvingStrategy;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;

public class MiraklOfferOverviewDataChannelAdvancedPricePopulator extends MiraklOfferOverviewDataAdvancedPricePopulator {

  protected MiraklChannelResolvingStrategy channelResolvingStrategy;

  @Override
  protected List<OfferPricingData> getOfferPricingsData(OfferModel source) {
    MiraklChannelModel currentChannel = channelResolvingStrategy.resolveCurrentChannel();
    String channelCode = currentChannel != null ? currentChannel.getCode() : null;

    return offerPricingFacade
        .getOfferPricingsForCriteria(offerPricingCriteria(source.getId()) //
            .withChannelCode(channelCode) //
            .build());
  }

  @Required
  public void setChannelResolvingStrategy(MiraklChannelResolvingStrategy channelResolvingStrategy) {
    this.channelResolvingStrategy = channelResolvingStrategy;
  }

}
