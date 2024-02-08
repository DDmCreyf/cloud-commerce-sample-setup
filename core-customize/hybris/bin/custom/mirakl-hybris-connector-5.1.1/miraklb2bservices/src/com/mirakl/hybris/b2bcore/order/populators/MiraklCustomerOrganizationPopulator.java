package com.mirakl.hybris.b2bcore.order.populators;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.order.customerorganization.MiraklCustomerOrganization;
import com.mirakl.client.mmp.domain.order.customerorganization.MiraklCustomerOrganizationAddress;
import com.mirakl.hybris.beans.OrganizationData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCustomerOrganizationPopulator implements Populator<OrganizationData, MiraklCustomerOrganization> {

  @Override
  public void populate(OrganizationData organizationData, MiraklCustomerOrganization miraklCustomerOrganization)
      throws ConversionException {
    miraklCustomerOrganization.setId(organizationData.getId());
    miraklCustomerOrganization.setIdentificationNumber(organizationData.getIdentificationNumber());
    miraklCustomerOrganization.setName(organizationData.getName());
    miraklCustomerOrganization.setTaxIdentificationNumber(organizationData.getTaxIdentificationNumber());

    MiraklCustomerOrganizationAddress address = new MiraklCustomerOrganizationAddress();
    address.setCity(organizationData.getCity());
    if (isNotBlank(organizationData.getCountryIsoCode())) {
      address.setCountryIsoCode(IsoCountryCode.valueOf(organizationData.getCountryIsoCode()));
    }
    address.setState(organizationData.getState());
    address.setStreet1(organizationData.getStreet1());
    address.setStreet2(organizationData.getStreet2());
    address.setZipCode(organizationData.getZipCode());

    miraklCustomerOrganization.setAddress(address);
  }

}
