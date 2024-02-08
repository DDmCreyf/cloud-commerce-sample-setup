package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.REALTIME_OFFER_PRICE_ACCESS_MODE;

public class DefaultMiraklRealtimeOfferPriceRetrievalMode implements MiraklRealtimeOfferPriceRetrievalMode {
  private ConfigurationService configurationService;

  @Override
  public boolean isActive() {
    return BooleanUtils.isTrue(configurationService.getConfiguration().getBoolean(REALTIME_OFFER_PRICE_ACCESS_MODE,false));
  }

  @Required
  public void setConfigurationService(final ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
