package com.mirakl.hybris.core.customfields.services;

import java.util.Collection;
import java.util.Set;

import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

public interface CustomFieldImportService {

  /**
   * Imports all custom fields from Mirakl
   *
   * @return A collection of imported custom fields
   */
  Collection<MiraklCustomFieldModel> importAllCustomFields();

  /**
   * Imports all custom fields from Mirakl for the specified entities
   *
   * @param miraklCustomFieldLinkedEntities the set of related entities to import
   * @return A collection of imported custom fields for the specified entities
   */
  Collection<MiraklCustomFieldModel> importCustomFields(Set<MiraklCustomFieldLinkedEntity> miraklCustomFieldLinkedEntities);
}
