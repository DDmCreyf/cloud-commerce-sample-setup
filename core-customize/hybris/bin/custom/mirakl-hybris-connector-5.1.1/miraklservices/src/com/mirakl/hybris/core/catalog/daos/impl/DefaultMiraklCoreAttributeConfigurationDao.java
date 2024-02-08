package com.mirakl.hybris.core.catalog.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collections;
import java.util.List;

import com.mirakl.hybris.core.catalog.daos.MiraklCoreAttributeConfigurationDao;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMiraklCoreAttributeConfigurationDao extends DefaultGenericDao<MiraklCoreAttributeConfigurationModel>
    implements MiraklCoreAttributeConfigurationDao {

  public DefaultMiraklCoreAttributeConfigurationDao() {
    super(MiraklCoreAttributeConfigurationModel._TYPECODE);
  }

  @Override
  public MiraklCoreAttributeConfigurationModel getCoreAttributeConfigurationForCode(String code) {
    validateParameterNotNullStandardMessage("code", code);

    List<MiraklCoreAttributeConfigurationModel> configurations =
        find(Collections.singletonMap(MiraklCoreAttributeConfigurationModel.CODE, code));
    if (isNotEmpty(configurations) && configurations.size() > 1) {
      throw new AmbiguousIdentifierException(String.format("Found multiple core attribute configurations for id [%s]", code));
    }
    return isEmpty(configurations) ? null : configurations.get(0);
  }
}
