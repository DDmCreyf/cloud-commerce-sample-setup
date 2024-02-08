package com.mirakl.hybris.core.category.services.impl;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.category.populators.CommissionCategoryPopulator;
import com.mirakl.hybris.core.category.services.CommissionCategoryService;
import com.mirakl.hybris.core.enums.MiraklCategoryExportHeader;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultCommissionCategoryService implements CommissionCategoryService {

  protected CategoryService categoryService;
  protected SessionService sessionService;
  protected I18NService i18NService;
  protected CsvService csvService;
  protected CommissionCategoryPopulator commissionCategoryPopulator;
  protected MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;

  @Override
  public Collection<CategoryModel> getCategories(CategoryModel rootCategory) {
    List<CategoryModel> subCategoriesForRootCatalogVersion = new ArrayList<>();
    subCategoriesForRootCatalogVersion.add(rootCategory);
    Collection<CategoryModel> allSubcategories = categoryService.getAllSubcategoriesForCategory(rootCategory);

    CatalogVersionModel rootCatalogVersion = rootCategory.getCatalogVersion();
    for (CategoryModel subcategory : allSubcategories) {
      if (rootCatalogVersion.equals(subcategory.getCatalogVersion())) {
        subCategoriesForRootCatalogVersion.add(subcategory);
      }
    }
    return subCategoriesForRootCatalogVersion;
  }

  @Override
  public String getCategoryExportCsvContent(final Locale locale, final Collection<CategoryModel> categories) throws IOException {
    return getCategoryExportCsvContent(locale, ImmutableSet.of(locale), categories);
  }

  @Override
  public String getCategoryExportCsvContent(final Locale defaultLocale, final Set<Locale> additionalLocales,
      final Collection<CategoryModel> categories) throws IOException {
    return csvService.createCsvWithHeaders(
        miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklCategoryExportHeader.class, additionalLocales),
        mapExportCategories(defaultLocale, additionalLocales, getCategoryPairs(categories)));
  }

  protected List<Map<String, String>> mapExportCategories(final Locale locale,
      final Collection<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairs) {
    return mapExportCategories(locale, ImmutableSet.of(locale), categoryPairs);
  }

  protected List<Map<String, String>> mapExportCategories(final Locale defaultLocale, final Set<Locale> locales,
      final Collection<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairs) {

    return sessionService.executeInLocalView(new SessionExecutionBody() {
      @Override
      public Object execute() {
        i18NService.setCurrentLocale(defaultLocale);
        final List<Map<String, String>> listOfExportableValues = new ArrayList<>();

        for (Pair<CategoryModel, Collection<CategoryModel>> next : categoryPairs) {
          Map<String, String> csvHeaderWithValuesMap = new HashMap<>();
          commissionCategoryPopulator.populate(next, csvHeaderWithValuesMap, locales);
          listOfExportableValues.add(csvHeaderWithValuesMap);
        }
        return listOfExportableValues;
      }
    });
  }

  protected Collection<Pair<CategoryModel, Collection<CategoryModel>>> getCategoryPairs(Collection<CategoryModel> categories) {
    Collection<Pair<CategoryModel, Collection<CategoryModel>>> categoryPairs = new ArrayList<>();

    for (CategoryModel category : categories) {
      categoryPairs.add(Pair.of(category, categories));
    }
    return categoryPairs;
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setI18NService(I18NService i18NService) {
    this.i18NService = i18NService;
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }

  @Required
  public void setCommissionCategoryPopulator(CommissionCategoryPopulator commissionCategoryPopulator) {
    this.commissionCategoryPopulator = commissionCategoryPopulator;
  }

  @Required
  public void setMiraklExportHeaderResolverStrategy(MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy) {
    this.miraklExportHeaderResolverStrategy = miraklExportHeaderResolverStrategy;
  }
}
