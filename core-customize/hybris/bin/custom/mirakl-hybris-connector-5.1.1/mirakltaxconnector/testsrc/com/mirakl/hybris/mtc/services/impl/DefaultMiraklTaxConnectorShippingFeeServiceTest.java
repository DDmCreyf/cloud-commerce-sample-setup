package com.mirakl.hybris.mtc.services.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.client.mmp.front.domain.shipping.MiraklShippingFeeType;
import com.mirakl.client.mmp.front.request.shipping.MiraklCustomerShippingToAddress;
import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.client.mmp.front.request.shipping.MiraklOfferQuantityShippingTypeTuple;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestFactory;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorShippingFeeServiceTest {

  private static final String COUNTRY_ISO_CODE = "country-iso-code";
  private static final String OFFER_1 = "offer-1";
  private static final String OFFER_2 = "offer-2";
  private static final int FIRST_OFFER_QUANTITY = 2;
  private static final int SECOND_OFFER_QUANTITY = 1;
  private static final String SHIPPING_ZONE_CODE = "US";

  @Spy
  @InjectMocks
  private DefaultMiraklTaxConnectorShippingFeeService testObj;

  @Mock
  private JsonMarshallingService jsonMarshallingServiceMock;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklOperatorApi;
  @Mock
  private ShippingZoneStrategy shippingZoneStrategy;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private MiraklOrderShippingFee shippingFee;
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
  @Captor
  private ArgumentCaptor<MiraklGetShippingRatesRequest> shippingRatesRequestArgumentCaptor;
  @Mock
  private MiraklOrderShippingFeeOffer miraklOffer;
  @Mock
  private List<MiraklOfferQuantityShippingTypeTuple> offerTuples;
  @Mock
  private AddressModel deliveryAddress;
  @Mock
  private CountryModel country;
  @Mock
  private Converter<AddressModel, MiraklCustomerShippingToAddress> miraklCustomerShippingToAddressConverter;
  @Mock
  private MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  @Mock
  private MiraklGetShippingRatesRequestFactory shippingRatesRequestFactory;
  @Mock
  private MiraklGetShippingRatesRequest request;

  @Before
  public void setUp() {
    when(firstOrderEntry.getOfferId()).thenReturn(OFFER_1);
    when(secondOrderEntry.getOfferId()).thenReturn(OFFER_2);
    when(firstOrderEntry.getQuantity()).thenReturn(Long.valueOf(FIRST_OFFER_QUANTITY));
    when(secondOrderEntry.getQuantity()).thenReturn(Long.valueOf(SECOND_OFFER_QUANTITY));
    when(shippingZoneStrategy.getShippingZoneCode(order)).thenReturn(SHIPPING_ZONE_CODE);
    when(order.getMarketplaceEntries()).thenReturn(asList(firstOrderEntry, secondOrderEntry));
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(deliveryAddress.getCountry()).thenReturn(country);
    when(country.getIsocode()).thenReturn(COUNTRY_ISO_CODE);
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)).thenReturn(true);
    when(shippingRatesRequestFactory.createShippingRatesRequest(eq(order), anyListOf(MiraklOfferQuantityShippingTypeTuple.class), anyString())).thenReturn(request);
    when(miraklOperatorApi.getShippingRates(request)).thenReturn(shippingFees);
  }

  @Test
  public void getAddressModelFromOrderShouldReturnNullWhenInvalidAddressOrNoAddress() {
    when(deliveryAddress.getCountry()).thenReturn(null);
    AddressModel result = testObj.getAddressModelFromOrder(order);
    assertThat(result).isNull();

    when(order.getDeliveryAddress()).thenReturn(null);
    result = testObj.getAddressModelFromOrder(order);
    assertThat(result).isNull();
  }

  @Test
  public void getMiraklOrderShippingFees() {
    MiraklOrderShippingFees miraklOrderShippingFees = testObj.getMiraklOrderShippingFees(order, offerTuples, deliveryAddress);

    verify(miraklOperatorApi).getShippingRates(shippingRatesRequestArgumentCaptor.capture());
    assertThat(miraklOrderShippingFees).isEqualTo(shippingFees);
  }

  @Test
  public void getShippingFeesWithTaxesShouldNotUseMTCWhenNotPossible() {

    when(order.getDeliveryAddress()).thenReturn(null);

    testObj.getShippingFeesWithTaxes(order);

    verify(testObj).getShippingFees(order, SHIPPING_ZONE_CODE);

    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)).thenReturn(false);

    testObj.getShippingFeesWithTaxes(order);

    verify(testObj, times(2)).getShippingFees(order, SHIPPING_ZONE_CODE);
  }

  @Test
  public void getShippingFeesWithTaxesShouldUseMTCWhenPossible() {
    MiraklOrderShippingFees result = testObj.getShippingFeesWithTaxes(order);

    assertThat(result).isEqualTo(shippingFees);
  }

  @Test
  public void getShippingFeesWithTaxesShouldReturnNullWhenAddressIsInvalid() {
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)).thenReturn(true);
    doReturn(null).when(testObj).getAddressModelFromOrder(order);

    MiraklOrderShippingFees result = testObj.getShippingFeesWithTaxes(order);

    assertThat(result).isNull();
  }

  @Test
  public void getShippingFeesShouldNotUseMTCIfNotEnabled() {
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)).thenReturn(false);

    testObj.getShippingFees(order);
    testObj.getShippingFees(order, SHIPPING_ZONE_CODE);

    verify(testObj).getShippingFees(order);
    verify(testObj, times(2)).getShippingFees(order, SHIPPING_ZONE_CODE);
  }

  @Test
  public void getShippingFeesShouldNotUseMTCWithoutAddress() {

    when(order.getDeliveryAddress()).thenReturn(null);

    testObj.getShippingFees(order);
    testObj.getShippingFees(order, SHIPPING_ZONE_CODE);

    verify(testObj).getShippingFees(order);
    verify(testObj, times(2)).getShippingFees(order, SHIPPING_ZONE_CODE);
  }

  @Test
  public void getShippingFeesShouldUseMTCIfEnabledWithAValidAddress() {
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);

    testObj.getShippingFees(order);
    testObj.getShippingFees(order, SHIPPING_ZONE_CODE);

    verify(testObj, times(2)).getShippingFeesWithTaxes(order);
  }

  @Test(expected = MiraklApiException.class)
  public void getMiraklOrderShippingFeesShouldThrowMiraklApiExceptionWhenDeliveryAddressIsInvalid() {
    when(shippingRatesRequestFactory.createShippingRatesRequest(eq(order), anyListOf(MiraklOfferQuantityShippingTypeTuple.class), anyString()))
        .thenThrow(new IllegalArgumentException("Unable to convert address"));

    testObj.getMiraklOrderShippingFees(order, offerTuples, deliveryAddress);
  }

}
