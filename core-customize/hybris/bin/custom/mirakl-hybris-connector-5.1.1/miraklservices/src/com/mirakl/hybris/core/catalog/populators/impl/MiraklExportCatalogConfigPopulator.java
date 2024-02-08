package com.mirakl.hybris.core.catalog.populators.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.model.MiraklExportCatalogCronJobModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class MiraklExportCatalogConfigPopulator implements Populator<MiraklExportCatalogCronJobModel, MiraklExportCatalogConfig> {

  protected CommonI18NService commonI18NService;
  protected CategoryService categoryService;

  @Override
  public void populate(MiraklExportCatalogCronJobModel source, MiraklExportCatalogConfig target) throws ConversionException {

    CatalogVersionModel catalogVersion = source.getCatalogVersion();
    CategoryModel rootCategory =
        categoryService.getCategoryForCode(catalogVersion, source.getCoreAttributeConfiguration().getProductRootCategoryCode());

    target.setRootCategory(rootCategory);
    target.setCatalogVersion(catalogVersion);
    target.setCoreAttributes(source.getCoreAttributes());
    target.setExportAttributes(source.isExportAttributes());
    target.setExportCategories(source.isExportCategories());
    target.setExportValueLists(source.isExportValueLists());
    target.setDefaultLocale(commonI18NService.getLocaleForLanguage(source.getDefaultLanguage()));
    target.setAdditionalLocales(getLocalesForLanguages(source.getAdditionalLanguages()));
    target.setTranslatableLocales(getLocalesForLanguages(catalogVersion.getCatalog().getTranslatableLanguages()));
    target.setCategoriesFilename(source.getCategoriesFileName());
    target.setAttributesFilename(source.getAttributesFileName());
    target.setValueListsFilename(source.getValueListsFileName());
    target.setDryRunMode(source.isDryRunMode());
    target.setImportTimeout(source.getImportTimeout());
    target.setImportCheckInterval(source.getImportCheckInterval());
    target.setRootProductType(getRootProductType(source));
    target.setMiraklCatalogSystem(catalogVersion.getCatalog().getMiraklCatalogSystem());
    target.setExcludeRootCategory(source.isExcludeRootCategory());
  }

  protected String getRootProductType(MiraklExportCatalogCronJobModel source) {
    ComposedTypeModel rootProductType = source.getCatalogVersion().getCatalog().getRootProductType();
    if (rootProductType == null) {
      return ProductModel._TYPECODE;
    }
    return rootProductType.getCode();
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
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }
}
