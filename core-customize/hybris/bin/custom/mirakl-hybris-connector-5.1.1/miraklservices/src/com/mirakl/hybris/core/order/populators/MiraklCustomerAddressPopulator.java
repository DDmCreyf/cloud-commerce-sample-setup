package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.domain.order.MiraklCustomerShippingAddress;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCustomerAddressPopulator implements Populator<AddressModel, MiraklCustomerShippingAddress> {

  @Override
  public void populate(AddressModel source, MiraklCustomerShippingAddress miraklCustomerAddress) throws ConversionException {
    validateParameterNotNullStandardMessage("address", source);
    validateParameterNotNullStandardMessage("miraklCustomerAddress", miraklCustomerAddress);

    miraklCustomerAddress.setLastname(source.getLastname());
    miraklCustomerAddress.setFirstname(source.getFirstname());
    miraklCustomerAddress.setAdditionalInfo(source.getRemarks());
    miraklCustomerAddress.setCity(source.getTown());
    miraklCustomerAddress.setCompany(source.getCompany());
    miraklCustomerAddress.setPhone(source.getPhone1());
    miraklCustomerAddress.setPhoneSecondary(source.getPhone2());
    miraklCustomerAddress.setStreet1(source.getLine1());
    miraklCustomerAddress.setStreet2(source.getLine2());
    miraklCustomerAddress.setZipCode(source.getPostalcode());

    setCountry(miraklCustomerAddress, source.getCountry());
    setState(miraklCustomerAddress, source);
  }

  protected void setState(MiraklCustomerShippingAddress miraklCustomerAddress, AddressModel address) {
    RegionModel region = address.getRegion();
    miraklCustomerAddress.setState(region == null ? null : region.getName());
  }

  protected void setCountry(MiraklCustomerShippingAddress miraklCustomerAddress, CountryModel country) {
    validateParameterNotNullStandardMessage("country", country);

    miraklCustomerAddress.setCountry(country.getName());

    String isoAlpha3 = country.getIsoAlpha3();
    miraklCustomerAddress.setCountryIsoCode(isoAlpha3 == null ? null : IsoCountryCode.valueOf(isoAlpha3));
  }
}
