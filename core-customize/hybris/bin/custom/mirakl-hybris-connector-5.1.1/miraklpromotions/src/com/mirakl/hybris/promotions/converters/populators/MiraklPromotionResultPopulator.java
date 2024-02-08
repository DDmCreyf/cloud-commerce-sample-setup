package com.mirakl.hybris.promotions.converters.populators;

import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.promotion.MiraklPromotionPublicDescription;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotions;
import com.mirakl.client.mmp.front.request.promotion.MiraklGetPromotionsRequest;
import com.mirakl.client.mmp.request.promotion.PromotionIdentifier;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;
import com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.model.MiraklRuleBasedOrderAdjustTotalActionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class MiraklPromotionResultPopulator implements Populator<PromotionResultModel, PromotionResultData> {

  private static final Logger LOG = Logger.getLogger(MiraklPromotionResultPopulator.class);

  protected static final String DEFAULT_PROMOTION_MESSAGES_SEPARATOR = ", ";

  protected MiraklPromotionService miraklPromotionService;
  protected ConfigurationService configurationService;
  protected I18NService i18NService;
  protected LocaleMappingStrategy localeMappingStrategy;
  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected ModelService modelService;
  protected Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter;

  @Override
  public void populate(PromotionResultModel source, PromotionResultData target) throws ConversionException {
    List<MiraklRuleBasedOrderAdjustTotalActionModel> miraklPromotionActions = new ArrayList<>();
    for (AbstractPromotionActionModel action : source.getActions()) {
      if (action instanceof MiraklRuleBasedOrderAdjustTotalActionModel) {
        miraklPromotionActions.add((MiraklRuleBasedOrderAdjustTotalActionModel) action);
      }
    }

    if (!miraklPromotionActions.isEmpty()) {
      Set<String> promotionalMessages = getPromotionalMessages(miraklPromotionActions);
      if (!promotionalMessages.isEmpty()) {
        target.setDescription(join(promotionalMessages, configurationService.getConfiguration()
            .getString(MiraklpromotionsConstants.PROMOTION_MESSAGES_SEPARATOR, DEFAULT_PROMOTION_MESSAGES_SEPARATOR)));
      }
    }
  }

  protected Set<String> getPromotionalMessages(List<MiraklRuleBasedOrderAdjustTotalActionModel> promotionActions) {
    Set<String> promotionalMessages = new HashSet<>();
    populateMessages(promotionalMessages, promotionActions, i18NService.getCurrentLocale());

    return promotionalMessages;
  }

  protected void populateMessages(Set<String> promotionMessages,
      List<MiraklRuleBasedOrderAdjustTotalActionModel> miraklPromotionActions, Locale currentLocale) {
    List<MiraklRuleBasedOrderAdjustTotalActionModel> actionsWithMissingPromotions = new ArrayList<>();

    for (MiraklRuleBasedOrderAdjustTotalActionModel miraklPromotionAction : miraklPromotionActions) {
      MiraklPromotionModel promotion =
          miraklPromotionService.getPromotion(miraklPromotionAction.getShopId(), miraklPromotionAction.getPromotionId());
      if (promotion != null) {
        promotionMessages.add(promotion.getPublicDescription(currentLocale));
      } else {
        actionsWithMissingPromotions.add(miraklPromotionAction);
      }
    }
    populateMessagesForMissingPromotions(promotionMessages, actionsWithMissingPromotions, currentLocale);
  }

  protected void populateMessagesForMissingPromotions(Set<String> promotionMessages,
      Collection<MiraklRuleBasedOrderAdjustTotalActionModel> miraklPromotionActions, Locale currentLocale) {
    if (miraklPromotionActions.isEmpty()) {
      return;
    }
    MiraklPromotions miraklPromotions = miraklApi.getPromotions(buildMiraklRequest(miraklPromotionActions));
    List<MiraklPromotionModel> modelsToSave = new ArrayList<>();

    for (MiraklPromotion miraklPromotion : miraklPromotions.getPromotions()) {
      for (MiraklPromotionPublicDescription promotionDescription : miraklPromotion.getPublicDescriptions()) {
        modelsToSave.add(miraklPromotionConverter.convert(miraklPromotion, modelService.create(MiraklPromotionModel.class)));
        if (currentLocale.equals(localeMappingStrategy.mapToHybrisLocale(promotionDescription.getLocale()))) {
          promotionMessages.add(promotionDescription.getValue());
        } else {
          LOG.warn(String.format("No message ([%s]) available for Mirakl promotion [%s]", currentLocale,
              miraklPromotion.getInternalId()));
        }
      }
    }
    modelService.saveAll(modelsToSave);
  }

  protected MiraklGetPromotionsRequest buildMiraklRequest(
      Collection<MiraklRuleBasedOrderAdjustTotalActionModel> miraklPromotionActions) {
    List<PromotionIdentifier> promotionIdentifiers = new ArrayList<>();
    MiraklGetPromotionsRequest request = new MiraklGetPromotionsRequest();
    for (MiraklRuleBasedOrderAdjustTotalActionModel miraklPromotionAction : miraklPromotionActions) {
      promotionIdentifiers
          .add(new PromotionIdentifier(miraklPromotionAction.getShopId(), miraklPromotionAction.getPromotionId()));
    }
    request.setIds(promotionIdentifiers);
    return request;
  }

  @Required
  public void setMiraklPromotionService(MiraklPromotionService miraklPromotionService) {
    this.miraklPromotionService = miraklPromotionService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setI18NService(I18NService i18NService) {
    this.i18NService = i18NService;
  }

  @Required
  public void setLocaleMappingStrategy(LocaleMappingStrategy localeMappingStrategy) {
    this.localeMappingStrategy = localeMappingStrategy;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMiraklPromotionConverter(Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter) {
    this.miraklPromotionConverter = miraklPromotionConverter;
  }
}
