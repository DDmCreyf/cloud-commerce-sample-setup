package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;


import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl.MiraklSearchResultProductPopulator;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklSearchResultProductWithChannelsPopulator extends MiraklSearchResultProductPopulator {

  protected MiraklChannelService miraklChannelService;
  protected Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryDataConverter;

  @Override
  protected OffersSummaryData decorateOffersSummaryData(OffersSummaryData offersSummary) {
    if (miraklChannelService.getCurrentMiraklChannel() == null) {
      return offersSummary;
    }
    List<OfferOverviewData> filteredOffers = filterOffersForCurrentMiraklChannel(offersSummary.getOffersWithChannels());
    return offersSummaryDataConverter.convert(filteredOffers);
  }

  protected List<OfferOverviewData> filterOffersForCurrentMiraklChannel(List<OfferOverviewData> offers) {
    final MiraklChannelModel currentMiraklChannel = miraklChannelService.getCurrentMiraklChannel();
    if (currentMiraklChannel == null) {
      return offers;
    }
    return FluentIterable.from(offers).filter(channelFilterPredicate(currentMiraklChannel)).toList();
  }

  protected Predicate<OfferOverviewData> channelFilterPredicate(final MiraklChannelModel currentMiraklChannel) {
    return new Predicate<OfferOverviewData>() {

      @Override
      public boolean apply(OfferOverviewData offerOverview) {
        return emptyIfNull(offerOverview.getChannelCodes()).contains(currentMiraklChannel.getCode());
      }
    };
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

  @Required
  public void setOffersSummaryDataConverter(Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryDataConverter) {
    this.offersSummaryDataConverter = offersSummaryDataConverter;
  }
}
