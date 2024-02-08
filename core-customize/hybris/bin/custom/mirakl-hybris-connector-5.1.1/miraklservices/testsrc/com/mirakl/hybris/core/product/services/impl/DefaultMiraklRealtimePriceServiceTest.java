package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.domain.offer.MiraklOfferMinimumShipping;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.MiraklRealtimeOfferService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.PriceValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRealtimePriceServiceTest {
  private static final String OFFER_ID = "offer-id";
  private static final String ISO_CODE = "iso-code";
  @InjectMocks
  private DefaultMiraklRealtimePriceService defaultMiraklRealtimePriceService;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private MiraklRealtimeOfferService realtimeOfferService;
  @Mock
  private MiraklPriceService priceService;
  @Mock
  private Converter<MiraklOfferPricing, OfferPricingData> offerPricingDataConverter;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private CurrencyModel currency;
  @Mock
  private MiraklOffer miraklOfferFromApi;
  @Mock
  private MiraklOfferPricing realtimeOfferPricing;
  @Mock
  private OfferPricingData offerPricingData;
  @Mock
  private OfferVolumePricingData offerVolumePricingData;


  public void setUpPriceValueTest() {
    when(orderEntry.getOrder()).thenReturn(order);
    when(orderEntry.getOfferId()).thenReturn(OFFER_ID);
    when(order.getNet()).thenReturn(false);
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getIsocode()).thenReturn(ISO_CODE);
  }

  @Test
  public void shouldRetrieveRealtimePriceAndFindBasePriceFromRealtimePriceInfoIfNoRealtimePriceAvailable() {
    setUpPriceValueTest();

    BigDecimal fetchedRealtimePriceValue = BigDecimal.valueOf(11.04);
    when(realtimeOfferPricing.getPrice()).thenReturn(fetchedRealtimePriceValue);
    when(realtimeOfferService.getOffer(anyString())).thenReturn(miraklOfferFromApi);
    when(miraklOfferFromApi.getApplicablePricing()).thenReturn(realtimeOfferPricing);
    when(offerPricingDataConverter.convert(realtimeOfferPricing)).thenReturn(offerPricingData);
    when(priceService.getVolumePriceForQuantity(eq(offerPricingData), anyLong())).thenReturn(offerVolumePricingData);
    when(offerVolumePricingData.getPrice()).thenReturn(fetchedRealtimePriceValue);

    PriceValue basePrice = defaultMiraklRealtimePriceService.getPriceValue(orderEntry);

    verify(jsonMarshallingService, never()).fromJson(any(), eq(MiraklOfferPricing.class));

    assertThat(basePrice.getValue()).isEqualTo(realtimeOfferPricing.getPrice().doubleValue());
  }

  @Test
  public void testGetOfferTotalPriceWithShipping() {
    MiraklOfferOnProduct offer = mock(MiraklOfferOnProduct.class);
    MiraklOfferMinimumShipping shippingPrice = mock(MiraklOfferMinimumShipping.class);

    BigDecimal offerPrice = new BigDecimal("100");
    BigDecimal shippingPriceValue = new BigDecimal("10");
    BigDecimal expectedPrice = offerPrice.add(shippingPriceValue);

    when(offer.getPrice()).thenReturn(offerPrice);
    when(offer.getMinShipping()).thenReturn(shippingPrice);
    when(shippingPrice.getPrice()).thenReturn(shippingPriceValue);

    assertThat(defaultMiraklRealtimePriceService.getOfferTotalPrice(offer)).isEqualByComparingTo(expectedPrice);
  }

  @Test
  public void testGetOfferTotalPriceWithoutShipping() {
    MiraklOfferOnProduct offer = mock(MiraklOfferOnProduct.class);

    BigDecimal offerPrice = new BigDecimal("100");

    when(offer.getPrice()).thenReturn(offerPrice);
    when(offer.getMinShipping()).thenReturn(null);

    assertThat(defaultMiraklRealtimePriceService.getOfferTotalPrice(offer)).isEqualByComparingTo(offerPrice);
  }

}
