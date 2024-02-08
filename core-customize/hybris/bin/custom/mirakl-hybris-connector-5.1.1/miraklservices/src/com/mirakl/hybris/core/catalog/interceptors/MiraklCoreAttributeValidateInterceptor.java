package com.mirakl.hybris.core.catalog.interceptors;

import static de.hybris.platform.core.Registry.getApplicationContext;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.type.TypeService;

public class MiraklCoreAttributeValidateInterceptor implements ValidateInterceptor<MiraklCoreAttributeModel> {

  private static final Logger LOG = Logger.getLogger(MiraklCoreAttributeValidateInterceptor.class);

  protected TypeService typeService;

  @Override
  public void onValidate(final MiraklCoreAttributeModel coreAttribute, final InterceptorContext context)
      throws InterceptorException {
    String beanId = coreAttribute.getImportExportHandlerStringId();
    if (isNotEmpty(beanId) && !beanDoesExist(beanId)) {
      throw new InterceptorException(format("Unable to find bean with name [%s]", beanId));
    }
  }

  protected Boolean beanDoesExist(String beanId) {
    try {
      return getApplicationContext().getBean(beanId, CoreAttributeHandler.class) != null;
    } catch (BeansException e) {
      LOG.error(format("Unable to find bean for id [%s]", beanId), e);
      return false;
    }
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

}
