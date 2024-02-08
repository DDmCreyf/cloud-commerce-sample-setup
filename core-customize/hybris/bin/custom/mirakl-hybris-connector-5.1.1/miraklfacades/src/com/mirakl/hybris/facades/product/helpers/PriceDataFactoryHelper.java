package com.mirakl.hybris.facades.product.helpers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class PriceDataFactoryHelper {

  protected PriceDataFactory priceDataFactory;

  protected CommonI18NService commonI18NService;

  public PriceData createPrice(BigDecimal priceValue) {
    return priceDataFactory.create(PriceDataType.BUY, priceValue, commonI18NService.getCurrentCurrency());
  }

  @Required
  public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
    this.priceDataFactory = priceDataFactory;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }
}
