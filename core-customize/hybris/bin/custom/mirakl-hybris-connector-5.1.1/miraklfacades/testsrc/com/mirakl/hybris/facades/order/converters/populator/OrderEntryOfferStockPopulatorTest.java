package com.mirakl.hybris.facades.order.converters.populator;

import static java.lang.Long.valueOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryOfferStockPopulatorTest {

  private static final String OFFER_ID = "offer-id";

  @InjectMocks
  private OrderEntryOfferStockPopulator populator;

  @Mock
  private OfferService offerService;
  @Mock
  private AbstractOrderEntryModel orderEntryModel;
  @Mock
  private OfferModel offerModel;
  @Mock
  private OrderEntryData orderEntryData;
  @Mock
  private ProductData productData;
  @Mock
  private StockData stockData;

  @Before
  public void setUp() {
    when(orderEntryModel.getOfferId()).thenReturn(OFFER_ID);
    when(offerService.getOfferForIdIgnoreSearchRestrictions(OFFER_ID)).thenReturn(offerModel);
    when(orderEntryData.getProduct()).thenReturn(productData);
    when(productData.getStock()).thenReturn(stockData);
  }

  @Test
  public void shouldPopulateWhenOfferInStock() {
    int offerStockQuantity = 3;
    when(offerModel.getQuantity()).thenReturn(offerStockQuantity);

    populator.populate(orderEntryModel, orderEntryData);

    verify(stockData).setStockLevel(valueOf(offerStockQuantity));
    verify(stockData).setStockLevelStatus(StockLevelStatus.INSTOCK);
  }

  @Test
  public void shouldPopulateWhenOfferOutOfStock() {
    int offerStockQuantity = 0;
    when(offerModel.getQuantity()).thenReturn(offerStockQuantity);

    populator.populate(orderEntryModel, orderEntryData);

    verify(stockData).setStockLevel(valueOf(offerStockQuantity));
    verify(stockData).setStockLevelStatus(StockLevelStatus.OUTOFSTOCK);
  }

  @Test
  public void shouldDoNothingWhenNotAMarketplaceEntry() {
    when(orderEntryModel.getOfferId()).thenReturn(null);

    OrderEntryData orderEntryData = new OrderEntryData();
    populator.populate(orderEntryModel, orderEntryData);

    verifyZeroInteractions(stockData);
  }

  @Test
  public void shouldMarkOutOfStockIfOfferNotFound() {
    populator.populate(orderEntryModel, orderEntryData);

    verify(stockData).setStockLevel(valueOf(0));
    verify(stockData).setStockLevelStatus(StockLevelStatus.OUTOFSTOCK);
  }


}
