package com.mirakl.hybris.core.product.services.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklPriceServiceTest {

  private static final BigDecimal PRICE_THRESHOLD_1 = BigDecimal.valueOf(19.99);
  private static final BigDecimal PRICE_THRESHOLD_2 = BigDecimal.valueOf(15.99);
  private static final BigDecimal PRICE_THRESHOLD_3 = BigDecimal.valueOf(11.99);
  private static final BigDecimal DISCOUNT_PRICE = BigDecimal.valueOf(29.99);
  private static final BigDecimal ORIGIN_PRICE = BigDecimal.valueOf(34.99);
  private static final boolean NET = true;
  private static final double MARKETPLACE_PRICE = 456.0;
  private static final String CURRENT_CURRENCY_ISOCODE = "USD";
  private static final String PRODUCT_CODE = "0987654321";

  @InjectMocks
  @Spy
  private DefaultMiraklPriceService miraklPriceService;

  @Mock
  private MiraklProductService miraklProductService;
  @Mock
  private OfferService offerService;
  @Mock
  private OfferPricingSelectionStrategy offerPricingSelectionStrategy;
  @Mock
  private ProductModel product;
  @Mock
  private OfferModel topOffer;
  @Mock
  private OfferPricingData offerPricingData;
  @Mock
  private CurrencyModel currentCurrency;

  @Before
  public void setUp() throws Exception {}

  @Test
  public void getPriceInformationsForProductWhenOfferHasBuyBox() throws Exception {
    doReturn(NET).when(miraklPriceService).isNet();
    when(product.getCode()).thenReturn(PRODUCT_CODE);
    when(offerService.getSortedOffersForProductCode(PRODUCT_CODE)).thenReturn(Collections.singletonList(topOffer));
    when(offerPricingData.getPrice()).thenReturn(BigDecimal.valueOf(MARKETPLACE_PRICE));
    when(topOffer.getCurrency()).thenReturn(currentCurrency);
    when(offerPricingSelectionStrategy.selectApplicableOfferPricing(topOffer)).thenReturn(offerPricingData);
    when(currentCurrency.getIsocode()).thenReturn(CURRENT_CURRENCY_ISOCODE);
    when(miraklProductService.isSellableByOperator(product)).thenReturn(false);

    List<PriceInformation> priceInformations = miraklPriceService.getPriceInformationsForProduct(product);

    assertThat(priceInformations).hasSize(1);
    assertThat(priceInformations.get(0).getPriceValue().getValue()).isEqualTo(MARKETPLACE_PRICE);
    assertThat(priceInformations.get(0).getPriceValue().getCurrencyIso()).isEqualTo(CURRENT_CURRENCY_ISOCODE);
  }

  @Test
  public void getVolumePriceForQuantity() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    when(offerPricingSelectionStrategy.selectApplicableOfferPricing(topOffer)).thenReturn(offerPricing);
    offerPricing.setVolumePrices(asList(getVolumePricing(PRICE_THRESHOLD_1, 1),
        getVolumePricing(PRICE_THRESHOLD_2, 5), getVolumePricing(PRICE_THRESHOLD_3, 10)));

    OfferVolumePricingData volumePriceForQuantity = miraklPriceService.getVolumePriceForQuantity(topOffer, 6);

    assertThat(volumePriceForQuantity.getPrice()).isEqualTo(PRICE_THRESHOLD_2);
  }

  protected OfferVolumePricingData getVolumePricing(BigDecimal originPrice, Integer qtyThreshold) {
    OfferVolumePricingData volumePricing = new OfferVolumePricingData();
    volumePricing.setUnitOriginPrice(originPrice);
    volumePricing.setQuantityThreshold(qtyThreshold);
    return volumePricing;
  }

  @Test
  public void isDiscountedPriceWithNoDiscount() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isFalse();
  }

  @Test
  public void isDiscountedPriceWithNoDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isTrue();
  }

  @Test
  public void isDiscountedPriceWithActiveStartDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isTrue();
  }

  @Test
  public void isDiscountedPriceWithFutureStartDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().plusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isFalse();
  }

  @Test
  public void isDiscountedPriceWithActiveEndDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountEndDate(new DateTime().plusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isTrue();
  }

  @Test
  public void isDiscountedPriceWithPastEndDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountEndDate(new DateTime().minusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isFalse();
  }

  @Test
  public void isDiscountedPriceWithActiveDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(2).toDate());
    offerPricing.setDiscountEndDate(new DateTime().plusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isTrue();
  }

  @Test
  public void isDiscountedPriceWithFutureDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().plusDays(2).toDate());
    offerPricing.setDiscountEndDate(new DateTime().plusDays(5).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isFalse();
  }

  @Test
  public void isDiscountedPriceWithPastDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(5).toDate());
    offerPricing.setDiscountEndDate(new DateTime().minusDays(2).toDate());

    boolean isDiscountedPrice = miraklPriceService.isDiscountedPrice(offerPricing);

    assertThat(isDiscountedPrice).isFalse();
  }

  @Test
  public void getPriceWithNoDiscount() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(ORIGIN_PRICE);
  }

  @Test
  public void getPriceWithNoDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(DISCOUNT_PRICE);
  }

  @Test
  public void getPriceWithActiveStartDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(DISCOUNT_PRICE);
  }

  @Test
  public void getPriceWithFutureStartDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().plusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(ORIGIN_PRICE);
  }

  @Test
  public void getPriceWithActiveEndDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountEndDate(new DateTime().plusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(DISCOUNT_PRICE);
  }

  @Test
  public void getPriceWithPastEndDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountEndDate(new DateTime().minusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(ORIGIN_PRICE);
  }

  @Test
  public void getPriceWithActiveDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(2).toDate());
    offerPricing.setDiscountEndDate(new DateTime().plusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(DISCOUNT_PRICE);
  }

  @Test
  public void getPriceWithFutureDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().plusDays(2).toDate());
    offerPricing.setDiscountEndDate(new DateTime().plusDays(5).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(ORIGIN_PRICE);
  }

  @Test
  public void getPriceWithPastDiscountPeriod() throws Exception {
    OfferPricingData offerPricing = new OfferPricingData();
    offerPricing.setUnitOriginPrice(ORIGIN_PRICE);
    offerPricing.setUnitDiscountPrice(DISCOUNT_PRICE);
    offerPricing.setDiscountStartDate(new DateTime().minusDays(5).toDate());
    offerPricing.setDiscountEndDate(new DateTime().minusDays(2).toDate());

    BigDecimal price = miraklPriceService.getPrice(offerPricing);

    assertThat(price).isEqualTo(ORIGIN_PRICE);
  }


}
