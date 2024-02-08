package com.mirakl.hybris.core.customfields.services.impl;

import static com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity.OFFER;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferOfferAdditionalFields;
import com.mirakl.hybris.core.customfields.daos.MiraklCustomFieldDao;
import com.mirakl.hybris.core.customfields.services.CustomFieldService;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.enums.MiraklCustomFieldType;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

public class DefaultCustomFieldService implements CustomFieldService {
  private static final Logger LOG = Logger.getLogger(DefaultCustomFieldService.class);

  protected MiraklCustomFieldDao miraklCustomFieldDao;

  @Override
  public MiraklCustomFieldModel findCustomFieldByCodeAndEntity(String code, MiraklCustomFieldLinkedEntity linkedEntity) {
    return miraklCustomFieldDao.findCustomFieldByCodeAndEntity(code, linkedEntity);
  }

  @Override
  public List<MiraklAdditionalFieldValue> getCustomFieldValues(Map<String, String> customFields,
      MiraklCustomFieldLinkedEntity linkedEntity) {

    List<MiraklAdditionalFieldValue> mappedCustomFields = new ArrayList<>();
    for (Map.Entry<String, String> customFieldEntry : customFields.entrySet()) {
      MiraklCustomFieldModel customField = findCustomFieldByCodeAndEntity(customFieldEntry.getKey(), linkedEntity);
      if (customField == null) {
        handleMissingCustomField(linkedEntity, customFieldEntry.getKey());
        continue;
      }

      mappedCustomFields
          .add(buildMiraklAdditionalFieldValue(customFieldEntry.getKey(), customFieldEntry.getValue(), customField.getType()));
    }
    return mappedCustomFields;
  }

  @Override
  public List<MiraklAdditionalFieldValue> getCustomFieldValues(List<MiraklAsyncExportOfferOfferAdditionalFields> customFields) {
    List<MiraklAdditionalFieldValue> values = new ArrayList<>();
    for (MiraklAsyncExportOfferOfferAdditionalFields customField : customFields) {
      MiraklCustomFieldModel customFieldModel = findCustomFieldByCodeAndEntity(customField.getCode(), OFFER);
      if (customFieldModel == null) {
        handleMissingCustomField(OFFER, customField.getCode());
        continue;
      }
      values.add(buildMiraklAdditionalFieldValue(customField.getCode(), customField.getValue(), customFieldModel.getType()));
    }
    return values;
  }

  protected void handleMissingCustomField(MiraklCustomFieldLinkedEntity linkedEntity, String customFieldCode) {
    LOG.warn(format("Unknown [%s] custom field with code [%s], you may need to perform the import custom fields job",
        linkedEntity.getCode(), customFieldCode));
  }

  @Override
  public MiraklAdditionalFieldValue buildMiraklAdditionalFieldValue(String code, String value,
      MiraklCustomFieldType miraklCustomFieldType) {
    switch (miraklCustomFieldType) {
      case STRING:
        return new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(code, value);
      case DATE:
        return new MiraklAdditionalFieldValue.MiraklDateAdditionalFieldValue(code, value);
      case NUMERIC:
        return new MiraklAdditionalFieldValue.MiraklNumericAdditionalFieldValue(code, value);
      case BOOLEAN:
        return new MiraklAdditionalFieldValue.MiraklBooleanAdditionalFieldValue(code, value);
      case LINK:
        return new MiraklAdditionalFieldValue.MiraklLinkAdditionalFieldValue(code, value);
      case LIST:
        return new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(code, value);
      case REGEX:
        return new MiraklAdditionalFieldValue.MiraklRegexAdditionalFieldValue(code, value);
      case TEXTAREA:
        return new MiraklAdditionalFieldValue.MiraklTextAreaAdditionalFieldValue(code, value);
      case MULTIPLE_VALUES_LIST:
        if (value != null) {
          return new MiraklAdditionalFieldValue.MiraklMultipleValuesListAdditionalFieldValue(code,
              new ArrayList<>(asList(value.split(","))));
        } else {
          return new MiraklAdditionalFieldValue.MiraklMultipleValuesListAdditionalFieldValue(code, null);
        }
      default:
        return new MiraklAdditionalFieldValue.MiraklUnknownAdditionalFieldValue.MiraklUnknownAdditionalFieldValue();
    }
  }

  @Required
  public void setMiraklCustomFieldDao(MiraklCustomFieldDao miraklCustomFieldsDao) {
    this.miraklCustomFieldDao = miraklCustomFieldsDao;
  }

}
