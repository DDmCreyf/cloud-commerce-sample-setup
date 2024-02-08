package com.mirakl.hybris.core.environment.interceptors;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.environment.services.MiraklEnvironmentService;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;

public class MiraklEnvironmentInterceptor implements PrepareInterceptor<MiraklEnvironmentModel> {

  protected MiraklEnvironmentService miraklEnvironmentService;

  @Override
  public void onPrepare(MiraklEnvironmentModel miraklEnvironmentModel, InterceptorContext context) throws InterceptorException {
    if (miraklEnvironmentModel.isDefault() && (context.isNew(miraklEnvironmentModel)
        || (context.isModified(miraklEnvironmentModel)
            && context.getDirtyAttributes(miraklEnvironmentModel).containsKey(MiraklEnvironmentModel.DEFAULT)))){
      resetDefaultEnvironment(context);
    }
  }

  protected void resetDefaultEnvironment(InterceptorContext context) {
    final MiraklEnvironmentModel currentDefault = miraklEnvironmentService.getDefault();
    if (currentDefault != null) {
      currentDefault.setDefault(false);
      final ModelService modelService = context.getModelService();
      modelService.save(currentDefault);
    }
  }

  @Required
  public void setMiraklEnvironmentService(MiraklEnvironmentService miraklEnvironmentService) {
    this.miraklEnvironmentService = miraklEnvironmentService;
  }

}
