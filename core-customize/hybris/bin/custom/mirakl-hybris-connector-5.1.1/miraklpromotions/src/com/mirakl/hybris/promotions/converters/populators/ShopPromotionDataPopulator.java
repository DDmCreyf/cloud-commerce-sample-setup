package com.mirakl.hybris.promotions.converters.populators;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklPromotionData;
import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ShopPromotionDataPopulator implements Populator<ShopModel, ShopData> {

  protected MiraklPromotionService miraklPromotionService;
  protected Converter<MiraklPromotionModel, MiraklPromotionData> miraklPromotionDataConverter;

  @Override
  public void populate(ShopModel shopModel, ShopData shopData) throws ConversionException {
    Collection<MiraklPromotionModel> promotions = miraklPromotionService.getPromotionsForShop(shopModel.getId(), true);
    shopData.setPromotions(miraklPromotionDataConverter.convertAllIgnoreExceptions(promotions));
  }

  @Required
  public void setMiraklPromotionService(MiraklPromotionService miraklPromotionService) {
    this.miraklPromotionService = miraklPromotionService;
  }

  @Required
  public void setMiraklPromotionDataConverter(Converter<MiraklPromotionModel, MiraklPromotionData> miraklPromotionDataConverter) {
    this.miraklPromotionDataConverter = miraklPromotionDataConverter;
  }
}
