package com.mirakl.hybris.promotions.converters.populators;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.promotion.MiraklAppliedPromotion;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklPromotionRaoPopulator implements Populator<Pair<MiraklAppliedPromotion, String>, MiraklPromotionRAO> {

  @Override
  public void populate(Pair<MiraklAppliedPromotion, String> source, MiraklPromotionRAO target) throws ConversionException {
    MiraklAppliedPromotion miraklAppliedPromotion = source.getLeft();
    target.setDeducedAmount(miraklAppliedPromotion.getDeducedAmount());
    target.setOfferedQuantity(miraklAppliedPromotion.getOfferedQuantity());
    target.setPromotionId(miraklAppliedPromotion.getId());
    target.setShopId(source.getRight());
  }

}
