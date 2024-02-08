package com.mirakl.hybris.core.environment.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.environment.services.MiraklEnvironmentService;
import com.mirakl.hybris.core.environment.strategies.MiraklEnvironmentSelectionStrategy;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

public class DefaultMiraklEnvironmentSelectionStrategy implements MiraklEnvironmentSelectionStrategy {

  protected MiraklEnvironmentService miraklEnvironmentService;

  @Override
  public MiraklEnvironmentModel resolveCurrentMiraklEnvironment() {
    return miraklEnvironmentService.getDefault();
  }

  @Required
  public void setMiraklEnvironmentService(MiraklEnvironmentService miraklEnvironmentService) {
    this.miraklEnvironmentService = miraklEnvironmentService;
  }

}
