package com.mirakl.hybris.promotions.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.promotions.daos.MiraklPromotionDao;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklPromotionServiceTest {

  private static final String PROMOTION_SHOP_ID = "2865";
  private static final String PROMOTION_INTERNAL_ID = "get 10% on all offers";

  @InjectMocks
  private DefaultMiraklPromotionService testObj;

  @Mock
  private MiraklPromotionDao miraklPromotionDao;
  @Mock
  private MiraklPromotionModel randomPromotion, rewardPromotion, outdatedRewardPromotion, triggerPromotion;
  @Mock
  private OfferModel offer;

  @Before
  public void setUp() throws Exception {
    when(miraklPromotionDao.findMiraklPromotion(PROMOTION_SHOP_ID, PROMOTION_INTERNAL_ID)).thenReturn(randomPromotion);
    when(offer.getRewardPromotions()).thenReturn(Collections.singleton(rewardPromotion));
    when(offer.getTriggerPromotions()).thenReturn(new HashSet<>(asList(triggerPromotion, outdatedRewardPromotion)));
    when(outdatedRewardPromotion.getEndDate()).thenReturn(new DateTime().withYear(1900).toDate());
    when(miraklPromotionDao.find(Mockito.eq(singletonMap(MiraklPromotionModel.SHOPID, PROMOTION_SHOP_ID))))
        .thenReturn(asList(randomPromotion, rewardPromotion, outdatedRewardPromotion, triggerPromotion));
  }

  @Test
  public void getPromotion() {
    MiraklPromotionModel output = testObj.getPromotion(PROMOTION_SHOP_ID, PROMOTION_INTERNAL_ID);

    assertThat(output).isEqualTo(randomPromotion);
  }

  @Test
  public void getAllPromotionsConcerningOffer() {
    Collection<MiraklPromotionModel> promotions = testObj.getPromotionsForOffer(offer, false);

    assertThat(promotions).containsOnly(rewardPromotion, triggerPromotion, outdatedRewardPromotion);
  }

  @Test
  public void getActivePromotionsOnlyConcerningOffer() {
    Collection<MiraklPromotionModel> promotions = testObj.getPromotionsForOffer(offer, true);

    assertThat(promotions).containsOnly(rewardPromotion, triggerPromotion);
  }

  @Test
  public void getPromotionsForShop() {
    Collection<MiraklPromotionModel> promotions = testObj.getPromotionsForShop(PROMOTION_SHOP_ID, false);

    assertThat(promotions).containsOnly(randomPromotion, rewardPromotion, outdatedRewardPromotion, triggerPromotion);
  }

  @Test
  public void getActivePromotionsForShopOnly() {
    Collection<MiraklPromotionModel> promotions = testObj.getPromotionsForShop(PROMOTION_SHOP_ID, true);

    assertThat(promotions).containsOnly(randomPromotion, rewardPromotion, triggerPromotion);
  }
}
