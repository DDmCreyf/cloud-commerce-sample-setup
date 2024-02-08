package com.mirakl.hybris.mtc.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.domain.common.country.IsoCountryCode;
import com.mirakl.client.mmp.front.request.shipping.MiraklCustomerShippingToAddress;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCustomerShippingToAddressPopulator implements Populator<AddressModel, MiraklCustomerShippingToAddress> {

  @Override
  public void populate(AddressModel address, MiraklCustomerShippingToAddress miraklCustomerShipping) throws ConversionException {
    validateParameterNotNullStandardMessage("address", address);
    validateParameterNotNullStandardMessage("miraklCustomerShipping", miraklCustomerShipping);

    miraklCustomerShipping.setStreet1(address.getLine1());
    miraklCustomerShipping.setStreet2(address.getLine2());
    miraklCustomerShipping.setZipCode(address.getPostalcode());
    miraklCustomerShipping.setCity(address.getTown());
    setState(address, miraklCustomerShipping);
    setCountry(address, miraklCustomerShipping);
  }

  protected void setState(AddressModel address, MiraklCustomerShippingToAddress miraklCustomerShipping) {
    validateParameterNotNullStandardMessage("address.region", address.getRegion());

    miraklCustomerShipping.setState(address.getRegion().getIsocodeShort());
  }

  protected void setCountry(AddressModel address, MiraklCustomerShippingToAddress miraklCustomerShipping) {
    validateParameterNotNullStandardMessage("address.country", address.getCountry());

    String isoAlpha3 = address.getCountry().getIsoAlpha3();
    miraklCustomerShipping.setCountryCode(isoAlpha3 == null ? null : IsoCountryCode.valueOf(isoAlpha3));
  }
}
