package com.mirakl.hybris.promotions.action.impl;

import static java.lang.String.format;

import org.apache.log4j.Logger;

import com.mirakl.hybris.promotions.model.MiraklRuleBasedOrderAdjustTotalActionModel;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklDiscountRAO;

import de.hybris.platform.promotionengineservices.action.impl.DefaultOrderAdjustTotalActionStrategy;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;

public class MiraklOrderAdjustTotalActionStrategy extends DefaultOrderAdjustTotalActionStrategy {

  private static final Logger LOG = Logger.getLogger(MiraklOrderAdjustTotalActionStrategy.class);

  @Override
  protected MiraklRuleBasedOrderAdjustTotalActionModel createOrderAdjustTotalAction(PromotionResultModel promoResult,
      DiscountRAO discountRao) {
    if (!(discountRao instanceof MiraklDiscountRAO)) {
      LOG.error(format("Cannot apply %s, action is not of type MiraklDiscountRAO", getClass().getSimpleName()));
      return null;
    }

    MiraklDiscountRAO miraklDiscountRAO = (MiraklDiscountRAO) discountRao;
    MiraklRuleBasedOrderAdjustTotalActionModel miraklAction = getAction(promoResult, miraklDiscountRAO);
    miraklAction.setPromotionId(miraklDiscountRAO.getPromotionId());
    miraklAction.setShopId(miraklDiscountRAO.getShopId());

    return miraklAction;
  }

  protected MiraklRuleBasedOrderAdjustTotalActionModel getAction(PromotionResultModel promoResult,
      MiraklDiscountRAO miraklDiscountRAO) {
    MiraklRuleBasedOrderAdjustTotalActionModel miraklAction =
        (MiraklRuleBasedOrderAdjustTotalActionModel) super.createOrderAdjustTotalAction(promoResult, miraklDiscountRAO);
    return miraklAction;
  }
}
