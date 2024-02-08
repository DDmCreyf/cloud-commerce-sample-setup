package com.mirakl.hybris.core.customfields.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.core.customfields.daos.MiraklCustomFieldDao;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.model.MiraklCustomFieldModel;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultMiraklCustomFieldDao extends DefaultGenericDao<MiraklCustomFieldModel> implements MiraklCustomFieldDao {

  public DefaultMiraklCustomFieldDao() {
    super(MiraklCustomFieldModel._TYPECODE);
  }

  @Override
  public MiraklCustomFieldModel findCustomFieldByCodeAndEntity(String code, MiraklCustomFieldLinkedEntity linkedEntity) {
    validateParameterNotNullStandardMessage("code", code);
    validateParameterNotNullStandardMessage("linkedEntity", linkedEntity);
    Map<String, Object> params = new HashMap<>();
    params.put(MiraklCustomFieldModel.CODE, code);
    params.put(MiraklCustomFieldModel.ENTITY, linkedEntity);
    List<MiraklCustomFieldModel> customFieldModels = find(params);

    if (isNotEmpty(customFieldModels) && customFieldModels.size() > 1) {
      throw new AmbiguousIdentifierException(format("Multiple custom fields found for id [%s]", code));
    }
    return isEmpty(customFieldModels) ? null : customFieldModels.get(0);
  }

}
