package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.catalog.attributes.utils.TypeParametersUtils.extractDatePatternFromTypeParameters;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.CATALOG_EXPORT_DATE_FORMAT;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeValueTranslationStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.util.services.impl.TranslationException;

import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

public class DefaultCoreAttributeValueTranslationStrategy implements CoreAttributeValueTranslationStrategy {

  protected EnumerationService enumerationService;
  protected TypeService typeService;
  protected ConfigurationService configurationService;

  @Override
  public Object translateAttributeValue(AttributeValueData attributeValue, ProductModel product) throws TranslationException {
    validateParameterNotNullStandardMessage("attributeValue", attributeValue);
    validateParameterNotNullStandardMessage("attributeValue.coreAttribute", attributeValue.getCoreAttribute());
    validateParameterNotNullStandardMessage("product", product);

    MiraklCoreAttributeModel coreAttribute = attributeValue.getCoreAttribute();
    AttributeDescriptorModel attributeDescriptor =
        typeService.getAttributeDescriptor(product.getItemtype(), coreAttribute.getCode());

    if (attributeDescriptor.getAttributeType() instanceof EnumerationMetaTypeModel && isNotBlank(attributeValue.getValue())) {
      return enumerationService.getEnumerationValue(attributeDescriptor.getAttributeType().getCode(), attributeValue.getValue());
    }

    try {
      return getAtomicValue(attributeValue, attributeDescriptor.getPersistenceClass());

    } catch (UnsupportedOperationException e) {
      throw new TranslationException(String.format(
          "The core attribute [%s] of persistence type [%s] is not supported by the default handler. You may have to create your own handler.",
          coreAttribute.getCode(), attributeDescriptor.getPersistenceClass().getName()), e);
    }
  }


  protected Object getAtomicValue(AttributeValueData attributeValue, Class targetObjectType) throws TranslationException {
    String value = attributeValue.getValue();
    MiraklCoreAttributeModel coreAttribute = attributeValue.getCoreAttribute();

    String toParse = trimToNull(value);

    try {

      if (String.class.isAssignableFrom(targetObjectType)) {
        return parseString(attributeValue, coreAttribute, toParse);
      }

      if (toParse == null) {
        return null;
      }

      if (Boolean.class.isAssignableFrom(targetObjectType)) {
        return Boolean.valueOf(toParse);
      }

      if (Number.class.isAssignableFrom(targetObjectType)) {
        return parseNumber(targetObjectType, toParse);
      }

      if (Character.class.isAssignableFrom(targetObjectType)) {
        return toParse.charAt(0);
      }

      if (Date.class.isAssignableFrom(targetObjectType)) {
        return parseDate(toParse, getDateFormat(coreAttribute));
      }
    } catch (Exception e) {
      throw new TranslationException(
          String.format("Unable to parse value [%s] for atomic target type [%s].", value, targetObjectType.getName()), e);
    }
    throw new UnsupportedOperationException(
        String.format("Unable to parse value [%s] for atomic target type [%s]. The target type is not supported.", value,
            targetObjectType.getName()));
  }


  protected Number parseNumber(Class targetObjectType, String toParse) {
    if (Integer.class.isAssignableFrom(targetObjectType)) {
      return Integer.valueOf(toParse);
    }

    if (Double.class.isAssignableFrom(targetObjectType)) {
      return Double.valueOf(toParse);
    }

    if (Byte.class.isAssignableFrom(targetObjectType)) {
      return Byte.valueOf(toParse);
    }

    if (Float.class.isAssignableFrom(targetObjectType)) {
      return Float.valueOf(toParse);
    }

    if (Long.class.isAssignableFrom(targetObjectType)) {
      return Long.valueOf(toParse);
    }

    if (Short.class.isAssignableFrom(targetObjectType)) {
      return Short.valueOf(toParse);
    }

    if (BigDecimal.class.isAssignableFrom(targetObjectType)) {
      return new BigDecimal(toParse);
    }

    throw new UnsupportedOperationException(
        format("Unable to parse number [%s] for atomic target type [%s]. The target type is not supported.", toParse,
            targetObjectType.getName()));
  }


  protected Object parseString(AttributeValueData attributeValue, MiraklCoreAttributeModel coreAttribute, String toParse) {
    if (coreAttribute.isLocalized() && attributeValue.getLocale() != null) {
      return singletonMap(attributeValue.getLocale(), toParse);
    }
    return toParse;
  }


  protected Object parseDate(String toParse, DateFormat dateFormat) throws TranslationException {
    try {
      return dateFormat.parse(toParse);
    } catch (ParseException e) {
      throw new TranslationException(
          String.format("Unable to parse date for value [%s]. Expected format [%s].", toParse, dateFormat), e);
    }
  }

  protected DateFormat getDateFormat(MiraklCoreAttributeModel coreAttribute) {
    String format;
    if (coreAttribute.getType() == MiraklAttributeType.DATE) {
      format = extractDatePatternFromTypeParameters(coreAttribute);
    } else {
      format = configurationService.getConfiguration().getString(CATALOG_EXPORT_DATE_FORMAT);
    }

    return new SimpleDateFormat(format);
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
