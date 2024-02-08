package com.mirakl.hybris.mtc.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.front.request.shipping.MiraklCustomerShippingToAddress;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCustomerShippingToAddressPopulatorTest {

  private static final String STREET_1 = "6146 Honey Bluff Parkway";
  private static final String STREET_2 = "Street 2";
  private static final String POSTAL_CODE = "49628-7978";
  private static final String CITY = "Calder";
  private static final String STATE = "MI";
  private static final String COUNTRY_ISO_CODE = "USA";
  private static final String NON_EXISTING_COUNTRY_ISO_CODE = "FFF";

  @InjectMocks
  private MiraklCustomerShippingToAddressPopulator testObj;
  @Mock
  private AddressModel address;
  @Mock
  private CountryModel country;
  @Mock
  private RegionModel region;

  @Before
  public void setUp() {
    when(address.getLine1()).thenReturn(STREET_1);
    when(address.getLine2()).thenReturn(STREET_2);
    when(address.getPostalcode()).thenReturn(POSTAL_CODE);
    when(address.getTown()).thenReturn(CITY);
    when(address.getRegion()).thenReturn(region);
    when(region.getIsocodeShort()).thenReturn(STATE);
    when(address.getCountry()).thenReturn(country);
    when(country.getIsoAlpha3()).thenReturn(COUNTRY_ISO_CODE);
  }

  @Test
  public void shouldPopulateMiraklCustomerShippingToAddress() {
    MiraklCustomerShippingToAddress result = new MiraklCustomerShippingToAddress();

    testObj.populate(address, result);
    assertThat(result.getStreet1()).isEqualTo(STREET_1);
    assertThat(result.getStreet2()).isEqualTo(STREET_2);
    assertThat(result.getZipCode()).isEqualTo(POSTAL_CODE);
    assertThat(result.getCity()).isEqualTo(CITY);
    assertThat(result.getState()).isEqualTo(STATE);
    assertThat(result.getCountryCode()).isEqualTo(IsoCountryCode.USA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfNoRegionFound() {
    when(address.getRegion()).thenReturn(null);

    MiraklCustomerShippingToAddress result = new MiraklCustomerShippingToAddress();

    testObj.populate(address, result);

    assertThat(result.getState()).isNull();
  }

  @Test
  public void shouldNotPopulateCountryIsoCodeIfNoIsoAlpha3Found() {
    when(country.getIsoAlpha3()).thenReturn(null);

    MiraklCustomerShippingToAddress result = new MiraklCustomerShippingToAddress();

    testObj.populate(address, result);

    assertThat(result.getCountryCode()).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfIsoAlpha3IsIncorrect() {
    when(country.getIsoAlpha3()).thenReturn(NON_EXISTING_COUNTRY_ISO_CODE);

    testObj.populate(address, new MiraklCustomerShippingToAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfSourceAddressIsNull() {
    testObj.populate(null, new MiraklCustomerShippingToAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfTargetMiraklCustomerAddressIsNull() {
    testObj.populate(address, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfRegionIsNull() {
    when(address.getRegion()).thenReturn(null);

    testObj.populate(address, new MiraklCustomerShippingToAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfCountryIsNull() {
    when(address.getCountry()).thenReturn(null);

    testObj.populate(address, new MiraklCustomerShippingToAddress());
  }
}
