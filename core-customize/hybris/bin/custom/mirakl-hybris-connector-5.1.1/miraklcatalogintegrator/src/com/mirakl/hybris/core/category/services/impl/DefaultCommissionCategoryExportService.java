package com.mirakl.hybris.core.category.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroTracking;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroRequest;
import com.mirakl.hybris.core.category.services.CategoryExportService;
import com.mirakl.hybris.core.category.services.CommissionCategoryService;

import de.hybris.platform.category.model.CategoryModel;

public class DefaultCommissionCategoryExportService implements CategoryExportService {

  protected CommissionCategoryService commissionCategoryService;
  protected MiraklMarketplacePlatformFrontApi miraklOperatorApi;

  @Override
  public MiraklCategorySynchroTracking exportCommissionCategories(CategoryModel rootCategory, Locale locale, String fileName)
      throws IOException {

    return exportCommissionCategories(rootCategory, locale, fileName, ImmutableSet.of(locale));
  }

  @Override
  public MiraklCategorySynchroTracking exportCommissionCategories(CategoryModel rootCategory, Locale defaultLocale,
      String fileName, Set<Locale> additionalLocales) throws IOException {
    validateParameterNotNull(rootCategory, "Cannot export categories for null root category");

    Collection<CategoryModel> categories = commissionCategoryService.getCategories(rootCategory);
    Set<Locale> localesToExport = new HashSet<>();
    localesToExport.add(defaultLocale);
    if (additionalLocales != null) {
      localesToExport.addAll(additionalLocales);
    }
    String categoryExportCsvContent = commissionCategoryService.getCategoryExportCsvContent(defaultLocale, additionalLocales, categories);

    return miraklOperatorApi.synchronizeCategories(
        new MiraklCategorySynchroRequest(new ByteArrayInputStream(categoryExportCsvContent.getBytes()), fileName));
  }

  @Required
  public void setCommissionCategoryService(CommissionCategoryService commissionCategoryService) {
    this.commissionCategoryService = commissionCategoryService;
  }

  @Required
  public void setMiraklOperatorApi(MiraklMarketplacePlatformFrontApi miraklOperatorApi) {
    this.miraklOperatorApi = miraklOperatorApi;
  }
}
