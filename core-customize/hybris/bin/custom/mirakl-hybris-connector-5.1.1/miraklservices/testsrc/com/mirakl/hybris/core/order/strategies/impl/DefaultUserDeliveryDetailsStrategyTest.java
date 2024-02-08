package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUserDeliveryDetailsStrategyTest {

  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private BaseStoreModel baseStore;
  @Mock
  private CountryModel defaultDeliveryCountry, countryFromDeliveryAddress;
  @Mock
  private AddressModel defaultDeliveryAddress;

  @Spy
  @InjectMocks
  private DefaultUserDeliveryDetailsStrategy testObj = new DefaultUserDeliveryDetailsStrategy();

  @Before
  public void setUp() throws Exception {
    when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
    when(baseStore.getDefaultDeliveryCountry()).thenReturn(defaultDeliveryCountry);
  }

  @Test
  public void getDefaultDeliveryAddress() {
    AddressModel defaultDeliveryAddress = testObj.getDefaultDeliveryAddress();

    assertThat(defaultDeliveryAddress).isNull();
  }

  @Test
  public void getDefaultDeliveryCountryFromDeliveryAddress() {
    when(testObj.getDefaultDeliveryAddress()).thenReturn(defaultDeliveryAddress);
    when(defaultDeliveryAddress.getCountry()).thenReturn(countryFromDeliveryAddress);

    CountryModel defaultDeliveryCountry = testObj.getDefaultDeliveryCountry();

    assertThat(defaultDeliveryCountry).isEqualTo(countryFromDeliveryAddress);
  }

  @Test
  public void getDefaultDeliveryCountryFallbackToBaseStore() {
    CountryModel defaultDeliveryCountry = testObj.getDefaultDeliveryCountry();

    assertThat(defaultDeliveryCountry).isEqualTo(defaultDeliveryCountry);
  }
}
