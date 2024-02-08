package com.mirakl.hybris.promotions.converters.populators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.promotion.MiraklPromotionPublicDescription;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;
import com.mirakl.hybris.promotions.enums.MiraklPromotionActionType;
import com.mirakl.hybris.promotions.enums.MiraklPromotionState;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklPromotionPopulator implements Populator<MiraklPromotion, MiraklPromotionModel> {

  protected LocaleMappingStrategy localeMappingStrategy;

  @Override
  public void populate(MiraklPromotion source, MiraklPromotionModel target) throws ConversionException {
    target.setStartDate(source.getStartDate());
    target.setEndDate(source.getEndDate());
    target.setAmountOff(source.getAmountOff());
    target.setInternalId(source.getInternalId());
    target.setInternalDescription(source.getInternalDescription());
    target.setPercentageOff(source.getPercentageOff());
    target.setFreeItemsQuantity(source.getFreeItemsQuantity());
    target.setMiraklCreationDate(source.getDateCreated());
    target.setShopId(source.getShopId());
    if (source.getState() != null) {
      target.setState(MiraklPromotionState.valueOf(source.getState().toString()));
    }
    if (source.getType() != null) {
      target.setType(MiraklPromotionActionType.valueOf(source.getType().toString()));
    }
    populateDescriptions(source, target);
  }

  protected void populateDescriptions(MiraklPromotion source, MiraklPromotionModel target) {
    List<MiraklPromotionPublicDescription> publicDescriptions = source.getPublicDescriptions();
    if (isNotEmpty(publicDescriptions)) {
      for (MiraklPromotionPublicDescription publicDescription : publicDescriptions) {
        target.setPublicDescription(publicDescription.getValue(),
            localeMappingStrategy.mapToHybrisLocale(publicDescription.getLocale()));
      }
    }
  }

  @Required
  public void setLocaleMappingStrategy(LocaleMappingStrategy localeMappingStrategy) {
    this.localeMappingStrategy = localeMappingStrategy;
  }

}
