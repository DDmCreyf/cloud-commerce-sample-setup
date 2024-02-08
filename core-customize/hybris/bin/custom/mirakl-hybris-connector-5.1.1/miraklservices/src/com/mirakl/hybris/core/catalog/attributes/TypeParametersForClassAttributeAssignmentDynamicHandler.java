package com.mirakl.hybris.core.catalog.attributes;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class TypeParametersForClassAttributeAssignmentDynamicHandler extends AbstractDynamicAttributeHandler<String, ClassAttributeAssignmentModel> {

  protected ConfigurationService configurationService;
  protected ValueListNamingStrategy valueListNamingStrategy;

  @Override
  public String get(ClassAttributeAssignmentModel model) {
    if (isNotBlank(model.getMarketplaceTypeParametersInternal())) {
      return model.getMarketplaceTypeParametersInternal();
    }

    switch (model.getAttributeType()) {
      case BOOLEAN:
        return BOOLEAN_TYPE_PARAMETERS;
      case DATE:
        return configurationService.getConfiguration().getString(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE);
      case NUMBER:
        return configurationService.getConfiguration().getString(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE);
      case ENUM:
        return format(CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN, valueListNamingStrategy.getCode(model));
      default:
        return null;
    }
  }

  @Override
  public void set(ClassAttributeAssignmentModel model, String value) {
    model.setMarketplaceTypeParametersInternal(value);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setValueListNamingStrategy(ValueListNamingStrategy valueListNamingStrategy) {
    this.valueListNamingStrategy = valueListNamingStrategy;
  }

}
