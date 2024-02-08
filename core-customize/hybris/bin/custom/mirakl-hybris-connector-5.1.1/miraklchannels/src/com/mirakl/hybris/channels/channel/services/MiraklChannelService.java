package com.mirakl.hybris.channels.channel.services;

import com.mirakl.hybris.channels.model.MiraklChannelModel;

public interface MiraklChannelService {

  /**
   * Finds the Mirakl channel having a given code.
   *
   * @param code Mirakl channel code
   * @return the {@link MiraklChannelModel} resolved by the code, null otherwise
   */
  MiraklChannelModel getMiraklChannelForCode(String code);

  /**
   * Creates a new {@link MiraklChannelModel}
   *
   * @param code Mirakl channel code
   * @param label Mirakl channel label
   * @return the newly created {@link MiraklChannelModel}
   */
  MiraklChannelModel createMiraklChannel(String code, String label);

  /**
   * Creates a new {@link MiraklChannelModel}, ignoring PK constraint violation errors. Mainly used to prevent concurrent channel
   * creation exceptions.
   *
   * @param code Mirakl channel code
   * @param label Mirakl channel label
   * @return the newly created {@link MiraklChannelModel}
   */
  MiraklChannelModel createMiraklChannelSilently(String code, String label);

  /**
   * Returns the current Mirakl channel stored in the session
   *
   * @return the current Mirakl channel
   */
  MiraklChannelModel getCurrentMiraklChannel();

  /**
   * Sets a Mirakl channel as the current by storing it in the session
   *
   * @param miraklChannel Mirakl channel to set as the current one
   */
  void setCurrentMiraklChannel(MiraklChannelModel miraklChannel);

  /**
   * Checks if Mirakl channels feature is enabled in Hybris
   *
   * @return true if Mirakl channels are enabled
   */
  boolean isMiraklChannelsEnabled();
}
