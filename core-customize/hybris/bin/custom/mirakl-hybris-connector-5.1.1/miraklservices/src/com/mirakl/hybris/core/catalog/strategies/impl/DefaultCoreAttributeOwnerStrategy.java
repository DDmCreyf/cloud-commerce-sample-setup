package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeOwnerStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultCoreAttributeOwnerStrategy implements CoreAttributeOwnerStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultCoreAttributeOwnerStrategy.class);

  protected TypeService typeService;

  @Override
  public ProductModel determineOwner(MiraklCoreAttributeModel attribute, ProductImportData data,
      ProductImportFileContextData context) {
    Collection<ComposedTypeModel> composedTypeOwners = attribute.getComposedTypeOwners();
    if (isNotEmpty(composedTypeOwners)) {
      ProductModel productToUpdate = data.getProductToUpdate();
      ComposedTypeModel composedType = typeService.getComposedTypeForClass(productToUpdate.getClass());
      while (!composedTypeOwners.contains(composedType) && productToUpdate instanceof VariantProductModel) {
        productToUpdate = ((VariantProductModel) productToUpdate).getBaseProduct();
        composedType = typeService.getComposedTypeForClass(productToUpdate.getClass());
      }
      if (composedTypeOwners.contains(composedType)) {
        return productToUpdate;
      } else {
        LOG.warn(format("Unable to find a type owner for attribute [%s]. Declared types [%s], product type: [%s]",
            attribute.getCode(), getComposedTypesAsString(composedTypeOwners), composedType.getCode()));
      }
    }

    if (attribute.isVariant() || attribute.isUniqueIdentifier()) {
      return data.getProductToUpdate();
    }
    return data.getRootBaseProductToUpdate();
  }

  protected Collection<String> getComposedTypesAsString(Collection<ComposedTypeModel> composedTypeOwners) {
    return FluentIterable.from(composedTypeOwners).transform(new Function<ComposedTypeModel, String>() {
      @Override
      public String apply(ComposedTypeModel composedType) {
        return composedType.getCode();
      }

    }).toSet();
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

}
