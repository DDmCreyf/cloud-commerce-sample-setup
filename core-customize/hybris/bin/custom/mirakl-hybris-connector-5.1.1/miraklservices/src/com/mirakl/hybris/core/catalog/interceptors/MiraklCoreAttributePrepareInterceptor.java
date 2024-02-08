package com.mirakl.hybris.core.catalog.interceptors;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

public class MiraklCoreAttributePrepareInterceptor implements PrepareInterceptor<MiraklCoreAttributeModel> {

  @Override
  public void onPrepare(MiraklCoreAttributeModel coreAttribute, InterceptorContext context) throws InterceptorException {
    if (isEmpty(coreAttribute.getUid())) {
      if (coreAttribute instanceof MiraklCategoryCoreAttributeModel) {
        MiraklCategoryCoreAttributeModel categoryCoreAttribute = (MiraklCategoryCoreAttributeModel) coreAttribute;
        coreAttribute.setUid(format("%s-%s", coreAttribute.getCode(), categoryCoreAttribute.getRootCategoryCode()));
      } else {
        coreAttribute.setUid(coreAttribute.getCode());
      }
    }
  }


}
