package com.mirakl.hybris.core.mtc.strategies.impl;

import static com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants.ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES;
import static com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants.ALLOWED_MIRAKL_TAX_CONNECTOR_SHIP_TO_COUNTRIES;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.hybris.mtc.strategies.impl.DefaultMiraklTaxConnectorActivationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorActivationStrategyTest {

  private static final MiraklIsoCurrencyCode MIRAKL_ISO_CURRENCY_CODE = ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES.iterator().next();
  private static final IsoCountryCode MIRAKL_ISO_COUNTRY_CODE = ALLOWED_MIRAKL_TAX_CONNECTOR_SHIP_TO_COUNTRIES.iterator().next();

  private static final String BAD_CURRENCY_ISO_CODE = "BAD-CURRENCY-ISO-CODE";
  private static final String BAD_COUNTRY_ISO_CODE = "BAD-COUNTRY-ISO-CODE";

  @InjectMocks
  private DefaultMiraklTaxConnectorActivationStrategy testObj;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private BaseStoreModel baseStore;
  @Mock
  private CurrencyModel currency;
  @Mock
  private OrderModel order;
  @Mock
  private AddressModel deliveryAddress;
  @Mock
  private CountryModel country;

  @Before
  public void setUp() {
    when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
    when(baseStore.isNet()).thenReturn(true);
    when(baseStore.isMiraklTaxConnectorEnabled()).thenReturn(true);
    when(currency.getIsocode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE.name());
    when(order.getCurrency()).thenReturn(currency);
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(deliveryAddress.getCountry()).thenReturn(country);
    when(country.getIsoAlpha3()).thenReturn(MIRAKL_ISO_COUNTRY_CODE.name());
  }

  @Test
  public void miraklTaxConnectorIsEnabledAtBaseStoreAndNet() {
    when(baseStore.isMiraklTaxConnectorEnabled()).thenReturn(true);
    assertThat(testObj.isMiraklTaxConnectorEnabled()).isTrue();
  }

  @Test
  public void miraklTaxConnectorIsEnabledAtBaseStoreAndGross() {
    when(baseStore.isNet()).thenReturn(false);
    assertThat(testObj.isMiraklTaxConnectorEnabled()).isFalse();
  }

  @Test
  public void miraklTaxConnectorIsDisabled() {
    when(baseStore.isMiraklTaxConnectorEnabled()).thenReturn(false);
    assertThat(testObj.isMiraklTaxConnectorEnabled()).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsDisabledDueToConfiguration() {
    when(baseStore.isMiraklTaxConnectorEnabled()).thenReturn(false);
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsDisabledDueToInvalidCurrency() {
    when(currency.getIsocode()).thenReturn(BAD_CURRENCY_ISO_CODE);
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsDisabledDueToUnauthorizedCurrency() {
    when(currency.getIsocode()).thenReturn(MiraklIsoCurrencyCode.AED.name());
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsDisabledDueToInvalidCountry() {
    when(country.getIsoAlpha3()).thenReturn(BAD_COUNTRY_ISO_CODE);
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsDisabledDueToUnauthorizedCountry() {
    when(country.getIsoAlpha3()).thenReturn(IsoCountryCode.FRA.name());
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isFalse();
  }

  @Test
  public void miraklTaxConnectorComputationIsEnabled() {
    assertThat(testObj.isMiraklTaxConnectorComputation(order)).isTrue();
  }

}
