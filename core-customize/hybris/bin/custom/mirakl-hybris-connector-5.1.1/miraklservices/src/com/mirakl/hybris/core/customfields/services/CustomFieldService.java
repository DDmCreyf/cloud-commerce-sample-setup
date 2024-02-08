package com.mirakl.hybris.core.customfields.services;

import java.util.List;
import java.util.Map;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferOfferAdditionalFields;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.enums.MiraklCustomFieldType;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

public interface CustomFieldService {

  /**
   * Returns the custom field with the specified code and linked entity.
   *
   * @param code the code of the custom field
   * @param linkedEntity the linked entity of the custom field
   * @return the custom field with the specified code and linked entity.
   */
  MiraklCustomFieldModel findCustomFieldByCodeAndEntity(String code, MiraklCustomFieldLinkedEntity linkedEntity);

  /**
   * Returns the list of additional field value for the specified code, linked entity and type.
   *
   * @param offerCustomFields the code and value map of the custom field
   * @param linkedEntity the entity related to the custom field
   * @return the additional fields value
   */
  List<MiraklAdditionalFieldValue> getCustomFieldValues(Map<String, String> offerCustomFields,
      MiraklCustomFieldLinkedEntity linkedEntity);

  /**
   * Returns the list of additional field value for the specified code, linked entity and type.
   *
   * @param customFields the offer custom fields from OF54
   * @return the additional fields value
   */
  List<MiraklAdditionalFieldValue> getCustomFieldValues(List<MiraklAsyncExportOfferOfferAdditionalFields> customFields);

  /**
   * Returns the additional field value for the specified code, linked entity and type.
   *
   * @param code the code of the custom field
   * @param value the value of the custom field
   * @param miraklCustomFieldType the type of the custom field
   * @return the additional field value
   */
  MiraklAdditionalFieldValue buildMiraklAdditionalFieldValue(String code, String value,
      MiraklCustomFieldType miraklCustomFieldType);
}
