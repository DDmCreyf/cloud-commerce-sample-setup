package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Locale;
import java.util.Set;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultMcmCategoryAttributeHandler<T extends MiraklCategoryCoreAttributeModel>
    extends AbstractMcmCoreAttributeHandler<T> {

  @Override
  public String getValue(ProductModel product, T coreAttribute, ProductDataSheetExportContextData context) {
    Set<PK> allCategoryPKs = context.getAllCategoryValues().get(coreAttribute.getUid());
    return isNotEmpty(allCategoryPKs) ? findCategory(product, allCategoryPKs) : null;
  }

  protected String findCategory(ProductModel product, Set<PK> allCategoryPKs) {
    if (isNotEmpty(product.getSupercategories())) {
      for (CategoryModel category : product.getSupercategories()) {
        if (allCategoryPKs.contains(category.getPk())) {
          return category.getCode();
        }
      }
    }
    if (product instanceof VariantProductModel) {
      return findCategory(((VariantProductModel) product).getBaseProduct(), allCategoryPKs);
    }
    return null;
  }

  @Override
  public String getValue(ProductModel product, T coreAttribute, Locale locale, ProductDataSheetExportContextData context) {
    return getValue(product, coreAttribute, context);
  }

}
