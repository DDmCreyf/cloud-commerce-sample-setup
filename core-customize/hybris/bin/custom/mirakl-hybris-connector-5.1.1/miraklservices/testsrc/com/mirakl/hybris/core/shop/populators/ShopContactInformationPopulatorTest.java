package com.mirakl.hybris.core.shop.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.core.i18n.services.CountryService;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopContactInformationPopulatorTest {

  @InjectMocks
  private ShopContactInformationPopulator shopInformationPopulator = new ShopContactInformationPopulator();

  @Mock
  private MiraklContactInformation contactInformationMock;

  @Mock
  private CountryService countryServiceMock;

  @Mock
  private MiraklShop miraklShopMock;

  @Mock
  private ModelService modelService;

  private static final String COUNTRY = "USA";
  private static final String EMAIL = "test.test@test.com";
  private static final String FAX = "0475986420";
  private static final String FIRST_NAME = "Firstname";
  private static final String LAST_NAME = "Lastname";
  private static final String PHONE1 = "0461359820";
  private static final String PHONE2 = "7632418420";
  private static final String POSTAL_CODE = "15300";
  private static final String STATE = "State";
  private static final String STREET_NAME_1 = "Lilas Street";
  private static final String STREET_NAME_2 = "Lilas Street 2";
  private static final String TOWN = "Unknown";
  private static final String URL = "http://sample-url.com";

  @Before
  public void setUp() {
    when(miraklShopMock.getContactInformation()).thenReturn(contactInformationMock);
    when(contactInformationMock.getCity()).thenReturn(TOWN);
    when(contactInformationMock.getCountry()).thenReturn(COUNTRY);
    when(contactInformationMock.getEmail()).thenReturn(EMAIL);
    when(contactInformationMock.getFax()).thenReturn(FAX);
    when(contactInformationMock.getFirstname()).thenReturn(FIRST_NAME);
    when(contactInformationMock.getLastname()).thenReturn(LAST_NAME);
    when(contactInformationMock.getPhone()).thenReturn(PHONE1);
    when(contactInformationMock.getPhoneSecondary()).thenReturn(PHONE2);
    when(contactInformationMock.getState()).thenReturn(STATE);
    when(contactInformationMock.getStreet1()).thenReturn(STREET_NAME_1);
    when(contactInformationMock.getStreet2()).thenReturn(STREET_NAME_2);
    when(contactInformationMock.getWebSite()).thenReturn(URL);
    when(contactInformationMock.getZipCode()).thenReturn(POSTAL_CODE);
    when(modelService.create(AddressModel.class)).thenReturn(new AddressModel());
  }

  @Test
  public void populateWhenIsoCodeIsFound() {
    CountryModel country = Mockito.mock(CountryModel.class);
    when(country.getIsoAlpha3()).thenReturn(COUNTRY);
    when(countryServiceMock.getCountryForIsoAlpha3Code(COUNTRY)).thenReturn(country);

    ShopModel shopModel = new ShopModel();
    shopInformationPopulator.populate(miraklShopMock, shopModel);

    assertThat(shopModel.getContactInformation()).isNotNull();
    assertThat(shopModel.getContactInformation().getCountry().getIsoAlpha3()).isEqualTo(COUNTRY);
    assertThat(shopModel.getContactInformation().getEmail()).isEqualTo(EMAIL);
    assertThat(shopModel.getContactInformation().getFax()).isEqualTo(FAX);
    assertThat(shopModel.getContactInformation().getFirstname()).isEqualTo(FIRST_NAME);
    assertThat(shopModel.getContactInformation().getLastname()).isEqualTo(LAST_NAME);
    assertThat(shopModel.getContactInformation().getPhone1()).isEqualTo(PHONE1);
    assertThat(shopModel.getContactInformation().getPhone2()).isEqualTo(PHONE2);
    assertThat(shopModel.getContactInformation().getPostalcode()).isEqualTo(POSTAL_CODE);
    assertThat(shopModel.getContactInformation().getTown()).isEqualTo(TOWN);
    assertThat(shopModel.getContactInformation().getUrl()).isEqualTo(URL);

  }

  @Test(expected = ConversionException.class)
  public void populateWhenIsoCodeIsNotFound() {
    when(countryServiceMock.getCountryForIsoAlpha3Code(anyString())).thenReturn(null);

    ShopModel shopModel = new ShopModel();
    shopInformationPopulator.populate(miraklShopMock, shopModel);
  }

}
