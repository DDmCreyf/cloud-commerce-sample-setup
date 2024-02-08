package com.mirakl.hybris.b2badvancedpricing.user.populators;

import static java.util.UUID.nameUUIDFromBytes;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.mirakl.hybris.beans.OrganizationData;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OrganizationDataPopulator implements Populator<B2BUnitModel, OrganizationData> {

  @Override
  public void populate(B2BUnitModel b2bUnit, OrganizationData organizationData) throws ConversionException {
    organizationData.setId(b2bUnit.getUid());
    organizationData.setName(b2bUnit.getName());
    // Determinist way of generating the identification number (It is missing from Hybris' B2B sample data)
    organizationData.setIdentificationNumber(nameUUIDFromBytes(b2bUnit.getUid().getBytes()).toString());
    if (b2bUnit.getBillingAddress() != null) {
      populateAddress(b2bUnit.getBillingAddress(), organizationData);
    } else if (isNotEmpty(b2bUnit.getAddresses())) {
      populateAddress(b2bUnit.getAddresses().iterator().next(), organizationData);
    }
  }

  private void populateAddress(AddressModel address, OrganizationData organizationData) {
    organizationData.setCity(address.getTown());
    if (address.getCountry() != null) {
      organizationData.setCountryIsoCode(address.getCountry().getIsoAlpha3());
    }
    if (address.getRegion() != null) {
      organizationData.setState(address.getRegion().getName());
    }
    StringBuilder street1 = new StringBuilder();
    if (isNotBlank(address.getStreetnumber())) {
      street1.append(address.getStreetnumber()).append(' ');
    }
    street1.append(address.getStreetname());
    organizationData.setStreet1(street1.toString());
    organizationData.setZipCode(address.getPostalcode());
  }

}
