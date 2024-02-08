package com.mirakl.hybris.core.customfields.daos;

import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

public interface MiraklCustomFieldDao extends GenericDao<MiraklCustomFieldModel> {

  /**
   * Returns the custom field with the specified code and entity.
   *
   * @param code the code of the custom field
   * @param linkedEntity the linked entity of the custom field
   * @return the custom field with the specified code and entity.
   * @throws AmbiguousIdentifierException of multiple custom field founds
   */
  MiraklCustomFieldModel findCustomFieldByCodeAndEntity(String code, MiraklCustomFieldLinkedEntity linkedEntity);

}
