package com.mirakl.hybris.core.order.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.client.mmp.front.domain.shipping.MiraklShippingFeeType;
import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestFactory;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShippingFeeServiceTest {

  private static final String OFFER_ID = "offerId";
  private static final String SHOP_ID = "shopId";
  private static final String SHIPPING_FEES_JSON = "shippingFeesJSON";
  private static final String SELECTED_SHIPPING_OPTION_CODE = "selectedShippingOptionCode";
  private static final String SELECTED_SHIPPING_OPTION_LABEL = "selectedShippingOptionLabel";
  private static final String OFFER_1 = "offer1";
  private static final String OFFER_2 = "offer2";
  private static final String ORIGINAL_SHIPPING_CODE_FOR_OFFER_1 = "originalShippingCodeForOffer1";
  private static final int FIRST_OFFER_QUANTITY = 2;
  private static final int SECOND_OFFER_QUANTITY = 1;
  private static final double LINE_SHIPPING_PRICE = 10.0;
  private static final int LEAD_TIME_TO_SHIP = 1;
  private static final String SHIPPING_ZONE_CODE = "FR";
  private static final String CURRENCY_NAME = "EUR";

  @InjectMocks
  private DefaultShippingFeeService testObj;

  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private MiraklGetShippingRatesRequestFactory shippingRatesRequestFactory;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  @Mock
  private ShippingZoneStrategy shippingZoneStrategy;
  @Mock
  private Converter<Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer>, AbstractOrderEntryModel> orderEntryShippingConverter;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private MiraklOrderShippingFee shippingFee, firstShippingFee, secondShippingFee;
  @Mock
  private MiraklOrderShippingFeeOffer offer, firstOffer, secondOffer;
  @Mock
  private MiraklOrderShippingFeeError error;
  @Mock
  private MiraklShippingFeeType selectedShippingType, firstShippingType, secondShippingType;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private AbstractOrderEntryModel firstOrderEntry, secondOrderEntry;
  @Mock
  private MiraklOrderShippingFees shippingRates;
  @Mock
  private MiraklGetShippingRatesRequest request;
  @Mock
  private CurrencyModel currency;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    when(shippingFees.getOrders()).thenReturn(singletonList(shippingFee));
    when(shippingFees.getErrors()).thenReturn(singletonList(error));
    when(shippingFee.getOffers()).thenReturn(singletonList(offer));
    when(shippingFee.getShippingTypes()).thenReturn(asList(firstShippingType, secondShippingType));
    when(offer.getId()).thenReturn(OFFER_ID);
    when(error.getOfferId()).thenReturn(OFFER_ID);

    when(jsonMarshallingService.fromJson(SHIPPING_FEES_JSON, MiraklOrderShippingFees.class)).thenReturn(shippingFees);
    when(jsonMarshallingService.toJson(shippingFees)).thenReturn(SHIPPING_FEES_JSON);

    when(order.getMarketplaceEntries()).thenReturn(asList(firstOrderEntry, secondOrderEntry));
    when(firstOrderEntry.getOfferId()).thenReturn(OFFER_1);
    when(secondOrderEntry.getOfferId()).thenReturn(OFFER_2);
    when(firstOffer.getId()).thenReturn(OFFER_1);
    when(secondOffer.getId()).thenReturn(OFFER_2);
    when(firstOrderEntry.getQuantity()).thenReturn(Long.valueOf(FIRST_OFFER_QUANTITY));
    when(secondOrderEntry.getQuantity()).thenReturn(Long.valueOf(SECOND_OFFER_QUANTITY));

    when(shippingRatesRequestFactory.createShippingRatesRequest(eq(order), any(List.class), Mockito.anyString()))
        .thenReturn(request);
    when(miraklOperatorApi.getShippingRates(request)).thenReturn(shippingFees);

    when(shippingZoneStrategy.getShippingZoneCode(order)).thenReturn(SHIPPING_ZONE_CODE);
  }

  @Test
  public void getShippingFees() {
    when(firstOrderEntry.getLineShippingCode()).thenReturn(ORIGINAL_SHIPPING_CODE_FOR_OFFER_1);

    MiraklOrderShippingFees result = testObj.getShippingFees(order);

    verify(miraklOperatorApi).getShippingRates(request);
    assertThat(result).isEqualTo(shippingFees);
  }

  @Test
  public void getShippingFeesReturnsNullIfFNoOffersFoundInCart() {
    when(order.getMarketplaceEntries()).thenReturn(Collections.emptyList());

    MiraklOrderShippingFees shippingFees = testObj.getShippingFees(order);

    assertThat(shippingFees).isNull();
  }

  @Test
  public void getsShippingFeeOffer() {
    Optional<MiraklOrderShippingFeeOffer> result = testObj.extractShippingFeeOffer(OFFER_ID, shippingFees);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isSameAs(offer);
  }

  @Test
  public void getShippingFeeOfferReturnsAbsentOptionalIfNoOfferFound() {
    when(shippingFee.getOffers()).thenReturn(Collections.<MiraklOrderShippingFeeOffer>emptyList());

    Optional<MiraklOrderShippingFeeOffer> result = testObj.extractShippingFeeOffer(OFFER_ID, shippingFees);

    assertThat(result.isPresent()).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeOfferThrowsIllegalArgumentExceptionIfOfferIdIsNull() {
    testObj.extractShippingFeeOffer(null, shippingFees);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeOfferThrowsIllegalArgumentExceptionIfShippingRatesIsNull() {
    testObj.extractShippingFeeOffer(OFFER_ID, null);
  }

  @Test
  public void getsShippingFeeError() {
    Optional<MiraklOrderShippingFeeError> result = testObj.extractShippingFeeError(OFFER_ID, shippingFees);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isSameAs(error);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeErrorThrowsIllegalArgumentExceptionIfOfferIdIsNull() {
    testObj.extractShippingFeeError(null, shippingFees);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeErrorThrowsIllegalArgumentExceptionIfShippingRatesIsNull() {
    testObj.extractShippingFeeError(OFFER_ID, null);
  }

  @Test
  public void getsStoredShippingFees() {
    when(order.getShippingFeesJSON()).thenReturn(SHIPPING_FEES_JSON);

    MiraklOrderShippingFees result = testObj.getStoredShippingFees(order);

    assertThat(result).isSameAs(shippingFees);
  }

  @Test
  public void getShippingFeesJSONReturnsNullIfShippingFeesJSONIsNull() {
    when(order.getShippingFeesJSON()).thenReturn(null);

    MiraklOrderShippingFees result = testObj.getStoredShippingFees(order);

    assertThat(result).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeesJSONThrowsIllegalArgumentExceptionIfOrderNull() {
    testObj.getStoredShippingFees(null);
  }

  @Test
  public void getsShippingFeesAsJson() {
    String result = testObj.getShippingFeesAsJson(shippingFees);

    assertThat(result).isSameAs(SHIPPING_FEES_JSON);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeesThrowsIllegalArgumentExceptionIfShippingFeesNull() {
    testObj.getShippingFeesAsJson(null);
  }

  @Test
  public void getsShippingFeeForShop() {
    when(shippingFee.getShopId()).thenReturn(SHOP_ID);
    when(shippingFee.getLeadtimeToShip()).thenReturn(LEAD_TIME_TO_SHIP);

    Optional<MiraklOrderShippingFee> result = testObj.extractShippingFeeForShop(shippingFees, SHOP_ID, LEAD_TIME_TO_SHIP);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isSameAs(shippingFee);
  }

  @Test
  public void getShippingFeeForShopReturnsAbsentOptionalIfNoShopWasFound() {
    Optional<MiraklOrderShippingFee> result = testObj.extractShippingFeeForShop(shippingFees, SHOP_ID, LEAD_TIME_TO_SHIP);

    assertThat(result.isPresent()).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeForShopThrowsIllegalArgumentExceptionIfShippingFeesIsNull() {
    testObj.extractShippingFeeForShop(null, SHOP_ID, LEAD_TIME_TO_SHIP);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeForShopThrowsIllegalArgumentExceptionIfShopIdIsNull() {
    testObj.extractShippingFeeForShop(shippingFees, null, LEAD_TIME_TO_SHIP);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShippingFeeForShopThrowsIllegalArgumentExceptionIfLeadTimeToShipNull() {
    testObj.extractShippingFeeForShop(shippingFees, SHOP_ID, null);
  }

  @Test
  public void getsAllShippingFeeOffers() {
    when(shippingFees.getOrders()).thenReturn(asList(firstShippingFee, secondShippingFee));
    when(firstShippingFee.getOffers()).thenReturn(singletonList(firstOffer));
    when(secondShippingFee.getOffers()).thenReturn(singletonList(secondOffer));

    List<MiraklOrderShippingFeeOffer> result = testObj.extractAllShippingFeeOffers(shippingFees);

    assertThat(result).containsOnly(firstOffer, secondOffer);
  }

  @Test
  public void getAllShippingFeeOffersReturnsEmptyListIfNoOffersFound() {
    when(shippingFees.getOrders()).thenReturn(asList(firstShippingFee, secondShippingFee));

    List<MiraklOrderShippingFeeOffer> result = testObj.extractAllShippingFeeOffers(shippingFees);

    assertThat(result).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getAllShippingFeeOffersThrowsIllegalArgumentExceptionIfShippingFeesIsNull() {
    testObj.extractAllShippingFeeOffers(null);
  }

  @Test
  public void updatesSelectedShippingOption() {
    when(secondShippingType.getCode()).thenReturn(SELECTED_SHIPPING_OPTION_CODE);

    testObj.updateSelectedShippingOption(shippingFee, SELECTED_SHIPPING_OPTION_CODE);

    verify(shippingFee).setSelectedShippingType(secondShippingType);
  }

  @Test
  public void updateSelectedShippingOptionDoesNotChangeSelectedShippingTypeIfNoShippingTypeFound() {
    testObj.updateSelectedShippingOption(shippingFee, SELECTED_SHIPPING_OPTION_CODE);

    verify(shippingFee, never()).setSelectedShippingType(any(MiraklShippingFeeType.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void updateSelectedShippingOptionThrowsIllegalArgumentExceptionIfShippingFeeIsNull() {
    testObj.updateSelectedShippingOption(null, SELECTED_SHIPPING_OPTION_CODE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void updateSelectedShippingOptionThrowsIllegalArgumentExceptionIfSelectedShippingOptionIsNull() {
    testObj.updateSelectedShippingOption(shippingFee, null);
  }

  @Test
  public void setsLineShippingDetails() {
    when(firstOrderEntry.getOfferId()).thenReturn(OFFER_ID);

    testObj.setLineShippingDetails(order, shippingFees);

    verify(orderEntryShippingConverter).convert(Pair.of(shippingFee, offer), firstOrderEntry);
  }

  @Test
  public void setLineShippingPricesDoesNotSetShippingLinesIfNoOfferFound() {
    when(shippingFees.getOrders()).thenReturn(asList(firstShippingFee, secondShippingFee));
    when(firstShippingFee.getOffers()).thenReturn(Collections.<MiraklOrderShippingFeeOffer>emptyList());
    when(secondShippingFee.getOffers()).thenReturn(Collections.<MiraklOrderShippingFeeOffer>emptyList());

    testObj.setLineShippingDetails(order, shippingFees);

    verifyZeroInteractions(orderEntryShippingConverter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setLineShippingPricesThrowsIllegalArgumentExceptionIfOrderIsNull() {
    testObj.setLineShippingDetails(null, shippingFees);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setLineShippingPricesThrowsIllegalArgumentExceptionIfSelectedShippingFeesIsNull() {
    MiraklOrderShippingFees orderShippingFees = null;
    testObj.setLineShippingDetails(order, orderShippingFees);
  }

  @Test
  public void extractsOrderShippingFeeForOffer() {
    when(shippingFees.getOrders()).thenReturn(asList(firstShippingFee, secondShippingFee));
    when(firstShippingFee.getOffers()).thenReturn(asList(firstOffer, secondOffer));
    when(secondShippingFee.getOffers()).thenReturn(asList(offer));
    when(offer.getId()).thenReturn(OFFER_ID);
    when(firstOffer.getId()).thenReturn(OFFER_1);
    when(secondOffer.getId()).thenReturn(OFFER_2);

    Optional<MiraklOrderShippingFee> shippingFeeForOffer = testObj.extractOrderShippingFeeForOffer(OFFER_ID, shippingFees);

    assertThat(shippingFeeForOffer.isPresent()).isEqualTo(true);
    assertThat(shippingFeeForOffer.get()).isEqualTo(secondShippingFee);
  }
}
