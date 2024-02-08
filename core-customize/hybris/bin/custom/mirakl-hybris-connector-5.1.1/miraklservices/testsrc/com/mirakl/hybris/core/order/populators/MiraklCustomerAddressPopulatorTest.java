package com.mirakl.hybris.core.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.order.MiraklCustomerShippingAddress;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCustomerAddressPopulatorTest {

  private static final String LAST_NAME = "lastName";
  private static final String FIRST_NAME = "firstName";
  private static final String REMARKS = "remarks";
  private static final String CITY = "city";
  private static final String COMPANY = "company";
  private static final String COUNTRY = "France";
  private static final String COUNTRY_ISO_CODE = "FRA";
  private static final String PHONE_PRIMARY = "phone1";
  private static final String PHONE_SECONDARY = "phone2";
  private static final String STATE = "state";
  private static final String STREET_1 = "street1";
  private static final String STREET_2 = "street2";
  private static final String POSTAL_CODE = "postalCode";
  private static final String NON_EXISTING_COUNTRY_ISO_CODE = "nonExistingCountryIsoCode";

  private MiraklCustomerAddressPopulator testObj = new MiraklCustomerAddressPopulator();

  @Mock
  private AddressModel addressMock;
  @Mock
  private CountryModel countryMock;
  @Mock
  private RegionModel regionMock;

  @Before
  public void setUp() {
    when(addressMock.getFirstname()).thenReturn(FIRST_NAME);
    when(addressMock.getLastname()).thenReturn(LAST_NAME);
    when(addressMock.getRemarks()).thenReturn(REMARKS);
    when(addressMock.getTown()).thenReturn(CITY);
    when(addressMock.getCompany()).thenReturn(COMPANY);
    when(addressMock.getCountry()).thenReturn(countryMock);
    when(addressMock.getPhone1()).thenReturn(PHONE_PRIMARY);
    when(addressMock.getPhone2()).thenReturn(PHONE_SECONDARY);
    when(addressMock.getRegion()).thenReturn(regionMock);
    when(addressMock.getLine1()).thenReturn(STREET_1);
    when(addressMock.getLine2()).thenReturn(STREET_2);
    when(addressMock.getPostalcode()).thenReturn(POSTAL_CODE);

    when(countryMock.getName()).thenReturn(COUNTRY);
    when(countryMock.getIsoAlpha3()).thenReturn(COUNTRY_ISO_CODE);
    when(regionMock.getName()).thenReturn(STATE);
  }

  @Test
  public void shouldPopulateMiraklCustomerAddress() {
    MiraklCustomerShippingAddress result = new MiraklCustomerShippingAddress();

    testObj.populate(addressMock, result);

    assertThat(result.getLastname()).isEqualTo(LAST_NAME);
    assertThat(result.getFirstname()).isEqualTo(FIRST_NAME);
    assertThat(result.getAdditionalInfo()).isEqualTo(REMARKS);
    assertThat(result.getCity()).isEqualTo(CITY);
    assertThat(result.getCompany()).isEqualTo(COMPANY);
    assertThat(result.getCountry()).isEqualTo(COUNTRY);
    assertThat(result.getCountryIsoCode()).isEqualTo(IsoCountryCode.FRA);
    assertThat(result.getPhone()).isEqualTo(PHONE_PRIMARY);
    assertThat(result.getPhoneSecondary()).isEqualTo(PHONE_SECONDARY);
    assertThat(result.getState()).isEqualTo(STATE);
    assertThat(result.getStreet1()).isEqualTo(STREET_1);
    assertThat(result.getStreet2()).isEqualTo(STREET_2);
    assertThat(result.getZipCode()).isEqualTo(POSTAL_CODE);
  }

  @Test
  public void shouldNotPopulateStateIfNoRegionFound() {
    when(addressMock.getRegion()).thenReturn(null);

    MiraklCustomerShippingAddress result = new MiraklCustomerShippingAddress();

    testObj.populate(addressMock, result);

    assertThat(result.getState()).isNull();
  }

  @Test
  public void shouldNotPopulateCountryIsoCodeIfNoIsoAlpha3Found() {
    when(countryMock.getIsoAlpha3()).thenReturn(null);

    MiraklCustomerShippingAddress result = new MiraklCustomerShippingAddress();

    testObj.populate(addressMock, result);

    assertThat(result.getCountryIsoCode()).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfIsoAlpha3IsIncorrect() {
    when(countryMock.getIsoAlpha3()).thenReturn(NON_EXISTING_COUNTRY_ISO_CODE);

    testObj.populate(addressMock, new MiraklCustomerShippingAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfSourceAddressIsNull() {
    testObj.populate(null, new MiraklCustomerShippingAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfTargetMiraklCustomerAddressIsNull() {
    testObj.populate(addressMock, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfCountryIsNull() {
    when(addressMock.getCountry()).thenReturn(null);

    testObj.populate(addressMock, new MiraklCustomerShippingAddress());
  }
}
