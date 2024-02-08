package com.mirakl.hybris.core.catalog.attributes;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class CoreAttributeConfigurationRootCategoryDynamicHandler
    extends AbstractDynamicAttributeHandler<String, MiraklCoreAttributeConfigurationModel> {

  @Override
  public String get(MiraklCoreAttributeConfigurationModel coreAttributeConfiguration) {
    for (MiraklCoreAttributeModel coreAttribute : coreAttributeConfiguration.getCoreAttributes()) {
      if (MiraklAttributeRole.CATEGORY_ATTRIBUTE.equals(coreAttribute.getRole())
          && coreAttribute instanceof MiraklCategoryCoreAttributeModel) {
        return ((MiraklCategoryCoreAttributeModel) coreAttribute).getRootCategoryCode();
      }
    }
    return null;
  }

}
