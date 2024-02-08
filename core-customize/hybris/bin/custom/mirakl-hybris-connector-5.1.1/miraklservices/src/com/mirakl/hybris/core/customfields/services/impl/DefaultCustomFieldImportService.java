package com.mirakl.hybris.core.customfields.services.impl;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.additionalfield.MiraklAdditionalFieldLinkedEntity;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.additionalfield.MiraklGetAdditionalFieldRequest;
import com.mirakl.hybris.core.customfields.daos.MiraklCustomFieldDao;
import com.mirakl.hybris.core.customfields.services.CustomFieldImportService;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;


public class DefaultCustomFieldImportService implements CustomFieldImportService {

  private static final Logger LOG = Logger.getLogger(DefaultCustomFieldImportService.class);

  protected ModelService modelService;
  protected MiraklMarketplacePlatformFrontApi mmpFrontApi;
  protected MiraklCustomFieldDao miraklCustomFieldDao;
  protected Converter<MiraklFrontOperatorAdditionalField, MiraklCustomFieldModel> miraklCustomFieldConverter;

  @Override
  public Collection<MiraklCustomFieldModel> importAllCustomFields() {
    return importCustomFields(Collections.emptySet());
  }

  @Override
  public Collection<MiraklCustomFieldModel> importCustomFields(Set<MiraklCustomFieldLinkedEntity> linkedEntities) {
    MiraklGetAdditionalFieldRequest request = getAdditionalFieldRequest(linkedEntities);
    return importApiCustomFields(mmpFrontApi.getAdditionalFields(request));
  }

  protected MiraklGetAdditionalFieldRequest getAdditionalFieldRequest(Set<MiraklCustomFieldLinkedEntity> linkedEntities) {
    if (isEmpty(linkedEntities)) {
      return new MiraklGetAdditionalFieldRequest();
    }

    Set<MiraklAdditionalFieldLinkedEntity> miraklLinkedEntity = EnumSet.noneOf(MiraklAdditionalFieldLinkedEntity.class);
    for (MiraklCustomFieldLinkedEntity customFieldLinkedEntity : linkedEntities) {
      miraklLinkedEntity.add(MiraklAdditionalFieldLinkedEntity.valueOf(customFieldLinkedEntity.getCode()));
    }
    return new MiraklGetAdditionalFieldRequest(miraklLinkedEntity.toArray(new MiraklAdditionalFieldLinkedEntity[0]));
  }

  protected List<MiraklCustomFieldModel> importApiCustomFields(List<MiraklFrontOperatorAdditionalField> apiCustomFields) {
    final List<MiraklCustomFieldModel> importedAdditionalFields = new ArrayList<>();
    for (MiraklFrontOperatorAdditionalField apiCustomField : apiCustomFields) {
      MiraklCustomFieldModel customField = getCustomField(apiCustomField);
      if (customField != null) {
        importedAdditionalFields.add(customField);
      }
    }
    modelService.saveAll(importedAdditionalFields);
    return importedAdditionalFields;
  }

  protected MiraklCustomFieldModel getCustomField(MiraklFrontOperatorAdditionalField apiCustomField) {
    try {
      MiraklCustomFieldModel customField = miraklCustomFieldDao.findCustomFieldByCodeAndEntity(apiCustomField.getCode(),
          MiraklCustomFieldLinkedEntity.valueOf(apiCustomField.getEntity().name()));
      if (customField == null) {
        customField = miraklCustomFieldConverter.convert(apiCustomField);
      } else {
        customField = miraklCustomFieldConverter.convert(apiCustomField, customField);
      }
      return customField;
    } catch (AmbiguousIdentifierException ex) {
      LOG.error(String.format("Custom field for code [%s] and entity [%s] has more than one instance.", apiCustomField.getCode(),
          apiCustomField.getEntity()), ex);
    }
    return null;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMmpFrontApi(MiraklMarketplacePlatformFrontApi mmpFrontApi) {
    this.mmpFrontApi = mmpFrontApi;
  }

  @Required
  public void setMiraklCustomFieldDao(MiraklCustomFieldDao miraklCustomFieldDao) {
    this.miraklCustomFieldDao = miraklCustomFieldDao;
  }

  @Required
  public void setMiraklCustomFieldConverter(
      Converter<MiraklFrontOperatorAdditionalField, MiraklCustomFieldModel> miraklCustomFieldConverter) {
    this.miraklCustomFieldConverter = miraklCustomFieldConverter;
  }

}
