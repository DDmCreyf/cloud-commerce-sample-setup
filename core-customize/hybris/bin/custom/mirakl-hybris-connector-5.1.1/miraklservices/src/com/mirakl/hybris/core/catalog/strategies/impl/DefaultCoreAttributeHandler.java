package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.catalog.attributes.utils.TypeParametersUtils.extractListCodeFromTypeParameters;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.util.services.impl.TranslationException;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

public class DefaultCoreAttributeHandler extends AbstractCoreAttributeHandler<MiraklCoreAttributeModel> {

  private static final Logger LOG = Logger.getLogger(DefaultCoreAttributeHandler.class);

  protected DefaultCoreAttributeValueTranslationStrategy attributeValueTranslationStrategy;
  protected TypeService typeService;
  protected EnumerationService enumerationService;
  protected ValueListNamingStrategy valueListNamingStrategy;

  @Override
  public List<Map<String, String>> getValues(MiraklCoreAttributeModel coreAttribute, MiraklExportCatalogContext context) {
    if (coreAttribute.getType() == MiraklAttributeType.LIST) {
      String ownerProductType = getProductOwnerType(coreAttribute, context);
      try {
        AttributeDescriptorModel attributeDescriptor =
            typeService.getAttributeDescriptor(ownerProductType, coreAttribute.getCode());
        TypeModel attributeType = attributeDescriptor.getAttributeType();
        if (attributeType instanceof EnumerationMetaTypeModel) {
          return getEnumerationValueLines(coreAttribute, context, attributeDescriptor);
        } else {
          handleUnsupportedAttributeType(coreAttribute, attributeType.getCode());
        }
      } catch (UnknownIdentifierException e) {
        handleMissingAttributeDescriptor(coreAttribute, ownerProductType);
      }
    }
    return super.getValues(coreAttribute, context);
  }

  protected List<Map<String, String>> getEnumerationValueLines(MiraklCoreAttributeModel coreAttribute,
      MiraklExportCatalogContext context, AttributeDescriptorModel attributeDescriptor) {
    List<Map<String, String>> target = new ArrayList<>();
    List<HybrisEnumValue> values = enumerationService.getEnumerationValues(attributeDescriptor.getAttributeType().getCode());
    for (HybrisEnumValue value : values) {
      target.add(buildLine(value, coreAttribute, context));
    }
    return target;
  }

  protected String getProductOwnerType(MiraklCoreAttributeModel coreAttribute, MiraklExportCatalogContext context) {
    Collection<ComposedTypeModel> composedTypeOwners = coreAttribute.getComposedTypeOwners();
    String rootProductTypeCode = context.getExportConfig().getRootProductType();
    if (!isEmpty(composedTypeOwners)) {
      return composedTypeOwners.iterator().next().getCode();
    }
    return rootProductTypeCode;
  }

  protected Map<String, String> buildLine(HybrisEnumValue value, MiraklCoreAttributeModel coreAttribute,
      MiraklExportCatalogContext context) {
    Map<String, String> target = new HashMap<>();
    target.put(MiraklValueListExportHeader.LIST_CODE.getCode(), extractListCodeFromTypeParameters(coreAttribute));
    target.put(MiraklValueListExportHeader.LIST_LABEL.getCode(),
        valueListNamingStrategy.getLabel(coreAttribute, context.getExportConfig().getDefaultLocale()));
    target.put(MiraklValueListExportHeader.VALUE_CODE.getCode(), value.getCode());
    target.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(), value.getCode());
    return target;
  }

  @Override
  public void setValue(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    setValue(attribute, determineOwner(attribute.getCoreAttribute(), data, context), data, context);
  }

  protected void setValue(AttributeValueData attribute, ProductModel attributeOwner, ProductImportData data,
      ProductImportFileContextData context) throws ProductImportException {

    MiraklCoreAttributeModel coreAttribute = attribute.getCoreAttribute();
    if (!isAttributePresentOnType(coreAttribute.getCode(), attributeOwner.getItemtype(), context)) {
      return;
    }

    try {
      modelService.setAttributeValue(attributeOwner, coreAttribute.getCode(),
          attributeValueTranslationStrategy.translateAttributeValue(attribute, attributeOwner));
    } catch (TranslationException e) {
      throw new ProductImportException(data.getRawProduct(),
          format("Unable to parse core attribute [%s] for persistence.", coreAttribute.getCode()), e);
    }
  }

  protected void handleUnsupportedAttributeType(MiraklCoreAttributeModel coreAttribute, String attributeType) {
    LOG.warn(String.format(
        "You may need to create your own handler to manage the type [%s] with core attribute [%s]. The default core attribute handler only supports lists whose type is enum",
        attributeType, coreAttribute.getUid()));
  }

  protected void handleMissingAttributeDescriptor(MiraklCoreAttributeModel coreAttribute, String ownerProductType) {
    LOG.warn(String.format(
        "Impossible to find attribute [%s] on product type [%s] for core attribute [%s]. Did you properly set the composed type owners ?",
        coreAttribute.getCode(), ownerProductType, coreAttribute.getUid()));
  }

  @Required
  public void setAttributeValueTranslationStrategy(
      DefaultCoreAttributeValueTranslationStrategy attributeValueTranslationStrategy) {
    this.attributeValueTranslationStrategy = attributeValueTranslationStrategy;
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setValueListNamingStrategy(ValueListNamingStrategy valueListNamingStrategy) {
    this.valueListNamingStrategy = valueListNamingStrategy;
  }
}
