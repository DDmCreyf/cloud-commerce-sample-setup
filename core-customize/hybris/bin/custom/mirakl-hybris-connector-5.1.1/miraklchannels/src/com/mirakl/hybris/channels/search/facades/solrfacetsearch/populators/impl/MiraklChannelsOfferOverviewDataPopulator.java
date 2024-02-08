package com.mirakl.hybris.channels.search.facades.solrfacetsearch.populators.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklChannelsOfferOverviewDataPopulator implements Populator<OfferModel, OfferOverviewData> {

  @Override
  public void populate(OfferModel source, OfferOverviewData target) throws ConversionException {
    validateParameterNotNullStandardMessage("source", source);
    validateParameterNotNullStandardMessage("target", target);

    populateChannels(source, target);
  }

  protected void populateChannels(OfferModel source, OfferOverviewData target) {
    target.setChannelCodes(extractChannelCodes(emptyIfNull(source.getChannels())));
  }

  protected ImmutableSet<String> extractChannelCodes(Collection<MiraklChannelModel> channels) {
    return FluentIterable.from(channels).transform(new Function<MiraklChannelModel, String>() {

      @Override
      public String apply(MiraklChannelModel channel) {
        return channel.getCode();
      }
    }).toSet();
  }

}
