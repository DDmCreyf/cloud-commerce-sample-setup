package com.mirakl.hybris.core.catalog.daos;

import java.util.List;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MiraklCoreAttributeDao extends GenericDao<MiraklCoreAttributeModel> {

  /**
   * Returns the Mirakl Core attributes with unique identifier flag set to true
   *
   * @return a list of Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> findUniqueIdentifierCoreAttributes();

  /**
   * Returns the Mirakl Core Attributes tagged as variant attributes
   *
   * @return a list of Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> findVariantCoreAttributes();

  /**
   * Returns the Mirakl core attributes matching a given role
   * 
   * @param attributeRole the role of the attribute
   * @return a list of Mirakl Core Attributes having the given role
   */
  List<MiraklCoreAttributeModel> findCoreAttributeByRole(MiraklAttributeRole attributeRole);
}
