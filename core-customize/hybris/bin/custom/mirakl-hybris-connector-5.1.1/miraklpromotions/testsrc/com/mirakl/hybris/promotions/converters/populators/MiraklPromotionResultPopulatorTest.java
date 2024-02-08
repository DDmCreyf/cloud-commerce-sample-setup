package com.mirakl.hybris.promotions.converters.populators;

import static com.mirakl.hybris.promotions.converters.populators.MiraklPromotionResultPopulator.DEFAULT_PROMOTION_MESSAGES_SEPARATOR;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.promotion.MiraklPromotionPublicDescription;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotions;
import com.mirakl.client.mmp.front.request.promotion.MiraklGetPromotionsRequest;
import com.mirakl.client.mmp.request.promotion.PromotionIdentifier;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.model.MiraklRuleBasedOrderAdjustTotalActionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklPromotionResultPopulatorTest {

  private static final String SHOP_ID_2 = "2132";
  private static final String SHOP_ID_1 = "1476";
  private static final String PROMOTION_ID_2 = "AMOUNT_OFF_2017-10-20-18-11";
  private static final String PROMOTION_ID_1 = "AMOUNT_OFF_2017-10-20-17-35";
  private static final String PROMOTION_DESCRIPTION_1 = "20$ on sunglasses";
  private static final String MIRAKL_PROMOTIONS_MESSAGES_SEPARATOR = "mirakl.promotions.messages.separator";

  @InjectMocks
  private MiraklPromotionResultPopulator testObj;

  @Captor
  private ArgumentCaptor<MiraklGetPromotionsRequest> miraklRequestCaptor;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private I18NService i18NService;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private AbstractPromotionActionModel randomActionModel1, randomActionModel2;
  @Mock
  private MiraklRuleBasedOrderAdjustTotalActionModel miraklActionModel1, miraklActionModel2;
  @Mock
  private PromotionResultModel promotionResult;
  @Mock
  private PromotionResultData target;
  @Mock
  private MiraklPromotions miraklPromotions;
  @Mock
  private MiraklPromotion miraklPromotion1, miraklPromotion2;
  @Mock
  private MiraklPromotionPublicDescription descriptionPromotion1, descriptionPromotion2;
  @Mock
  private Configuration configuration;
  @Mock
  private MiraklPromotionService miraklPromotionService;
  @Mock
  private LocaleMappingStrategy localeMappingStrategy;
  @Mock
  private ModelService modelService;
  @Mock
  private Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter;
  @Mock
  private MiraklPromotionModel createdModel1, createdModel2, miraklPromotionModel1, miraklPromotionModel2;
  @Captor
  private ArgumentCaptor<List<MiraklPromotionModel>> promotionModelListCaptor;

  @Before
  public void setUp() throws Exception {
    when(promotionResult.getActions())
        .thenReturn(asList(randomActionModel1, randomActionModel2, miraklActionModel1, miraklActionModel2));
    when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
    when(localeMappingStrategy.mapToHybrisLocale(Locale.US)).thenReturn(Locale.ENGLISH);
    when(miraklActionModel1.getPromotionId()).thenReturn(PROMOTION_ID_1);
    when(miraklActionModel2.getPromotionId()).thenReturn(PROMOTION_ID_2);
    when(miraklActionModel1.getShopId()).thenReturn(SHOP_ID_1);
    when(miraklActionModel2.getShopId()).thenReturn(SHOP_ID_2);
    when(miraklApi.getPromotions(any(MiraklGetPromotionsRequest.class))).thenReturn(miraklPromotions);
    when(miraklPromotions.getPromotions()).thenReturn(asList(miraklPromotion1, miraklPromotion2));
    when(miraklPromotion1.getPublicDescriptions()).thenReturn(singletonList(descriptionPromotion1));
    when(miraklPromotion2.getPublicDescriptions()).thenReturn(singletonList(descriptionPromotion2));
    when(descriptionPromotion1.getLocale()).thenReturn(Locale.US);
    when(descriptionPromotion2.getLocale()).thenReturn(Locale.TAIWAN);
    when(descriptionPromotion1.getValue()).thenReturn(PROMOTION_DESCRIPTION_1);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getString(MIRAKL_PROMOTIONS_MESSAGES_SEPARATOR, DEFAULT_PROMOTION_MESSAGES_SEPARATOR))
        .thenReturn(DEFAULT_PROMOTION_MESSAGES_SEPARATOR);
    when(miraklPromotionConverter.convert(miraklPromotion1, createdModel1)).thenReturn(miraklPromotionModel1);
    when(miraklPromotionConverter.convert(miraklPromotion2, createdModel2)).thenReturn(miraklPromotionModel2);
  }

  @Test
  public void populateWhenNoMiraklActions() throws Exception {
    when(promotionResult.getActions()).thenReturn(asList(randomActionModel1, randomActionModel2));

    testObj.populate(promotionResult, target);

    verifyZeroInteractions(target);
  }

  @Test
  public void populate() throws Exception {
    when(modelService.create(MiraklPromotionModel.class)).thenReturn(createdModel1, createdModel2);

    testObj.populate(promotionResult, target);

    verify(miraklApi).getPromotions(miraklRequestCaptor.capture());
    List<PromotionIdentifier> promotionIds = miraklRequestCaptor.getValue().getIds();
    assertThat(promotionIds.get(0).getPromotionId()).isEqualTo(PROMOTION_ID_1);
    assertThat(promotionIds.get(0).getShopId()).isEqualTo(SHOP_ID_1);
    assertThat(promotionIds.get(1).getPromotionId()).isEqualTo(PROMOTION_ID_2);
    assertThat(promotionIds.get(1).getShopId()).isEqualTo(SHOP_ID_2);
    verify(target).setDescription(PROMOTION_DESCRIPTION_1);
    verify(modelService).saveAll(promotionModelListCaptor.capture());
    assertThat(promotionModelListCaptor.getValue()).containsOnly(miraklPromotionModel1, miraklPromotionModel2);
  }

  @Test
  public void populateWhenNoDefaultLocaleDescriptionAvailable() throws Exception {
    when(promotionResult.getActions()).thenReturn(asList(randomActionModel1, randomActionModel2, miraklActionModel2));
    when(miraklPromotions.getPromotions()).thenReturn(singletonList(miraklPromotion2));
    when(modelService.create(MiraklPromotionModel.class)).thenReturn(createdModel2);

    testObj.populate(promotionResult, target);

    verify(target, never()).setDescription(anyString());
    verify(modelService).saveAll(promotionModelListCaptor.capture());
    assertThat(promotionModelListCaptor.getValue()).containsOnly(miraklPromotionModel2);
  }

}
