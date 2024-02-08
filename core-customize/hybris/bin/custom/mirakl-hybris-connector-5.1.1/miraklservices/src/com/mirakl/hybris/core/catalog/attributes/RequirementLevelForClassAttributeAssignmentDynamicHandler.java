package com.mirakl.hybris.core.catalog.attributes;

import com.mirakl.hybris.core.enums.MiraklAttributeRequirementLevel;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class RequirementLevelForClassAttributeAssignmentDynamicHandler
    extends AbstractDynamicAttributeHandler<MiraklAttributeRequirementLevel, ClassAttributeAssignmentModel> {

  @Override
  public MiraklAttributeRequirementLevel get(ClassAttributeAssignmentModel model) {
    if (model.getMarketplaceRequirementLevelInternal() != null) {
      return model.getMarketplaceRequirementLevelInternal();
    }
    return model.isRequiredForMarketplace() ? MiraklAttributeRequirementLevel.REQUIRED : MiraklAttributeRequirementLevel.OPTIONAL;
  }

  @Override
  public void set(ClassAttributeAssignmentModel model, MiraklAttributeRequirementLevel value) {
    model.setMarketplaceRequirementLevelInternal(value);
  }
}
