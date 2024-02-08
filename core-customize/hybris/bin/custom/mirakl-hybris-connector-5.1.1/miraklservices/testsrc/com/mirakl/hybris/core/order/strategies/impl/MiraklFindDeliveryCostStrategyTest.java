package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.util.PriceValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklFindDeliveryCostStrategyTest {

  private static final double MARKETPLACE_DELIVERY_COST = 12.50;
  private static final double OPERATOR_DELIVERY_COST = 10.00;
  private static final String CURRENCY_ISO_CODE = "currencyIsoCode";

  @InjectMocks
  private MiraklFindDeliveryCostStrategy testObj = new MiraklFindDeliveryCostStrategy();

  @Mock
  private AbstractOrderModel order;
  @Mock
  private PriceValue priceValue;
  @Mock
  private MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;
  @Mock
  private FindDeliveryCostStrategy operatorFindDeliveryCostStrategy;

  @Before
  public void setUp() throws JaloDeliveryModeException {
    when(operatorFindDeliveryCostStrategy.getDeliveryCost(order)).thenReturn(priceValue);
    when(operatorFindDeliveryCostStrategy.getDeliveryCost(null)).thenThrow(new IllegalArgumentException());
    when(priceValue.getCurrencyIso()).thenReturn(CURRENCY_ISO_CODE);
    when(priceValue.isNet()).thenReturn(true);
    when(priceValue.getValue()).thenReturn(OPERATOR_DELIVERY_COST);
    when(marketplaceDeliveryCostStrategy.getMarketplaceDeliveryCost(order)).thenReturn(MARKETPLACE_DELIVERY_COST);
  }

  @Test
  public void getsCost() {
    PriceValue result = testObj.getDeliveryCost(order);

    assertThat(result.getValue()).isEqualTo(OPERATOR_DELIVERY_COST + MARKETPLACE_DELIVERY_COST);
    assertThat(result.getCurrencyIso()).isEqualTo(CURRENCY_ISO_CODE);
    assertThat(result.isNet()).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getCostThrowsIllegalArgumentExceptionIfOrderIsNull() {
    testObj.getDeliveryCost(null);
  }

}
