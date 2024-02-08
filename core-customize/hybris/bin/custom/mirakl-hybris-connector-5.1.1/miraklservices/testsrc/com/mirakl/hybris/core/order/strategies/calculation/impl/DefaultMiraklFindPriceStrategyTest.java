package com.mirakl.hybris.core.order.strategies.calculation.impl;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.product.services.MiraklRealtimePriceService;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.PriceValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklFindPriceStrategyTest {
  private static final String SHIPPING_FEES_JSON = "shipping-fees-json";
  private static final String OFFER_ID = "offer-id";
  private static final String ISO_CODE = "iso-code";

  @InjectMocks
  private DefaultMiraklFindPriceStrategy findPriceStrategy;

  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private SessionService sessionService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private MiraklOrderShippingFees orderShippingFees;
  @Mock
  private MiraklOrderShippingFeeOffer shippingFeeOffer;
  @Mock
  private CurrencyModel currency;
  @Mock
  private MiraklRealtimeOfferPriceRetrievalMode realtimeOfferDataRetrievalMode;
  @Mock
  private MiraklRealtimePriceService realtimePriceService;

  @Before
  public void setUp() throws Exception {
    when(orderEntry.getOfferId()).thenReturn(OFFER_ID);
    when(orderEntry.getOrder()).thenReturn(order);
    when(order.getNet()).thenReturn(false);
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getIsocode()).thenReturn(ISO_CODE);
  }

  @Test
  public void shouldFindBasePriceFromSH02SavedResponse() throws Exception {
    when(order.getShippingFeesJSON()).thenReturn(SHIPPING_FEES_JSON);
    shouldFindBasePriceFromSavedJSONResponse();
  }

  @Test
  public void shouldFindBasePriceFromCartCalculationSavedResponse() throws Exception {
    when(order.getCartCalculationJSON()).thenReturn(SHIPPING_FEES_JSON);
    shouldFindBasePriceFromSavedJSONResponse();
  }

  @Test
  public void shouldFindBasePriceFromRealtimePriceInfoIfRealtimePriceAvailableAndRealtimePriceModeIsON() throws Exception {
    when(order.getShippingFeesJSON()).thenReturn(null);
    when(order.getCartCalculationJSON()).thenReturn(null);
    when(realtimeOfferDataRetrievalMode.isActive()).thenReturn(Boolean.TRUE);

    findPriceStrategy.findBasePrice(orderEntry);

    verify(realtimePriceService).getPriceValue(orderEntry);
  }

  @Test
  public void shouldFindBasePriceFromDatabaseIfRealtimePriceModeIsOFF() throws Exception {
    final BigDecimal offerPriceFromDatabase = BigDecimal.valueOf(36);
    PriceValue priceValueFromDatabase = new PriceValue(ISO_CODE, offerPriceFromDatabase.doubleValue(), order.getNet());

    when(order.getShippingFeesJSON()).thenReturn(null);
    when(order.getCartCalculationJSON()).thenReturn(null);
    when(realtimeOfferDataRetrievalMode.isActive()).thenReturn(Boolean.FALSE);
    when(sessionService.executeInLocalView(any(SessionExecutionBody.class))).thenReturn(priceValueFromDatabase);

    findPriceStrategy.findBasePrice(orderEntry);

    verify(realtimePriceService, Mockito.never()).getPriceValue(orderEntry);
  }

  public void shouldFindBasePriceFromSavedJSONResponse() throws Exception {
    when(jsonMarshallingService.fromJson(SHIPPING_FEES_JSON, MiraklOrderShippingFees.class)).thenReturn(orderShippingFees);
    when(shippingFeeService.extractShippingFeeOffer(OFFER_ID, orderShippingFees)).thenReturn(Optional.of(shippingFeeOffer));
    BigDecimal offerPrice = BigDecimal.valueOf(100);
    when(shippingFeeOffer.getPrice()).thenReturn(offerPrice);

    PriceValue basePrice = findPriceStrategy.findBasePrice(orderEntry);

    assertThat(basePrice.getValue()).isEqualTo(offerPrice.doubleValue());
  }


}
