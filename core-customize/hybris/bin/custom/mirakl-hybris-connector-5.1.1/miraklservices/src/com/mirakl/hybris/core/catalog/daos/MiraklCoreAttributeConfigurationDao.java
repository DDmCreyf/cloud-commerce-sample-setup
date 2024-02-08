package com.mirakl.hybris.core.catalog.daos;

import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MiraklCoreAttributeConfigurationDao extends GenericDao<MiraklCoreAttributeConfigurationModel> {

  /**
   * Returns the Core Attribute Configuration with the wanted code.
   * 
   * @param code the code of the configuration
   * @return the core attribute configuration
   */
  MiraklCoreAttributeConfigurationModel getCoreAttributeConfigurationForCode(String code);

}
