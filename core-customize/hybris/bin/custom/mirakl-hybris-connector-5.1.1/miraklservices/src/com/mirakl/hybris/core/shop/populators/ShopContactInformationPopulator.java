package com.mirakl.hybris.core.shop.populators;

import com.mirakl.client.mmp.domain.shop.MiraklContactInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.hybris.core.i18n.services.CountryService;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import static java.lang.String.format;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ShopContactInformationPopulator implements Populator<MiraklShop, ShopModel> {

  protected CountryService countryService;

  private ModelService modelService;

  @Override
  public void populate(MiraklShop miraklShop, ShopModel shopModel) throws ConversionException {
    MiraklContactInformation contactInformation = miraklShop.getContactInformation();
    AddressModel addressModel = shopModel.getContactInformation();
    if (addressModel == null) {
      addressModel = modelService.create(AddressModel.class);
      shopModel.setContactInformation(addressModel);
      addressModel.setOwner(shopModel);
    }

    CountryModel shopCountry = countryService.getCountryForIsoAlpha3Code(contactInformation.getCountry());
    if (shopCountry != null) {
      addressModel.setCountry(shopCountry);
    } else {
      throw new ConversionException(format("Impossible to find the country with iso-code '%s'", contactInformation.getCountry()));
    }

    // This is where we could handle the civility also
    addressModel.setLine1(contactInformation.getStreet1());
    addressModel.setLine2(contactInformation.getStreet2());
    addressModel.setDepartment(contactInformation.getState());
    addressModel.setEmail(contactInformation.getEmail());
    addressModel.setFax(contactInformation.getFax());
    addressModel.setFirstname(contactInformation.getFirstname());
    addressModel.setLastname(contactInformation.getLastname());
    addressModel.setPhone1(contactInformation.getPhone());
    addressModel.setPhone2(contactInformation.getPhoneSecondary());
    addressModel.setPostalcode(contactInformation.getZipCode());
    addressModel.setTown(contactInformation.getCity());
    addressModel.setUrl(contactInformation.getWebSite());

  }

  @Required
  public void setCountryService(CountryService countryService) {
    this.countryService = countryService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
