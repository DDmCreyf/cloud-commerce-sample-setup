package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

public class MiraklMaxPaginationSizeGetOffersOnProductsRequestPopulator implements MiraklGetOffersOnProductsRequestPopulator {

  protected ConfigurationService configurationService;

  @Override
  public void populate(final MiraklGetOffersOnProductsRequest getOffersOnProductsRequest) {
    getOffersOnProductsRequest.setMax(configurationService.getConfiguration().getInt(MiraklservicesConstants.REALTIME_OFFER_PRICE_FETCH_PAGESIZE, 100));
  }

  @Required
  public void setConfigurationService(final ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
