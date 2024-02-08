package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.catalog.attributes.utils.TypeParametersUtils.extractListCodeFromTypeParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.category.model.CategoryModel;

public class DefaultCategoryWithExportableValuesAttributeHandler
    extends DefaultCategoryAttributeHandler<MiraklCategoryCoreAttributeModel> {

  protected ValueListNamingStrategy valueListNamingStrategy;

  @Override
  public List<Map<String, String>> getValues(MiraklCategoryCoreAttributeModel coreAttribute, MiraklExportCatalogContext context) {
    CategoryModel rootCategory =
        categoryService.getCategoryForCode(context.getExportConfig().getCatalogVersion(), coreAttribute.getRootCategoryCode());

    List<Map<String, String>> target = new ArrayList<>();
    for (CategoryModel category : rootCategory.getAllSubcategories()) {
      target.add(buildLine(category, coreAttribute, context));
    }

    return target;
  }

  protected Map<String, String> buildLine(CategoryModel category, MiraklCoreAttributeModel coreAttribute,
      MiraklExportCatalogContext context) {
    Map<String, String> target = new HashMap<>();
    target.put(MiraklValueListExportHeader.LIST_CODE.getCode(), extractListCodeFromTypeParameters(coreAttribute));
    target.put(MiraklValueListExportHeader.LIST_LABEL.getCode(),
        valueListNamingStrategy.getLabel(coreAttribute, context.getExportConfig().getDefaultLocale()));
    target.put(MiraklValueListExportHeader.VALUE_CODE.getCode(), category.getCode());
    target.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(), category.getName(context.getExportConfig().getDefaultLocale()));
    for (Locale additionalLocale : context.getExportConfig().getAdditionalLocales()) {
      target.put(MiraklValueListExportHeader.LIST_LABEL.getCode(additionalLocale),
          valueListNamingStrategy.getLabel(coreAttribute, additionalLocale));
      target.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(additionalLocale), category.getName(additionalLocale));
    }

    return target;
  }

  @Required
  public void setValueListNamingStrategy(ValueListNamingStrategy valueListNamingStrategy) {
    this.valueListNamingStrategy = valueListNamingStrategy;
  }
}
