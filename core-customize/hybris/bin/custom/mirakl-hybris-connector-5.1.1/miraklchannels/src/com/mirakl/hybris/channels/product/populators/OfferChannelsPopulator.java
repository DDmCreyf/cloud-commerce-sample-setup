package com.mirakl.hybris.channels.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.offer.MiraklExportOffer;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OfferChannelsPopulator implements Populator<MiraklExportOffer, OfferModel> {

  protected MiraklChannelService miraklChannelService;

  @Override
  public void populate(MiraklExportOffer miraklExportOffer, OfferModel offerModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklExportOffer", miraklExportOffer);
    validateParameterNotNullStandardMessage("offerModel", offerModel);

    if (isNotEmpty(miraklExportOffer.getChannels())) {
      populateChannels(miraklExportOffer, offerModel);
    }
  }

  protected void populateChannels(MiraklExportOffer miraklExportOffer, OfferModel offerModel) {
    Set<MiraklChannelModel> channels = new HashSet<>();
    for (String channelCode : miraklExportOffer.getChannels()) {
      MiraklChannelModel resolvedChannel = miraklChannelService.getMiraklChannelForCode(channelCode);
      if (resolvedChannel != null) {
        channels.add(resolvedChannel);
      } else {
        channels.add(miraklChannelService.createMiraklChannel(channelCode, channelCode));
      }
    }
    offerModel.setChannels(channels);
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
