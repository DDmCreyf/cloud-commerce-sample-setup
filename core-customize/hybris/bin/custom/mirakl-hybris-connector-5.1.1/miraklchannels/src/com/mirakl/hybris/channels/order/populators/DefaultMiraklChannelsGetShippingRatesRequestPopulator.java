package com.mirakl.hybris.channels.order.populators;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.channels.model.MiraklChannelModel;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestPopulator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.springframework.beans.factory.annotation.Required;

public class DefaultMiraklChannelsGetShippingRatesRequestPopulator implements MiraklGetShippingRatesRequestPopulator {

  protected MiraklChannelService miraklChannelService;

  @Override
  public MiraklGetShippingRatesRequest populate(AbstractOrderModel order, MiraklGetShippingRatesRequest request) {
    MiraklChannelModel currentMiraklChannel = miraklChannelService.getCurrentMiraklChannel();
    if (currentMiraklChannel != null) {
      request.setChannelCode(currentMiraklChannel.getCode());
    }
    return request;
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }
}
