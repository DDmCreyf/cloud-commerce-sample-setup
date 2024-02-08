package com.mirakl.hybris.facades.product.converters.populator;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.variants.model.VariantProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklVariantOptionDataPopulatorTest {

  private static final String PRODUCT_CODE = "008973456";
  private static final Integer OFFER_STOCK = 210;
  private static final BigDecimal OFFER_PRICE = BigDecimal.valueOf(120.50);

  @InjectMocks
  private MiraklVariantOptionDataPopulator testObj;

  @Mock
  private OfferService offerService;
  @Mock
  private MiraklProductService miraklProductService;
  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  private PriceDataFactory priceDataFactory;
  @Mock
  private VariantProductModel source;
  @Mock
  private VariantOptionData target;
  @Mock
  private StockData stock;
  @Mock
  private OfferModel topOffer, randomOffer;
  @Mock
  private CurrencyModel offerCurrency;
  @Mock
  private PriceData productPriceData, offerPriceData;
  @Mock
  private OfferPricingData offerPricingData;

  @Before
  public void setUp() throws Exception {
    when(target.getStock()).thenReturn(stock);
    when(target.getCode()).thenReturn(PRODUCT_CODE);
    when(topOffer.getQuantity()).thenReturn(OFFER_STOCK);
    when(topOffer.getCurrency()).thenReturn(offerCurrency);
    when(miraklPriceService.getOfferPricing(topOffer)).thenReturn(offerPricingData);
    when(offerPricingData.getPrice()).thenReturn(OFFER_PRICE);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_PRICE, offerCurrency)).thenReturn(offerPriceData);
  }

  @Test
  public void shouldDoNothingWhenProductIsSellableByOpertator() throws Exception {
    when(miraklProductService.isSellableByOperator(source)).thenReturn(true);

    testObj.populate(source, target);

    verify(offerService, never()).getSortedOffersForProductCode(PRODUCT_CODE);
    verify(target, never()).setPriceData(any(PriceData.class));
    verify(stock, never()).setStockLevel(anyLong());
    verify(stock, never()).setStockLevelStatus(any(StockLevelStatus.class));
  }

  @Test
  public void shouldLookForOffersWhenProductIsNotSellableByOpertator() throws Exception {
    when(miraklProductService.isSellableByOperator(source)).thenReturn(false);

    testObj.populate(source, target);

    verify(offerService).getSortedOffersForProductCode(PRODUCT_CODE);
    verify(target, never()).setPriceData(any(PriceData.class));
    verify(stock, never()).setStockLevel(anyLong());
    verify(stock, never()).setStockLevelStatus(any(StockLevelStatus.class));
  }

  @Test
  public void shouldPopulateWhenProductIsNotSellableByOpertatorAndOffersAreAvailable() throws Exception {
    when(miraklProductService.isSellableByOperator(source)).thenReturn(false);
    when(offerService.getSortedOffersForProductCode(PRODUCT_CODE)).thenReturn(asList(topOffer, randomOffer));

    testObj.populate(source, target);

    verify(target).setPriceData(offerPriceData);
    verify(stock).setStockLevel(OFFER_STOCK.longValue());
    verify(stock).setStockLevelStatus(StockLevelStatus.INSTOCK);
  }

}
