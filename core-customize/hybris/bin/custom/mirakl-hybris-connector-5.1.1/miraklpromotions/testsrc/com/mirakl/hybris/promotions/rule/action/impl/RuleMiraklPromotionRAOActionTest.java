package com.mirakl.hybris.promotions.rule.action.impl;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import org.apache.commons.configuration.Configuration;
import org.drools.core.WorkingMemory;
import org.drools.core.spi.KnowledgeHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklDiscountRAO;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleMiraklPromotionRAOActionTest {

  private static final String SHOP_ID_1 = "1345";
  private static final String SHOP_ID_2 = "9473";
  private static final String PROMOTION_ID_1 = "AMOUNT_OFF_2017-10-20-17-35";
  private static final String PROMOTION_ID_2 = "AMOUNT_OFF_2016-10-20-17-47";

  @Spy
  @InjectMocks
  private RuleMiraklPromotionRAOAction testObj;

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private RuleActionContext context;
  @Mock
  private MiraklPromotionRAO miraklPromotionRao1, miraklPromotionRao2;
  @Mock
  private RuleEngineCalculationService ruleEngineCalculationService;
  @Mock
  private BigDecimal deducedAmount1, deducedAmount2;
  @Mock
  private MiraklDiscountRAO miraklDiscountRao1, miraklDiscountRao2;
  @Mock
  private KnowledgeHelper knowledgeHelper;
  @Mock
  private WorkingMemory workingMemory;

  private CartRAO cartRao;
  private OrderEntryRAO orderEntryRao;
  private RuleEngineResultRAO ruleEngineResultRao;


  @Before
  public void setUp() throws Exception {
    cartRao = new CartRAO();
    orderEntryRao = new OrderEntryRAO();
    ruleEngineResultRao = new RuleEngineResultRAO();
    cartRao.setEntries(newHashSet(orderEntryRao));
    cartRao.setAppliedMiraklPromotions(asList(miraklPromotionRao1, miraklPromotionRao2));
    ruleEngineResultRao.setActions(new LinkedHashSet<>());
    when(context.getValue(CartRAO.class)).thenReturn(cartRao);
    when(miraklPromotionRao1.getDeducedAmount()).thenReturn(deducedAmount1);
    when(miraklPromotionRao2.getDeducedAmount()).thenReturn(deducedAmount2);
    when(ruleEngineCalculationService.addOrderLevelDiscount(cartRao, true, deducedAmount1)).thenReturn(miraklDiscountRao1);
    when(ruleEngineCalculationService.addOrderLevelDiscount(cartRao, true, deducedAmount2)).thenReturn(miraklDiscountRao2);
    when(miraklPromotionRao1.getPromotionId()).thenReturn(PROMOTION_ID_1);
    when(miraklPromotionRao2.getPromotionId()).thenReturn(PROMOTION_ID_2);
    when(miraklPromotionRao1.getShopId()).thenReturn(SHOP_ID_1);
    when(miraklPromotionRao2.getShopId()).thenReturn(SHOP_ID_2);
    when(context.getRuleEngineResultRao()).thenReturn(ruleEngineResultRao);
    when(context.getCartRao()).thenReturn(cartRao);
  }

  @Test
  public void performAction() {
    testObj.performActionInternal(context);

    verify(testObj).performAction(context, miraklPromotionRao1);
    verify(testObj).performAction(context, miraklPromotionRao2);
    verify(miraklDiscountRao1).setShopId(SHOP_ID_1);
    verify(miraklDiscountRao2).setShopId(SHOP_ID_2);
    verify(miraklDiscountRao1).setPromotionId(PROMOTION_ID_1);
    verify(miraklDiscountRao2).setPromotionId(PROMOTION_ID_2);
  }

  @Test
  public void shouldDoNothingWhenNoPromotionApplied() {
    cartRao.setAppliedMiraklPromotions(null);

    testObj.performActionInternal(context);

    verifyZeroInteractions(ruleEngineCalculationService);
  }

}
