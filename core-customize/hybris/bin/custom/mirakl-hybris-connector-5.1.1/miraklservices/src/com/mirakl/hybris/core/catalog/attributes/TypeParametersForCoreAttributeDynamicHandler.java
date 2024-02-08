package com.mirakl.hybris.core.catalog.attributes;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_VALUE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class TypeParametersForCoreAttributeDynamicHandler
    extends AbstractDynamicAttributeHandler<String, MiraklCoreAttributeModel> {

  protected ConfigurationService configurationService;
  protected ValueListNamingStrategy valueListNamingStrategy;

  @Override
  public String get(MiraklCoreAttributeModel model) {
    if (isNotBlank(model.getTypeParametersInternal())) {
      return model.getTypeParametersInternal();
    }

    // For backward compatibility reasons (fallback to the legacy "type-parameter" if defined)
    if (isNotBlank(model.getTypeParameter()) && model.getType() != null) {
      String pattern = getTypeParametersConversionPattern(model.getType());
      return pattern != null ? format(pattern, model.getTypeParameter()) : null;
    }
    
    return getDefaultTypeParametersValue(model);
  }

  protected String getDefaultTypeParametersValue(MiraklCoreAttributeModel model) {
    if (model.getType() != null) {
      switch (model.getType()) {
        case BOOLEAN:
          return BOOLEAN_TYPE_PARAMETERS;
        case DATE:
          return configurationService.getConfiguration().getString(CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE);
        case DECIMAL:
          return configurationService.getConfiguration().getString(CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE);
        case MEDIA:
          return configurationService.getConfiguration().getString(CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_VALUE);
        case LIST:
        case LIST_MULTIPLE_VALUES:
          return format(getTypeParametersConversionPattern(model.getType()), valueListNamingStrategy.getCode(model));
        default:
          return null;
      }
    }
    return null;
  }

  protected String getTypeParametersConversionPattern(MiraklAttributeType type) {
    switch (type) {
      case DATE:
        return CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN;
      case DECIMAL:
        return CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN;
      case MEDIA:
        return CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN;
      case LIST:
      case LIST_MULTIPLE_VALUES:
        return CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN;
      default:
        return null;
    }
  }

  @Override
  public void set(MiraklCoreAttributeModel model, String value) {
    model.setTypeParametersInternal(value);
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
