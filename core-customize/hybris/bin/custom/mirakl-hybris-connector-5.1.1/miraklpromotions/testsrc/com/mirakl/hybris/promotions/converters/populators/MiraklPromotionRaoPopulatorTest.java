package com.mirakl.hybris.promotions.converters.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.promotion.MiraklAppliedPromotion;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklPromotionRaoPopulatorTest {

  private static final String SHOP_ID = "2907";
  private static final int OFFERED_QUANTITY = 5;
  private static final String PROMOTION_ID = "AMOUNT_OFF_2017-10-20-17-35";

  @InjectMocks
  private MiraklPromotionRaoPopulator testObj;
  @Mock
  private Pair<MiraklAppliedPromotion, String> source;
  @Mock
  private MiraklAppliedPromotion miraklAppliedPromotion;
  @Mock
  private BigDecimal deducedAmount;

  @Before
  public void setUp() throws Exception {
    when(source.getLeft()).thenReturn(miraklAppliedPromotion);
    when(source.getRight()).thenReturn(SHOP_ID);
    when(miraklAppliedPromotion.getDeducedAmount()).thenReturn(deducedAmount);
    when(miraklAppliedPromotion.getOfferedQuantity()).thenReturn(OFFERED_QUANTITY);
    when(miraklAppliedPromotion.getId()).thenReturn(PROMOTION_ID);
  }

  @Test
  public void populate() throws Exception {
    MiraklPromotionRAO target = new MiraklPromotionRAO();

    testObj.populate(source, target);

    assertThat(target.getPromotionId()).isEqualTo(PROMOTION_ID);
    assertThat(target.getDeducedAmount()).isEqualTo(deducedAmount);
    assertThat(target.getShopId()).isEqualTo(SHOP_ID);
    assertThat(target.getOfferedQuantity()).isEqualTo(OFFERED_QUANTITY);
  }

}
