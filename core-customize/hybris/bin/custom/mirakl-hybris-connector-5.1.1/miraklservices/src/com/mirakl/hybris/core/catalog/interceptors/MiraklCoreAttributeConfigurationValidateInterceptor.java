package com.mirakl.hybris.core.catalog.interceptors;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.CATEGORY_ATTRIBUTE;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.Set;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class MiraklCoreAttributeConfigurationValidateInterceptor
    implements ValidateInterceptor<MiraklCoreAttributeConfigurationModel> {

  @Override
  public void onValidate(final MiraklCoreAttributeConfigurationModel coreAttributeConfiguration,
      final InterceptorContext interceptorContext) throws InterceptorException {
    Set<MiraklCoreAttributeModel> coreAttributes = coreAttributeConfiguration.getCoreAttributes();
    if (!isEmpty(coreAttributes)) {
      verifyRole(coreAttributes, CATEGORY_ATTRIBUTE, "No category attribute was specified");
    }
  }

  protected void verifyRole(Set<MiraklCoreAttributeModel> coreAttributes, MiraklAttributeRole role, String errorMessage)
      throws InterceptorException {
    for (MiraklCoreAttributeModel coreAttribute : coreAttributes) {
      if (role.equals(coreAttribute.getRole())) {
        return;
      }
    }
    throw new InterceptorException(errorMessage);
  }
}
