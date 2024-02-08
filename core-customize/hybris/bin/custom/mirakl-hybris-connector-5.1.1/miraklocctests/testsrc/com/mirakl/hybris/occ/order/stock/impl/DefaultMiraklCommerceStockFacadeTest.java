package com.mirakl.hybris.occ.order.stock.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;
import com.mirakl.hybris.occ.order.strategies.MiraklStockLevelStatusStrategy;
import com.mirakl.hybris.occ.stock.impl.DefaultMiraklCommerceStockFacade;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;

public class DefaultMiraklCommerceStockFacadeTest {

  private static final String OFFER_CODE = "testOffer";
  private static final String BASE_SITE = "testBaseSite";
  private static final Integer IN_STOCK_QUANTITY = 1;
  private static final Integer OUT_OF_STOCK_QUANTITY = 0;

  @Mock
  private MiraklStockLevelStatusStrategy miraklStockLevelStatusStrategy;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private OfferFacade offerFacade;
  @Mock
  private OfferModel offerModel;

  @InjectMocks
  private DefaultMiraklCommerceStockFacade facade;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(offerCodeGenerationStrategy.isOfferCode(OFFER_CODE)).thenReturn(true);
    when(offerFacade.getOfferForCode(OFFER_CODE)).thenReturn(offerModel);
  }

  @Test
  public void shouldReturnOfferStockDataWhenOfferInStock() {
    when(miraklStockLevelStatusStrategy.getStockLevelStatus(offerModel)).thenReturn(StockLevelStatus.INSTOCK);
    when(offerModel.getQuantity()).thenReturn(IN_STOCK_QUANTITY);
    final StockData stockDataForProductAndBaseSite = this.facade.getStockDataForProductAndBaseSite(OFFER_CODE, BASE_SITE);
    assertSame(StockLevelStatus.INSTOCK, stockDataForProductAndBaseSite.getStockLevelStatus());
    assertSame(Long.valueOf(IN_STOCK_QUANTITY), stockDataForProductAndBaseSite.getStockLevel());
  }

  @Test
  public void shouldReturnOfferStockDataWhenOfferOutOfStock() {
    when(miraklStockLevelStatusStrategy.getStockLevelStatus(offerModel)).thenReturn(StockLevelStatus.OUTOFSTOCK);
    when(offerModel.getQuantity()).thenReturn(OUT_OF_STOCK_QUANTITY);
    final StockData stockDataForProductAndBaseSite = this.facade.getStockDataForProductAndBaseSite(OFFER_CODE, BASE_SITE);
    assertSame(StockLevelStatus.OUTOFSTOCK, stockDataForProductAndBaseSite.getStockLevelStatus());
    assertSame(Long.valueOf(OUT_OF_STOCK_QUANTITY), stockDataForProductAndBaseSite.getStockLevel());
  }

}
