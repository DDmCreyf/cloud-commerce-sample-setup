package com.mirakl.hybris.promotions.converters.populators;

import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.*;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotionOffersMapping;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklPromotionOffersMappingImpexPopulator implements Populator<MiraklPromotionOffersMapping, Map<String, String>> {

  @Override
  public void populate(MiraklPromotionOffersMapping source, Map<String, String> target) throws ConversionException {
    target.put(OFFER_ID_IMPEX_COLUMN, source.getOfferId());
    target.put(TRIGGER_PROMOTIONS_IMPEX_COLUMN, getPromotionImpexFormat(source.getShopId(), source.getTriggerPromotionIds()));
    target.put(REWARD_PROMOTIONS_IMPEX_COLUMN, getPromotionImpexFormat(source.getShopId(), source.getRewardPromotionIds()));
  }

  protected String getPromotionImpexFormat(final String shopId, final Collection<String> promotionInternalIds) {
    if (promotionInternalIds == null) {
      return "";
    }
    return Joiner.on(',').join(FluentIterable.from(promotionInternalIds).transform(new Function<String, String>() {

      @Override
      public String apply(String promotionId) {
        return isBlank(promotionId) ? EMPTY : format("%s:%s", promotionId, shopId);
      }
    }).toList());
  }
}
