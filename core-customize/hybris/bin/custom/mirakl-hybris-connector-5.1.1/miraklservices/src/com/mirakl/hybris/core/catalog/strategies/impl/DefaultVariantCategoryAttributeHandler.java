package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.List;
import java.util.Set;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;

public class DefaultVariantCategoryAttributeHandler extends DefaultCategoryWithExportableValuesAttributeHandler {

  @Override
  public void setValue(AttributeValueData attributeValue, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    super.setValue(attributeValue, data, context);
    if (isBlank(attributeValue.getValue())) {
      return;
    }
    MiraklCategoryCoreAttributeModel coreAttribute = (MiraklCategoryCoreAttributeModel) attributeValue.getCoreAttribute();
    Set<PK> allAttributeCategories = getAllAttributeCategories(coreAttribute, context);
    CategoryModel variantValue = getCurrentCategory(data.getProductToUpdate().getSupercategories(), allAttributeCategories);
    if (variantValue != null) {
      CategoryModel variantCategory = getVariantCategory(variantValue);
      if (!getSuperCategories(data.getRootBaseProductToUpdate()).contains(variantCategory)) {
        addCategory(data.getRootBaseProductToUpdate(), variantCategory, coreAttribute, context);
      }
    }
  }

  protected CategoryModel getVariantCategory(CategoryModel currentCategory) {
    List<CategoryModel> supercategories = currentCategory.getSupercategories();
    if (isEmpty(supercategories) || supercategories.size() > 1) {
      throw new IllegalStateException(
          format("Expected to find exactly one variant category as a supercategory of [%s]", currentCategory.getCode()));
    }
    return supercategories.get(0);
  }
}
