package com.mirakl.hybris.core.customfields.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkNotNull;

import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.enums.MiraklCustomFieldPermission;
import com.mirakl.hybris.core.enums.MiraklCustomFieldType;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCustomFieldPopulator implements Populator<MiraklFrontOperatorAdditionalField, MiraklCustomFieldModel> {
  @Override
  public void populate(MiraklFrontOperatorAdditionalField apiCustomField, MiraklCustomFieldModel customField)
      throws ConversionException {
    checkNotNull(apiCustomField.getCode());
    checkNotNull(apiCustomField.getEntity());
    checkNotNull(apiCustomField.getType());

    if (customField.getCode() == null) {
      customField.setCode(apiCustomField.getCode());
    }
    if (customField.getEntity() == null) {
      customField.setEntity(MiraklCustomFieldLinkedEntity.valueOf(apiCustomField.getEntity().name()));
    }
    customField.setShopPermission(MiraklCustomFieldPermission.valueOf(apiCustomField.getShopPermission().name()));
    customField.setLabel(apiCustomField.getLabel());
    customField.setType(MiraklCustomFieldType.valueOf(apiCustomField.getType().name()));
    customField.setRequired(apiCustomField.isRequired());
    customField.setAcceptedValues(apiCustomField.getAcceptedValues());
    customField.setRegex(apiCustomField.getRegex());
  }
}
