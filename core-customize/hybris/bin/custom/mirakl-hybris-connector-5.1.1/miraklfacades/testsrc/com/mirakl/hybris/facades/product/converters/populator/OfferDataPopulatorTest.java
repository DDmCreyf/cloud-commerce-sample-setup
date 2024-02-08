package com.mirakl.hybris.facades.product.converters.populator;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferDataPopulatorTest {

  private static final String OFFER_CODE = "offer-code";
  private static final String OFFER_STATE_NAME = "offerState";
  private static final Date OFFER_AVAILABLE_END_DATE = new Date();
  private static final Date OFFER_AVAILABLE_START_DATE = new Date();
  private static final String OFFER_DESCRIPTION = "Offer description";
  private static final BigDecimal OFFER_DISCOUNT_PRICE = BigDecimal.valueOf(75);
  private static final Date OFFER_DISCOUNT_START_DATE = new DateTime().minusDays(100).toDate();
  private static final Date OFFER_DISCOUNT_END_DATE = new DateTime().plusDays(100).toDate();
  private static final String OFFER_ID = "4567820";
  private static final Integer OFFER_LEAD_TIME_TO_SHIP = 10;
  private static final BigDecimal OFFER_MIN_SHIPPING_PRICE = BigDecimal.valueOf(10);
  private static final BigDecimal OFFER_MIN_SHIPPING_PRICE_ADDITIONAL = BigDecimal.valueOf(25);
  private static final BigDecimal OFFER_ORIGIN_PRICE = BigDecimal.valueOf(120);
  private static final BigDecimal OFFER_PRICE = BigDecimal.valueOf(100);
  private static final String OFFER_PRICE_ADDITIONAL_INFO = "Additional Info";
  private static final String OFFER_PRODUCT_CODE = "product_code";
  private static final Integer OFFER_QUANTITY = 456;
  private static final BigDecimal OFFER_TOTAL_PRICE = BigDecimal.valueOf(147);
  private static final String SHOP_NAME = "test shop";
  private static final String SHOP_CODE = "testshopcode";
  private static final Integer SHOP_EVALUATION_COUNT = 1500;
  private static final Double SHOP_GRADE = 4.5;
  private static final String OFFER_STATE_CODE = "offerStateCode";

  @InjectMocks
  private OfferDataPopulator populator;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private OfferModel offer;
  @Mock
  private ShopModel shop;
  @Mock
  private CurrencyData currencyData;
  @Mock
  private PriceDataFactory priceDataFactory;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  protected OfferDataVolumePricePopulator offerDataVolumePricePopulator;
  @Mock
  private EnumerationValueModel enumValue;
  @Mock
  private PriceData offerDiscount;
  @Mock
  private PriceData offerMinShippingPrice;
  @Mock
  private PriceData offerMinShippingPriceAdditional;
  @Mock
  private PriceData offerOriginPrice;
  @Mock
  private PriceData offerPrice;
  @Mock
  private PriceData offerTotalPrice;
  @Mock
  private OfferPricingData offerPricingData;
  @Mock
  private CurrencyModel currencyModel;
  @Mock
  private OfferState offerState;


  @Before
  public void setup() {
    when(enumerationService.getEnumerationName(offerState)).thenReturn(OFFER_STATE_NAME);
    when(offerState.getCode()).thenReturn(OFFER_STATE_CODE);
    when(offer.getCurrency()).thenReturn(currencyModel);

    when(priceDataFactory.create(PriceDataType.BUY, OFFER_DISCOUNT_PRICE, currencyModel)).thenReturn(offerDiscount);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_MIN_SHIPPING_PRICE, currencyModel)).thenReturn(offerMinShippingPrice);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_MIN_SHIPPING_PRICE_ADDITIONAL, currencyModel))
        .thenReturn(offerMinShippingPriceAdditional);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_ORIGIN_PRICE, currencyModel)).thenReturn(offerOriginPrice);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_PRICE, currencyModel)).thenReturn(offerPrice);
    when(priceDataFactory.create(PriceDataType.BUY, OFFER_TOTAL_PRICE, currencyModel)).thenReturn(offerTotalPrice);

    when(offerDiscount.getValue()).thenReturn(OFFER_DISCOUNT_PRICE);
    when(offerMinShippingPrice.getValue()).thenReturn(OFFER_MIN_SHIPPING_PRICE);
    when(offerMinShippingPriceAdditional.getValue()).thenReturn(OFFER_MIN_SHIPPING_PRICE_ADDITIONAL);
    when(offerOriginPrice.getValue()).thenReturn(OFFER_ORIGIN_PRICE);
    when(offerPrice.getValue()).thenReturn(OFFER_PRICE);
    when(offerTotalPrice.getValue()).thenReturn(OFFER_TOTAL_PRICE);

    when(shop.getName()).thenReturn(SHOP_NAME);
    when(shop.getId()).thenReturn(SHOP_CODE);
    when(shop.getEvaluationCount()).thenReturn(SHOP_EVALUATION_COUNT);
    when(shop.getGrade()).thenReturn(SHOP_GRADE);
    when(miraklPriceService.getOfferPricing(offer)).thenReturn(offerPricingData);
    when(miraklPriceService.getOfferTotalPrice(offer, offerPricingData)).thenReturn(OFFER_TOTAL_PRICE);
    when(offer.getAvailableEndDate()).thenReturn(OFFER_AVAILABLE_END_DATE);
    when(offer.getAvailableStartDate()).thenReturn(OFFER_AVAILABLE_START_DATE);
    when(offer.getDescription()).thenReturn(OFFER_DESCRIPTION);
    when(offerPricingData.getUnitDiscountPrice()).thenReturn(OFFER_DISCOUNT_PRICE);
    when(offer.getId()).thenReturn(OFFER_ID);
    when(offer.getLeadTimeToShip()).thenReturn(OFFER_LEAD_TIME_TO_SHIP);
    when(offer.getMinShippingPrice()).thenReturn(OFFER_MIN_SHIPPING_PRICE);
    when(offer.getMinShippingPriceAdditional()).thenReturn(OFFER_MIN_SHIPPING_PRICE_ADDITIONAL);
    when(offerPricingData.getUnitOriginPrice()).thenReturn(OFFER_ORIGIN_PRICE);
    when(offerPricingData.getPrice()).thenReturn(OFFER_PRICE);
    when(offerPricingData.getDiscountStartDate()).thenReturn(OFFER_DISCOUNT_START_DATE);
    when(offerPricingData.getDiscountEndDate()).thenReturn(OFFER_DISCOUNT_END_DATE);
    when(offer.getPriceAdditionalInfo()).thenReturn(OFFER_PRICE_ADDITIONAL_INFO);
    when(offer.getProductCode()).thenReturn(OFFER_PRODUCT_CODE);
    when(offer.getQuantity()).thenReturn(OFFER_QUANTITY);
    when(offer.getShop()).thenReturn(shop);
    when(offer.getState()).thenReturn(offerState);
    when(offerCodeGenerationStrategy.generateCode(OFFER_ID)).thenReturn(OFFER_CODE);
  }

  @Test
  public void shouldConvertOfferProperly() {
    OfferData result = new OfferData();
    populator.populate(offer, result);

    assertThat(result.getAvailableEndDate()).isEqualTo(OFFER_AVAILABLE_END_DATE);
    assertThat(result.getAvailableStartDate()).isEqualTo(OFFER_AVAILABLE_START_DATE);
    assertThat(result.getDescription()).isEqualTo(OFFER_DESCRIPTION);
    assertThat(result.getDiscountEndDate()).isEqualTo(OFFER_DISCOUNT_END_DATE);
    assertThat(result.getDiscountPrice().getValue()).isEqualTo(OFFER_DISCOUNT_PRICE);
    assertThat(result.getDiscountStartDate()).isEqualTo(OFFER_DISCOUNT_START_DATE);
    assertThat(result.getId()).isEqualTo(OFFER_ID);
    assertThat(result.getLeadTimeToShip()).isEqualTo(OFFER_LEAD_TIME_TO_SHIP);
    assertThat(result.getMinShippingPrice().getValue()).isEqualTo(OFFER_MIN_SHIPPING_PRICE);
    assertThat(result.getMinShippingPriceAdditional().getValue()).isEqualTo(OFFER_MIN_SHIPPING_PRICE_ADDITIONAL);
    assertThat(result.getOriginPrice().getValue()).isEqualTo(OFFER_ORIGIN_PRICE);
    assertThat(result.getPrice().getValue()).isEqualTo(OFFER_PRICE);
    assertThat(result.getPriceAdditionalInfo()).isEqualTo(OFFER_PRICE_ADDITIONAL_INFO);
    assertThat(result.getProductCode()).isEqualTo(OFFER_PRODUCT_CODE);
    assertThat(result.getQuantity()).isEqualTo(OFFER_QUANTITY);
    assertThat(result.getShopCode()).isEqualTo(SHOP_CODE);
    assertThat(result.getShopEvaluationCount()).isEqualTo(SHOP_EVALUATION_COUNT);
    assertThat(result.getShopGrade()).isEqualTo(SHOP_GRADE);
    assertThat(result.getShopName()).isEqualTo(SHOP_NAME);
    assertThat(result.getState()).isEqualTo(OFFER_STATE_NAME);
    assertThat(result.getStateCode()).isEqualTo(OFFER_STATE_CODE);
    assertThat(result.getTotalPrice().getValue()).isEqualTo(OFFER_TOTAL_PRICE);
    assertThat(result.getCode()).isEqualTo(OFFER_CODE);
  }

  @Test
  public void shouldConvertAdditionalInfoWhenInDiscount() {
    OfferData result = new OfferData();
    populator.populate(offer, result);

    assertThat(result.getPriceAdditionalInfo()).isEqualTo(OFFER_PRICE_ADDITIONAL_INFO);
  }

}
