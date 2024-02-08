package com.mirakl.hybris.promotions.converters.populators;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklPromotionData;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class OfferPromotionDataPopulator implements Populator<OfferModel, OfferData> {

  protected Converter<MiraklPromotionModel, MiraklPromotionData> miraklPromotionDataConverter;
  protected MiraklPromotionService miraklPromotionService;
  protected MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;

  @Override
  public void populate(OfferModel offerModel, OfferData offerData) throws ConversionException {
    if (miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()) {
      Collection<MiraklPromotionModel> promotions = miraklPromotionService.getPromotionsForOffer(offerModel, true);
      offerData.setPromotions(miraklPromotionDataConverter.convertAll(promotions));
    } else {
      offerData.setPromotions(Collections.emptyList());
    }
  }

  @Required
  public void setMiraklPromotionDataConverter(Converter<MiraklPromotionModel, MiraklPromotionData> miraklPromotionDataConverter) {
    this.miraklPromotionDataConverter = miraklPromotionDataConverter;
  }

  @Required
  public void setMiraklPromotionService(MiraklPromotionService miraklPromotionService) {
    this.miraklPromotionService = miraklPromotionService;
  }

  @Required
  public void setMiraklPromotionsActivationStrategy(MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy) {
    this.miraklPromotionsActivationStrategy = miraklPromotionsActivationStrategy;
  }
}
