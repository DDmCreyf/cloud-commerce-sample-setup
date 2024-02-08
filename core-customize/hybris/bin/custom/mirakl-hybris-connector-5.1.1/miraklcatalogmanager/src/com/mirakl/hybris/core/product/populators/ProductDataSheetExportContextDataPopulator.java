package com.mirakl.hybris.core.product.populators;

import static com.google.common.base.Preconditions.checkArgument;
import static com.mirakl.hybris.core.util.DataModelUtils.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.catalog.services.MiraklCoreAttributeService;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class ProductDataSheetExportContextDataPopulator
    implements Populator<MiraklExportSellableProductsCronJobModel, ProductDataSheetExportContextData> {

  protected MiraklCoreAttributeService coreAttributeService;
  protected MiraklProductService miraklProductService;
  protected CommonI18NService commonI18NService;
  protected CategoryService categoryService;

  @Override
  public void populate(MiraklExportSellableProductsCronJobModel source, ProductDataSheetExportContextData target)
      throws ConversionException {
    checkArgument(source.getBaseSite() != null, "Base site must be provided");
    checkArgument(source.getCatalogVersion() != null, "Catalog Version must be provided");
    checkArgument(source.getProductOrigins() != null, "Product Origins must be provided");

    target.setBaseSite(source.getBaseSite().getPk());
    Map<String, MiraklCoreAttributeModel> coreAttributeCodes =
        coreAttributeService.getCoreAttributeCodes(source.getCoreAttributes());
    target.setCoreAttributes(transformMapValuesToPks(coreAttributeCodes));
    target.setFilename(source.getSynchronizationFileName());
    target.setModifiedAfter(source.isFullExport() ? null : source.getLastExportDate());
    target.setProductOrigins(source.getProductOrigins());
    target.setProductCatalogVersion(source.getCatalogVersion().getPk());
    Map<String, Set<CategoryModel>> allCategoryValues = coreAttributeService
        .getAllCategoryValuesForCategoryCoreAttributes(source.getCoreAttributes(), source.getCatalogVersion());
    target.setAllCategoryValues(transformMapCollectionValuesToPks(allCategoryValues));
    final MiraklCategoryCoreAttributeModel categoryCoreAttribute = coreAttributeService
        .getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, source.getCoreAttributeConfiguration());
    CategoryModel rootCategory =
        categoryService.getCategoryForCode(source.getCatalogVersion(), categoryCoreAttribute.getRootCategoryCode());
    final Set<CategoryModel> exportableCategories = getExportableCategories(rootCategory);
    target.setAllExportableCategories(extractPks(exportableCategories));
    target.setAttributesPerType(
        transformMapComposedTypeKeyToCode(miraklProductService.getAttributeDescriptorQualifiersPerProductType()));
    target.setTranslatableLocales(getLocalesForLanguages(source.getCatalogVersion().getCatalog().getTranslatableLanguages()));
    target.setMiraklCatalogSystem(source.getCatalogVersion().getCatalog().getMiraklCatalogSystem());
  }

  protected Set<CategoryModel> getExportableCategories(CategoryModel categoryModel) {
    final Collection<CategoryModel> subcategories = categoryModel.getCategories();
    final Set<CategoryModel> exportableCategories = new HashSet<>();
    if (CollectionUtils.isNotEmpty(subcategories) && !categoryModel.isOperatorExclusive()) {
      exportableCategories.add(categoryModel);
      for (CategoryModel subcategory : subcategories) {
        if (!subcategory.isOperatorExclusive()) {
          exportableCategories.addAll(getExportableCategories(subcategory));
        }
      }
    } else if (!categoryModel.isOperatorExclusive()) {
      exportableCategories.add(categoryModel);
    }
    return exportableCategories;
  }

  protected List<Locale> getLocalesForLanguages(Collection<LanguageModel> languages) {
    if (languages == null) {
      return Collections.emptyList();
    }

    return FluentIterable.from(languages).transform(new Function<LanguageModel, Locale>() {

      @Override
      public Locale apply(LanguageModel language) {
        return commonI18NService.getLocaleForLanguage(language);
      }
    }).toList();
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

  @Required
  public void setCoreAttributeService(MiraklCoreAttributeService coreAttributeService) {
    this.coreAttributeService = coreAttributeService;
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }
}
