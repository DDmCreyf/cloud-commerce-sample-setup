package com.mirakl.hybris.core.catalog.daos.impl;

import java.util.Collections;
import java.util.List;

import com.mirakl.hybris.core.catalog.daos.MiraklCoreAttributeDao;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultMiraklCoreAttributeDao extends DefaultGenericDao<MiraklCoreAttributeModel> implements MiraklCoreAttributeDao {

  public DefaultMiraklCoreAttributeDao() {
    super(MiraklCoreAttributeModel._TYPECODE);
  }

  @Override
  public List<MiraklCoreAttributeModel> findUniqueIdentifierCoreAttributes() {
    return find(Collections.singletonMap(MiraklCoreAttributeModel.UNIQUEIDENTIFIER, true));
  }

  @Override
  public List<MiraklCoreAttributeModel> findVariantCoreAttributes() {
    return find(Collections.singletonMap(MiraklCoreAttributeModel.VARIANT, true));
  }

  @Override
  public List<MiraklCoreAttributeModel> findCoreAttributeByRole(MiraklAttributeRole attributeRole) {
    return find(Collections.singletonMap(MiraklCoreAttributeModel.ROLE, attributeRole));
  }
}
