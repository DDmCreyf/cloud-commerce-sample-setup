package com.mirakl.hybris.core.catalog.attributes;

import com.mirakl.hybris.core.enums.MiraklAttributeRequirementLevel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class RequirementLevelForCoreAttributeDynamicHandler
    extends AbstractDynamicAttributeHandler<MiraklAttributeRequirementLevel, MiraklCoreAttributeModel> {
  @Override
  public MiraklAttributeRequirementLevel get(MiraklCoreAttributeModel model) {
    if (model.getRequirementLevelInternal() != null) {
      return model.getRequirementLevelInternal();
    }
    return model.isRequired() ? MiraklAttributeRequirementLevel.REQUIRED : MiraklAttributeRequirementLevel.OPTIONAL;
  }

  @Override
  public void set(MiraklCoreAttributeModel model, MiraklAttributeRequirementLevel value) {
    model.setRequirementLevelInternal(value);
  }
}
