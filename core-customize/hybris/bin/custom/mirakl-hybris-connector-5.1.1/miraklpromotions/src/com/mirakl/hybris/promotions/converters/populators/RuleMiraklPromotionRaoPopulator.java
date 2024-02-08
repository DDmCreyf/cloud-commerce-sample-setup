package com.mirakl.hybris.promotions.converters.populators;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.promotion.MiraklAppliedPromotion;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class RuleMiraklPromotionRaoPopulator implements Populator<CartModel, CartRAO> {

  private static final Logger LOG = Logger.getLogger(RuleMiraklPromotionRaoPopulator.class);

  protected Converter<Pair<MiraklAppliedPromotion, String>, MiraklPromotionRAO> miraklAppliedPromotionConverter;
  protected MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void populate(CartModel source, CartRAO target) throws ConversionException {
    if (!miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled() || isEmpty(source.getMarketplaceEntries())) {
      return;
    }

    String cartCalculationJSON = source.getCartCalculationJSON();
    if (isBlank(cartCalculationJSON)) {
      LOG.warn(format("No cart calculation JSON object was found on cart [%s]. Marketplace promotions (if any) will be ignored",
          source.getCode()));
      return;
    }
    MiraklOrderShippingFees calculatedCart = jsonMarshallingService.fromJson(cartCalculationJSON, MiraklOrderShippingFees.class);
    List<MiraklPromotionRAO> promotionRaos = miraklAppliedPromotionConverter.convertAll(extractPromotions(calculatedCart));
    if (isNotEmpty(promotionRaos)) {
      target.setAppliedMiraklPromotions(promotionRaos);
    }
  }

  protected List<Pair<MiraklAppliedPromotion, String>> extractPromotions(MiraklOrderShippingFees calculatedCart) {
    List<Pair<MiraklAppliedPromotion, String>> promotions = new ArrayList<>();
    for (MiraklOrderShippingFee order : calculatedCart.getOrders()) {
      if (order.getPromotions() != null) {
        for (MiraklAppliedPromotion miraklAppliedPromotion : order.getPromotions().getAppliedPromotions()) {
          promotions.add(Pair.of(miraklAppliedPromotion, order.getShopId()));
        }
      }
    }
    return promotions;
  }

  @Required
  public void setMiraklAppliedPromotionConverter(
      Converter<Pair<MiraklAppliedPromotion, String>, MiraklPromotionRAO> miraklAppliedPromotionConverter) {
    this.miraklAppliedPromotionConverter = miraklAppliedPromotionConverter;
  }

  @Required
  public void setMiraklPromotionsActivationStrategy(MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy) {
    this.miraklPromotionsActivationStrategy = miraklPromotionsActivationStrategy;
  }

  @Required
  public void setJsonMarshallingService(final JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }
}
