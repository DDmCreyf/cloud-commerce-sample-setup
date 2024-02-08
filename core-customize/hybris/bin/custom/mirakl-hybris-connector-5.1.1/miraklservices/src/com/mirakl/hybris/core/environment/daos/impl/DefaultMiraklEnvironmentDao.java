package com.mirakl.hybris.core.environment.daos.impl;

import com.mirakl.hybris.core.environment.daos.MiraklEnvironmentDao;
import com.mirakl.hybris.core.model.MiraklEnvironmentModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklEnvironmentDao extends DefaultGenericDao<MiraklEnvironmentModel> implements MiraklEnvironmentDao {

  public DefaultMiraklEnvironmentDao() {
    super(MiraklEnvironmentModel._TYPECODE);
  }

}
