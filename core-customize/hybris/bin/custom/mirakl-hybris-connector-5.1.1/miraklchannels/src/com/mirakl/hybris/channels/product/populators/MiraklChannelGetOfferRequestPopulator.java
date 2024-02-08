package com.mirakl.hybris.channels.product.populators;

import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.hybris.channels.channel.services.MiraklChannelService;
import com.mirakl.hybris.core.product.populators.MiraklGetOfferRequestPopulator;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

public class MiraklChannelGetOfferRequestPopulator implements MiraklGetOfferRequestPopulator {
  protected MiraklChannelService miraklChannelService;

  @Override
  public void populate(MiraklGetOfferRequest getOfferRequest) {
    Optional.ofNullable(miraklChannelService.getCurrentMiraklChannel()).ifPresent(currentMiraklChannel -> getOfferRequest.setPricingChannelCode(currentMiraklChannel.getCode()));
  }

  @Required
  public void setMiraklChannelService(MiraklChannelService miraklChannelService) {
    this.miraklChannelService = miraklChannelService;
  }
}
