package com.mirakl.hybris.b2bcore.order.populators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.customerorganization.MiraklCustomerOrganization;
import com.mirakl.hybris.beans.OrganizationData;

import de.hybris.bootstrap.annotations.UnitTest;

import static org.fest.assertions.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCustomerOrganizationPopulatorTest {

  private static final String IDENTIFICATION_NUMBER = "7898765456565678765";
  private static final String ORGANIZATION_ID = "ORG01EAST";
  private static final String ORGANIZATION_NAME = "East organization";
  private static final String ORGANIZATION_STREET = "10 white rabbits' road";
  private static final String ORGANIZATION_CITY = "Boston";
  private static final String ORGANIZATION_COUNTRY_ISOCODE = "USA";
  private static final String ORGANIZATION_ZIPCODE = "8987";

  @InjectMocks
  private MiraklCustomerOrganizationPopulator testObj;

  @Test
  public void testPopulate() {
    OrganizationData organizationData = new OrganizationData();
    organizationData.setIdentificationNumber(IDENTIFICATION_NUMBER);
    organizationData.setId(ORGANIZATION_ID);
    organizationData.setName(ORGANIZATION_NAME);
    organizationData.setStreet1(ORGANIZATION_STREET);
    organizationData.setCity(ORGANIZATION_CITY);
    organizationData.setCountryIsoCode(ORGANIZATION_COUNTRY_ISOCODE);
    organizationData.setZipCode(ORGANIZATION_ZIPCODE);
    MiraklCustomerOrganization miraklCustomerOrganization = new MiraklCustomerOrganization();

    testObj.populate(organizationData, miraklCustomerOrganization);

    assertThat(miraklCustomerOrganization.getIdentificationNumber()).isEqualTo(IDENTIFICATION_NUMBER);
    assertThat(miraklCustomerOrganization.getId()).isEqualTo(ORGANIZATION_ID);
    assertThat(miraklCustomerOrganization.getName()).isEqualTo(ORGANIZATION_NAME);
    assertThat(miraklCustomerOrganization.getAddress().getStreet1()).isEqualTo(ORGANIZATION_STREET);
    assertThat(miraklCustomerOrganization.getAddress().getCity()).isEqualTo(ORGANIZATION_CITY);
    assertThat(miraklCustomerOrganization.getAddress().getCountryIsoCode().getCode()).isEqualTo(ORGANIZATION_COUNTRY_ISOCODE);
    assertThat(miraklCustomerOrganization.getAddress().getZipCode()).isEqualTo(ORGANIZATION_ZIPCODE);
  }
}
