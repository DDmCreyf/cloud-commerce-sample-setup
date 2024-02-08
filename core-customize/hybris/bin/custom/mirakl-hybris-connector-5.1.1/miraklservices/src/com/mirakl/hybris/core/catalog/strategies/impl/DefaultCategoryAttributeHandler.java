package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultCategoryAttributeHandler<T extends MiraklCategoryCoreAttributeModel> extends AbstractCoreAttributeHandler<T> {

  protected CategoryService categoryService;

  @Override
  public void setValue(AttributeValueData attributeValue, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    validateHandler(attributeValue, data, context);
    @SuppressWarnings("unchecked")
    T coreAttribute = (T) attributeValue.getCoreAttribute();

    ProductModel owner = determineOwner(coreAttribute, data, context);
    markItemsToSave(data, owner);

    String categoryCode = attributeValue.getValue();
    if (isBlank(categoryCode)) {
      removeCurrentCategory(owner, coreAttribute, context);
      return;
    }

    try {
      CatalogVersionModel productCatalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
      CategoryModel receivedCategory = getReceivedCategory(attributeValue, productCatalogVersion, data, context);
      addCategory(owner, receivedCategory, coreAttribute, context);
    } catch (AmbiguousIdentifierException | UnknownIdentifierException e) {
      throw new ProductImportException(data.getRawProduct(),
          format("Unable to find a category with code [%s] while updating attribute [%s]", categoryCode, coreAttribute.getCode()),
          e);
    }
  }

  protected void validateHandler(AttributeValueData attributeValue, ProductImportData data,
      ProductImportFileContextData context) {
    if (!(attributeValue.getCoreAttribute() instanceof MiraklCategoryCoreAttributeModel)) {
      throw new IllegalStateException(
          format("Problem with the handler of core attribute [%s]. [%s] should only be used with a core attribute of type [%s]",
              attributeValue.getCoreAttribute().getCode(), this.getClass(), MiraklCategoryCoreAttributeModel._TYPECODE));
    }
  }

  protected Collection<CategoryModel> removeCurrentCategory(ProductModel product, MiraklCategoryCoreAttributeModel coreAttribute,
      ProductImportFileContextData context) {
    Collection<CategoryModel> superCategories = getSuperCategories(product);
    CategoryModel currentCategory = getCurrentCategory(superCategories, getAllAttributeCategories(coreAttribute, context));
    if (currentCategory != null) {
      superCategories.remove(currentCategory);
      product.setSupercategories(superCategories);
    }
    return superCategories;
  }

  protected CategoryModel getReceivedCategory(AttributeValueData attributeValue, CatalogVersionModel catalogVersion,
      ProductImportData data, ProductImportFileContextData context) {
    return categoryService.getCategoryForCode(catalogVersion, attributeValue.getValue());
  }

  protected void addCategory(ProductModel product, CategoryModel receivedCategory, MiraklCategoryCoreAttributeModel coreAttribute,
      ProductImportFileContextData context) {
    Collection<CategoryModel> superCategories = getSuperCategories(product);
    if (!superCategories.contains(receivedCategory)) {
      superCategories = removeCurrentCategory(product, coreAttribute, context);
      superCategories.add(receivedCategory);
      product.setSupercategories(superCategories);
    }
  }

  protected CategoryModel getCurrentCategory(Collection<CategoryModel> supercategories, final Collection<PK> allCategories) {
    return FluentIterable.from(supercategories).firstMatch(new Predicate<CategoryModel>() {

      @Override
      public boolean apply(CategoryModel category) {
        return allCategories.contains(category.getPk());
      }
    }).orNull();
  }

  protected Collection<CategoryModel> getSuperCategories(ProductModel product) {
    return (product.getSupercategories() == null) ? new HashSet<>() : new HashSet<>(product.getSupercategories());
  }

  protected Set<PK> getAllAttributeCategories(MiraklCategoryCoreAttributeModel attribute, ProductImportFileContextData context) {
    return context.getGlobalContext().getAllCategoryValues().get(attribute.getUid());
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

}
