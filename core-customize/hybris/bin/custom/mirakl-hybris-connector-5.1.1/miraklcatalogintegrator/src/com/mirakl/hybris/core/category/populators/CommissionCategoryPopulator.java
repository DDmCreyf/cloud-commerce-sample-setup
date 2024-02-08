package com.mirakl.hybris.core.category.populators;

import static com.mirakl.hybris.core.enums.MiraklCategoryExportHeader.*;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.intersection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class CommissionCategoryPopulator
    implements ConfigurablePopulator<Pair<CategoryModel, Collection<CategoryModel>>, Map<String, String>, Locale> {

  @Override
  public void populate(Pair<CategoryModel, Collection<CategoryModel>> source, Map<String, String> target,
      Collection<Locale> locales) throws ConversionException {
    validateParameterNotNullStandardMessage("categoryToExportCategories", source);
    validateParameterNotNullStandardMessage("categoryMap", target);

    CategoryModel category = source.getKey();
    validateParameterNotNullStandardMessage("category", category);
    Collection<CategoryModel> exportCategories = source.getValue();
    validateParameterNotNullStandardMessage("exportCategories", exportCategories);

    target.put(CATEGORY_CODE.getCode(), category.getCode());
    target.put(CATEGORY_LABEL.getCode(), category.getName());
    for (Locale locale : locales) {
      target.put(CATEGORY_LABEL.getCode(locale), category.getName(locale));
    }
    target.put(PARENT_CODE.getCode(), getParentCategoryCode(category, exportCategories));
  }

  protected String getParentCategoryCode(CategoryModel source, Collection<CategoryModel> exportCategories) {
    List<CategoryModel> superCategories = source.getSupercategories();
    Collection<CategoryModel> exportedSuperCategories = intersection(superCategories, exportCategories);

    if (isNotEmpty(exportedSuperCategories)) {
      return exportedSuperCategories.iterator().next().getCode();
    }
    return EMPTY;
  }

}
