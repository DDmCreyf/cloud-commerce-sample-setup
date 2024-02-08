package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.util.DataModelUtils.extractPks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.product.daos.MiraklProductDao;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;
import com.mirakl.hybris.core.product.strategies.McmProductExportEligibilityStrategy;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

public class DefaultMcmProductExportEligibilityStrategy implements McmProductExportEligibilityStrategy {

  protected MiraklProductDao miraklProductDao;
  protected ModelService modelService;
  protected McmProductAcceptanceStrategy productAcceptanceStrategy;

  @Override
  public Collection<ProductModel> getProductDataSheetsEligibleForExport(ProductDataSheetExportContextData context) {
    Collection<String> validApprovalStatusCodes = productAcceptanceStrategy.getMappableApprovalStatusCodes();
    Set<ArticleApprovalStatus> validApprovalStatuses = transformToArticleApprovalStatuses(validApprovalStatusCodes);
    Collection<ProductModel> productDataSheetsEligibleForExport =
        miraklProductDao.findModifiedProductsWithNoVariantType(context.getModifiedAfter(),
            modelService.get(context.getProductCatalogVersion()), context.getProductOrigins(), validApprovalStatuses);
    Collection<ProductModel> filteredProductDataSheetsEligibleForExport = new HashSet<>();
    for (ProductModel productModel : productDataSheetsEligibleForExport) {
      if (isExportableProduct(productModel, context)) {
        filteredProductDataSheetsEligibleForExport.add(productModel);
      }
    }
    return filteredProductDataSheetsEligibleForExport;
  }

  protected boolean isExportableProduct(ProductModel productModel, ProductDataSheetExportContextData context) {
    return !Collections.disjoint(extractPks(findProductSupercategories(productModel)), context.getAllExportableCategories());
  }

  protected Collection<CategoryModel> findProductSupercategories(ProductModel productModel) {
    Set<CategoryModel> categoryModels = new HashSet<>();
    categoryModels.addAll(productModel.getSupercategories());
    while (productModel instanceof VariantProductModel) {
      productModel = ((VariantProductModel) productModel).getBaseProduct();
      categoryModels.addAll(productModel.getSupercategories());
    }
    return categoryModels;
  }

  protected Set<ArticleApprovalStatus> transformToArticleApprovalStatuses(Collection<String> validApprovalStatusCodes) {
    return FluentIterable.from(validApprovalStatusCodes).transform(new Function<String, ArticleApprovalStatus>() {

      @Override
      public ArticleApprovalStatus apply(String statusCode) {
        return ArticleApprovalStatus.valueOf(statusCode);
      }
    }).toSet();
  }

  @Required
  public void setMiraklProductDao(MiraklProductDao miraklProductDao) {
    this.miraklProductDao = miraklProductDao;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setProductAcceptanceStrategy(McmProductAcceptanceStrategy productAcceptanceStrategy) {
    this.productAcceptanceStrategy = productAcceptanceStrategy;
  }

}
