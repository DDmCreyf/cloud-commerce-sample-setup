package com.mirakl.hybris.occ.order.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklStockLevelStatusStrategyTest {

  private static final Integer IN_STOCK_QUANTITY = 1;
  private static final Integer OUT_OF_STOCK_QUANTITY = 0;

  @Mock
  private OfferModel offerModel;

  @InjectMocks
  private DefaultMiraklStockLevelStatusStrategy strategy;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldReturnInStockWhenOfferIsInStock() {
    when(offerModel.getQuantity()).thenReturn(IN_STOCK_QUANTITY);
    final StockLevelStatus stockLevelStatus = this.strategy.getStockLevelStatus(offerModel);
    assertSame(stockLevelStatus, StockLevelStatus.INSTOCK);
  }

  @Test
  public void shouldReturnOutOfStockWhenOfferIsOutOfStock() {
    when(offerModel.getQuantity()).thenReturn(OUT_OF_STOCK_QUANTITY);
    final StockLevelStatus stockLevelStatus = this.strategy.getStockLevelStatus(offerModel);
    assertSame(stockLevelStatus, StockLevelStatus.OUTOFSTOCK);
  }
}
