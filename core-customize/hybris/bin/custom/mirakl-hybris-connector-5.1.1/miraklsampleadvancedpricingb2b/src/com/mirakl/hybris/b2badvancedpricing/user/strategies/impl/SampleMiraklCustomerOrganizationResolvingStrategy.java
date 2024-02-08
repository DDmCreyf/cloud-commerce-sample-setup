package com.mirakl.hybris.b2badvancedpricing.user.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OrganizationData;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

public class SampleMiraklCustomerOrganizationResolvingStrategy implements MiraklCustomerOrganizationResolvingStrategy {

  protected UserService userService;
  protected B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
  protected Converter<B2BUnitModel, OrganizationData> organizationDataConverter;

  @Override
  public OrganizationData resolveUserOrganization(UserModel userModel) {
    B2BUnitModel b2bUnit = getUserParentB2BUnit(userModel);
    return b2bUnit != null ? organizationDataConverter.convert(b2bUnit) : null;
  }

  @Override
  public String resolveCurrentUserOrganizationId() {
    B2BUnitModel b2bUnit = getUserParentB2BUnit(userService.getCurrentUser());
    return b2bUnit != null ? b2bUnit.getUid() : null;
  }

  protected B2BUnitModel getUserParentB2BUnit(UserModel userModel) {
    if (userModel instanceof B2BCustomerModel) {
      return b2BUnitService.getParent((B2BCustomerModel) userModel);
    }
    return null;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setB2BUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService) {
    this.b2BUnitService = b2BUnitService;
  }

  @Required
  public void setOrganizationDataConverter(Converter<B2BUnitModel, OrganizationData> organizationDataConverter) {
    this.organizationDataConverter = organizationDataConverter;
  }
}
