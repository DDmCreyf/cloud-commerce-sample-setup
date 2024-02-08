package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.VariantTypeResolutionStrategy;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantTypeModel;

public class DefaultVariantTypeResolutionStrategy implements VariantTypeResolutionStrategy {

  protected TypeService typeService;

  @Override
  public void resolveVariantType(ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    Map<String, String> values = data.getRawProduct().getValues();
    Set<String> filledVariantAttributes = new HashSet<>();
    ProductImportGlobalContextData globalContext = context.getGlobalContext();

    for (String variantCoreAttribute : globalContext.getVariantAttributes()) {
      if (StringUtils.isNotBlank(values.get(variantCoreAttribute))) {
        filledVariantAttributes.add(variantCoreAttribute);
      }
    }

    if (isEmpty(filledVariantAttributes)) {
      return;
    }

    Map<String, Set<String>> variantAttributeCodesPerType = globalContext.getVariantAttributesPerType();
    for (Entry<String, Set<String>> entry : variantAttributeCodesPerType.entrySet()) {
      Set<String> variantAttributes = entry.getValue();
      if (variantAttributes.size() == filledVariantAttributes.size()) {
        ComposedTypeModel composedType = typeService.getComposedTypeForCode(entry.getKey());
        if (filledVariantAttributes.containsAll(variantAttributes) && composedType instanceof VariantTypeModel) {
          data.setVariantType((VariantTypeModel) composedType);
          return;
        }
      }
    }

    throw new IllegalStateException(
        format("Cannot find any variant type declaring the variant attributes [%s]", filledVariantAttributes));
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }
}
