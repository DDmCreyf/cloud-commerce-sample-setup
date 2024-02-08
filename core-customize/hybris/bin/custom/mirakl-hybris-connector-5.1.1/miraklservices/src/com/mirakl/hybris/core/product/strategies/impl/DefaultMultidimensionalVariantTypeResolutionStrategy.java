package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashSet;
import java.util.Map;
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
import de.hybris.platform.variants.model.GenericVariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

public class DefaultMultidimensionalVariantTypeResolutionStrategy implements VariantTypeResolutionStrategy {

  protected TypeService typeService;

  @Override
  public void resolveVariantType(ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    Set<String> filledVariantAttributes = getFilledVariantAttributes(data, context.getGlobalContext());

    if (isNotEmpty(filledVariantAttributes)) {
      ComposedTypeModel composedType = typeService.getComposedTypeForCode(GenericVariantProductModel._TYPECODE);
      data.setVariantType((VariantTypeModel) composedType);
    }
  }

  protected Set<String> getFilledVariantAttributes(ProductImportData data, ProductImportGlobalContextData globalContext) {
    Set<String> filledVariantAttributes = new HashSet<>();
    Map<String, String> values = data.getRawProduct().getValues();
    for (String variantCoreAttribute : globalContext.getVariantAttributes()) {
      if (StringUtils.isNotBlank(values.get(variantCoreAttribute))) {
        filledVariantAttributes.add(variantCoreAttribute);
      }
    }
    return filledVariantAttributes;
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }
}
