package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShippingZoneStrategyTest {

  private static final String SHIPPING_ZONE_CODE = "FR";
  private static final String SHIPPING_ZONE_CODE_BASE_STORE = "US";

  @InjectMocks
  private DefaultShippingZoneStrategy testObj;
  @Mock
  private CartModel cartModel;
  @Mock
  private AbstractOrderModel order;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private AddressModel deliveryAddress;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private BaseStoreModel baseStore;
  @Mock
  private CountryModel country, baseStoreCountry;

  @Before
  public void setUp() {
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(cartModel.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(deliveryAddress.getCountry()).thenReturn(country);
    when(country.getIsocode()).thenReturn(SHIPPING_ZONE_CODE);
    when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
    when(baseStore.getDefaultDeliveryCountry()).thenReturn(baseStoreCountry);
    when(baseStoreCountry.getIsocode()).thenReturn(SHIPPING_ZONE_CODE_BASE_STORE);
  }

  @Test
  public void getsShippingZoneCodeFromCountryIsoCode() {
    String result = testObj.getShippingZoneCode(order);

    assertThat(result).isEqualTo(SHIPPING_ZONE_CODE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfOrderIsNull() {
    testObj.getShippingZoneCode(null);
  }

  @Test(expected = IllegalStateException.class)
  public void throwsIllegalStateExceptionIfDeliveryAddressIsNull() {
    when(order.getDeliveryAddress()).thenReturn(null);

    testObj.getShippingZoneCode(order);
  }

  @Test
  public void getEstimatedShippingZoneCode() {
    String result = testObj.getEstimatedShippingZoneCode(cartModel);

    assertThat(result).isEqualTo(SHIPPING_ZONE_CODE);
  }

  @Test
  public void getEstimatedShippingZoneCodeWithBaseStoreFallback() {
    when(cartModel.getDeliveryAddress()).thenReturn(null);

    String result = testObj.getEstimatedShippingZoneCode(cartModel);

    assertThat(result).isEqualTo(SHIPPING_ZONE_CODE_BASE_STORE);
  }
}
