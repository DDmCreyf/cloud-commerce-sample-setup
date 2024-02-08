package com.mirakl.hybris.promotions.action.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.promotions.model.MiraklRuleBasedOrderAdjustTotalActionModel;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklDiscountRAO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderAdjustTotalActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionActionService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderAdjustTotalActionStrategyTest {

  private static final String PROMOTION_ID = "AMOUNT_OFF_2017-10-20-17-35";
  private static final String SHOP_ID = "2907";

  @InjectMocks
  @Spy
  private MiraklOrderAdjustTotalActionStrategy testObj;

  private Class<RuleBasedOrderAdjustTotalActionModel> promotionActionClass = RuleBasedOrderAdjustTotalActionModel.class;

  @Mock
  private DiscountRAO wrongDao;
  @Mock
  private MiraklDiscountRAO miraklDiscountRAO;
  @Mock
  private PromotionResultModel promoResult;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklRuleBasedOrderAdjustTotalActionModel promotionAction;
  @Mock
  private PromotionActionService promotionActionService;

  @Before
  public void setUp() throws Exception {
    testObj.setPromotionAction(promotionActionClass);
    when(miraklDiscountRAO.getPromotionId()).thenReturn(PROMOTION_ID);
    when(miraklDiscountRAO.getShopId()).thenReturn(SHOP_ID);
    doReturn(promotionAction).when(testObj).getAction(promoResult, miraklDiscountRAO);
  }

  @Test
  public void createOrderAdjustTotalActionWithWrongAction() throws Exception {
    MiraklRuleBasedOrderAdjustTotalActionModel result = testObj.createOrderAdjustTotalAction(promoResult, wrongDao);

    assertThat(result).isNull();
  }

  @Test
  public void createOrderAdjustTotalAction() throws Exception {
    testObj.createOrderAdjustTotalAction(promoResult, miraklDiscountRAO);

    verify(promotionAction).setPromotionId(PROMOTION_ID);
    verify(promotionAction).setShopId(SHOP_ID);
  }

}
