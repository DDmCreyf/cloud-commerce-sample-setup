package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static com.google.common.collect.FluentIterable.from;
import static com.mirakl.hybris.channels.constants.MiraklchannelsConstants.PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OffersSummaryData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklChannelsOffersSummaryDataPopulator implements Populator<List<OfferOverviewData>, OffersSummaryData> {

  protected static final int DEFAULT_MAX_OFFERS_PER_PRODUCT = 10;

  protected ConfigurationService configurationService;

  @Override
  public void populate(List<OfferOverviewData> source, OffersSummaryData target) throws ConversionException {
    validateParameterNotNullStandardMessage("source", source);
    target.setOffersWithChannels(getOffersWithChannels(source));
  }

  List<OfferOverviewData> getOffersWithChannels(List<OfferOverviewData> offers) {
    return from(isEmpty(offers) ? emptyList() : offers) //
        .filter(channelNotEmptyPredicate()) //
        .limit(getMaxOffersPerProduct()) //
        .toList();
  }

  protected Predicate<OfferOverviewData> channelNotEmptyPredicate() {
    return new Predicate<OfferOverviewData>() {

      @Override
      public boolean apply(OfferOverviewData offerOverview) {
        return !emptyIfNull(offerOverview.getChannelCodes()).isEmpty();
      }
    };
  }


  protected Integer getMaxOffersPerProduct() {
    return configurationService.getConfiguration().getInteger(PRODUCT_INDEX_MAX_CHANNEL_OFFERS_PER_PRODUCT,
        DEFAULT_MAX_OFFERS_PER_PRODUCT);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
