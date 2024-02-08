package com.mirakl.hybris.core.beans.strategies;

import com.mirakl.client.core.internal.MiraklClientConfigWrapper;

public interface MiraklApiClientConfigurationStrategy {

  /**
   * Customizes the Mirakl API client config
   * 
   * @param clientConfigWrapper
   */
  void configure(MiraklClientConfigWrapper clientConfigWrapper);

}
