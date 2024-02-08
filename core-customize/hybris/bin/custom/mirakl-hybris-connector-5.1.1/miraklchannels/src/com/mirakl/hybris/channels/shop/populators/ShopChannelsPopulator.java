package com.mirakl.hybris.channels.shop.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ShopChannelsPopulator implements Populator<MiraklShop, ShopModel> {

  protected MiraklChannelService miraklChannelService;

  @Override
  public void populate(MiraklShop miraklShop, ShopModel shopModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklShop", miraklShop);
    validateParameterNotNullStandardMessage("shopModel", shopModel);

    if (isNotEmpty(miraklShop.getChannels())) {
      populateChannels(miraklShop, shopModel);
    }
  }

  protected void populateChannels(MiraklShop miraklShop, ShopModel shopModel) {
    Set<MiraklChannelModel> channels = new HashSet<>();
    for (String channelCode : miraklShop.getChannels()) {
      MiraklChannelModel resolvedChannel = miraklChannelService.getMiraklChannelForCode(channelCode);
      if (resolvedChannel != null) {
        channels.add(resolvedChannel);
      } else {
        channels.add(miraklChannelService.createMiraklChannelSilently(channelCode, channelCode));
      }
    }
    shopModel.setChannels(channels);
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }

}
