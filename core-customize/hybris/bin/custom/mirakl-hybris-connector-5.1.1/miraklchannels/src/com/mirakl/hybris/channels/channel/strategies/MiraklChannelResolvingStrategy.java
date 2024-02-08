package com.mirakl.hybris.channels.channel.strategies;

import com.mirakl.hybris.channels.model.MiraklChannelModel;

public interface MiraklChannelResolvingStrategy {

  /**
   * Resolves the current applicable Mirakl channel
   *
   * @return the current Mirakl channel
   */
  MiraklChannelModel resolveCurrentChannel();
}
