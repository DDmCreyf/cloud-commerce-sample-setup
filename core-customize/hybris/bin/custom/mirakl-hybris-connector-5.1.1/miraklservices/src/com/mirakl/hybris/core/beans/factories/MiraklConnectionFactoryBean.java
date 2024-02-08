package com.mirakl.hybris.core.beans.factories;

import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApi;

public interface MiraklConnectionFactoryBean {

  /**
   * Creates an API client for the Mirakl Marketplace Platform Front API.
   * 
   * @return a Mirakl Marketplace Platform Front API client
   */
  MiraklMarketplacePlatformFrontApi getMmpFrontApiClient();

  /**
   * Creates an API client for the Mirakl Marketplace Platform Operator API.
   * 
   * @return a Mirakl Marketplace Platform Front API client
   */
  MiraklMarketplacePlatformOperatorApi getMmpOperatorApiClient();

  /**
   * Creates an API client for the Mirakl Catalog Integrator/Manager Front API.
   * 
   * @return a Mirakl Catalog Integrator/Manager Front API client
   */
  MiraklCatalogIntegrationFrontApi getMciFrontApiClient();

}
