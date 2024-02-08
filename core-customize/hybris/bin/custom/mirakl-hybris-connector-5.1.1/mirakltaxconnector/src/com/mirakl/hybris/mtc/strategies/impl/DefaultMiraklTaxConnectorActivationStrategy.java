package com.mirakl.hybris.mtc.strategies.impl;

import static com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants.ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES;
import static com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants.ALLOWED_MIRAKL_TAX_CONNECTOR_SHIP_TO_COUNTRIES;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultMiraklTaxConnectorActivationStrategy implements MiraklTaxConnectorActivationStrategy {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklTaxConnectorActivationStrategy.class);

  protected BaseStoreService baseStoreService;

  @Override
  public boolean isMiraklTaxConnectorEnabled() {
    BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
    return currentBaseStore != null && currentBaseStore.isNet() && currentBaseStore.isMiraklTaxConnectorEnabled();
  }

  @Override
  public boolean isMiraklTaxConnectorComputation(AbstractOrderModel order) {
    validateParameterNotNullStandardMessage("order", order);
    if (!isMiraklTaxConnectorEnabled() || order.getDeliveryAddress() == null || order.getDeliveryAddress().getCountry() == null) {
      return false;
    }
    return isAllowedOrderCurrency(order) && isAllowedDeliveryCountry(order);
  }

  protected boolean isAllowedDeliveryCountry(AbstractOrderModel order) {
    String countryIsoAlpha3 = order.getDeliveryAddress().getCountry().getIsoAlpha3();
    try {
      return ALLOWED_MIRAKL_TAX_CONNECTOR_SHIP_TO_COUNTRIES.contains(IsoCountryCode.valueOf(countryIsoAlpha3));
    } catch (IllegalArgumentException e) {
      LOG.warn(format("The country [%s] cannot be used with the Mirakl Tax Connector.", countryIsoAlpha3), e);
      return false;
    }
  }

  protected boolean isAllowedOrderCurrency(AbstractOrderModel order) {
    String currencyIsocode = order.getCurrency().getIsocode();
    try {
      return ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES.contains(MiraklIsoCurrencyCode.valueOf(currencyIsocode));
    } catch (IllegalArgumentException e) {
      LOG.warn(format("The currency [%s] cannot be used with the Mirakl Tax Connector.", currencyIsocode), e);
      return false;
    }
  }

  @Required
  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

}
