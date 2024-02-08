package com.mirakl.hybris.addon.models;

import com.mirakl.hybris.addon.model.restrictions.CMSPurchasableProductRestrictionModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.util.localization.Localization;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class PurchasableProductRestrictionDescription
    extends AbstractDynamicAttributeHandler<String, CMSPurchasableProductRestrictionModel> {

  private static final String DESCRIPTION_PROPERTY = "type.cmspurchasableproductrestriction.description.text";
  private static final String DESCRIPTION_FALLBACK = "Purchasable product restriction";

  @Override
  public String get(CMSPurchasableProductRestrictionModel model) {
    String description = Localization.getLocalizedString(DESCRIPTION_PROPERTY);

    return isBlank(description) ? DESCRIPTION_FALLBACK : description;
  }
}
