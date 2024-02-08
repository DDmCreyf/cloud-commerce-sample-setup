package com.mirakl.hybris.core.environment.services.impl;

import static java.util.Collections.singletonMap;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.environment.daos.MiraklEnvironmentDao;
import com.mirakl.hybris.core.environment.services.MiraklEnvironmentService;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

public class DefaultMiraklEnvironmentService implements MiraklEnvironmentService {

  protected MiraklEnvironmentDao miraklEnvironmentDao;

  @Override
  public MiraklEnvironmentModel getDefault() {
    final List<MiraklEnvironmentModel> miraklEnvironments =
        miraklEnvironmentDao.find(singletonMap(MiraklEnvironmentModel.DEFAULT, Boolean.TRUE));
    if (miraklEnvironments.size() > 1) {
      throw new IllegalStateException("Found more than one Mirakl Environment with default = true.");
    }
    if (!miraklEnvironments.isEmpty()) {
      return miraklEnvironments.get(0);
    }
    return null;
  }

  @Required
  public void setMiraklEnvironmentDao(MiraklEnvironmentDao miraklEnvironmentDao) {
    this.miraklEnvironmentDao = miraklEnvironmentDao;
  }

}
