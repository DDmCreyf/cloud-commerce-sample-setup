package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.ALL_BRANDS_CONTEXT_VARIABLE;
import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.ALL_CATEGORIES_CONTEXT_VARIABLE;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

public abstract class AbstractProductExportWithFallbackPopulator implements Populator<ProductModel, Map<String, String>> {

  protected SessionService sessionService;
  protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;
  protected BaseSiteService baseSiteService;
  protected ModelService modelService;

  @Override
  public void populate(ProductModel source, Map<String, String> target) throws ConversionException {
    populateAttributesIfNotPresent(source, target);
    fallbackToBaseProductForMissingAttributes(source, target);
  }

  protected void fallbackToBaseProductForMissingAttributes(ProductModel source, Map<String, String> target) {
    ProductModel product = source;

    while (shouldFallbackToBaseProduct(product)) {
      product = ((VariantProductModel) product).getBaseProduct();
      populateAttributesIfNotPresent(product, target);
    }
  }

  protected boolean shouldFallbackToBaseProduct(ProductModel product) {
    return product instanceof VariantProductModel;
  }

  protected abstract void populateAttributesIfNotPresent(ProductModel source, Map<String, String> target)
      throws ConversionException;

  protected String getCategoryCode(ProductModel productModel) {
    Collection<CategoryModel> categories = sessionService.getAttribute(ALL_CATEGORIES_CONTEXT_VARIABLE);
    return getCategoryCode(productModel, categories);
  }

  protected String getBrandLabel(ProductModel productModel) {
    Collection<CategoryModel> brands = sessionService.getAttribute(ALL_BRANDS_CONTEXT_VARIABLE);
    return getCategoryName(productModel, brands);
  }

  protected String getCategoryCode(ProductModel productModel, Collection<CategoryModel> categories) {
    CategoryModel category = getCategory(productModel, categories);
    return category != null ? category.getCode() : null;
  }

  protected String getCategoryName(ProductModel productModel, Collection<CategoryModel> categories) {
    CategoryModel category = getCategory(productModel, categories);
    return category != null ? category.getName() : null;
  }

  private CategoryModel getCategory(ProductModel productModel, Collection<CategoryModel> categories) {
    for (CategoryModel categoryModel : productModel.getSupercategories()) {
      if (categories.contains(categoryModel)) {
        return categoryModel;
      }
    }
    return null;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
    this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
  }

  @Required
  public void setBaseSiteService(BaseSiteService baseSiteService) {
    this.baseSiteService = baseSiteService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
