package com.mirakl.hybris.core.shop.strategies.impl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.strategies.ShopValidationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.AddressModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultShopValidationStrategyTest {

  @InjectMocks
  ShopValidationStrategy validationStrategy = new DefaultShopValidationStrategy();

  @Mock
  ShopModel mockedShop;

  @Mock
  AddressModel mockedContactInformation;

  @Mock
  CountryModel mockedCountry;

  @Mock
  CurrencyModel mockedCurrency;

  @Before
  public void setUp() {
    when(mockedShop.getContactInformation()).thenReturn(mockedContactInformation);
    when(mockedContactInformation.getCountry()).thenReturn(mockedCountry);
    when(mockedShop.getCurrency()).thenReturn(mockedCurrency);
  }

  @Test
  public void ShopIsValidForImportWhenCurrencyAndCountryAreActive() {
    when(mockedCountry.getActive()).thenReturn(true);
    when(mockedCurrency.getActive()).thenReturn(true);

    assertTrue(validationStrategy.isValid(mockedShop));
  }

  @Test
  public void ShopIsNotValidForImportWhenCurrencyIsInactive() {
    when(mockedCurrency.getActive()).thenReturn(false);

    assertFalse(validationStrategy.isValid(mockedShop));
  }

  @Test
  public void ShopIsNotValidForImportWhenCountryIsInactive() {
    when(mockedCountry.getActive()).thenReturn(false);
    when(mockedCurrency.getActive()).thenReturn(true);

    assertFalse(validationStrategy.isValid(mockedShop));
  }
}
