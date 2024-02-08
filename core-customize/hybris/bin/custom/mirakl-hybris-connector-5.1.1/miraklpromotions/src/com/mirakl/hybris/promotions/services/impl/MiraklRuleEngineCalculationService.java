package com.mirakl.hybris.promotions.services.impl;

import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklDiscountRAO;

import de.hybris.order.calculation.domain.AbstractDiscount;
import de.hybris.platform.ruleengineservices.calculation.impl.DefaultRuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;

public class MiraklRuleEngineCalculationService extends DefaultRuleEngineCalculationService {

  @Override
  protected DiscountRAO createDiscountRAO(AbstractDiscount discount) {
    DiscountRAO discountRAO = super.createDiscountRAO(discount);

    return toMiraklDiscountRAO(discountRAO);
  }

  protected MiraklDiscountRAO toMiraklDiscountRAO(DiscountRAO discountRAO) {
    MiraklDiscountRAO miraklDiscountRAO = new MiraklDiscountRAO();
    miraklDiscountRAO.setValue(discountRAO.getValue());
    miraklDiscountRAO.setCurrencyIsoCode(discountRAO.getCurrencyIsoCode());

    return miraklDiscountRAO;
  }
}
