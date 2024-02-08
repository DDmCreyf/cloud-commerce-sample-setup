package com.mirakl.hybris.core.product.strategies.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.ProductCodeGenerationStrategy;
import com.mirakl.hybris.core.product.strategies.ProductCreationStrategy;
import com.mirakl.hybris.core.product.strategies.VariantTypeResolutionStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

public class DefaultProductCreationStrategy implements ProductCreationStrategy {

  protected ModelService modelService;
  protected VariantsService variantsService;
  protected TypeService typeService;
  protected VariantTypeResolutionStrategy variantTypeResolutionStrategy;
  protected ProductCodeGenerationStrategy productCodeGenerationStrategy;

  @Override
  public ProductModel createProduct(ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    variantTypeResolutionStrategy.resolveVariantType(data, context);

    if (data.getVariantType() == null) {
      ComposedTypeModel composedType = typeService.getComposedTypeForCode(context.getGlobalContext().getRootProductType());
      ProductModel product = modelService.create(composedType.getCode());
      product.setCode(productCodeGenerationStrategy.generateCode(composedType, data.getRawProduct(), null, context));
      product.setOrigin(ProductOrigin.MARKETPLACE);
      CatalogVersionModel productCatalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
      product.setCatalogVersion(productCatalogVersion);
      data.getModelsToSave().add(product);

      return product;
    }

    Map<String, List<String>> variantTypeHierarchyPerType = context.getGlobalContext().getVariantTypeHierarchyPerType();
    List<String> typesToCreate = variantTypeHierarchyPerType.get(data.getVariantType().getCode());

    ProductModel resolvedBaseProduct = data.getProductResolvedByVariantGroup();
    if (resolvedBaseProduct == null) {
      return createProductHierarchy(typesToCreate, null, data, context);
    }

    return createRemainingProductHierarchy(resolvedBaseProduct, data, context, typesToCreate);
  }

  protected ProductModel createRemainingProductHierarchy(ProductModel resolvedBaseProduct, ProductImportData data,
      ProductImportFileContextData context, List<String> typesToCreate) throws ProductImportException {
    ProductModel baseProduct = resolvedBaseProduct;
    ProductModel closestBaseProduct = resolvedBaseProduct;
    int typeIndex = 1;
    ListIterator<String> listIterator = typesToCreate.listIterator(typeIndex);

    while (listIterator.hasNext() && closestBaseProduct != null) {
      closestBaseProduct = getMatchingVariantFromBaseProduct(baseProduct,
          context.getGlobalContext().getVariantAttributesPerType().get(listIterator.next()), data.getRawProduct());
      if (closestBaseProduct != null) {
        baseProduct = closestBaseProduct;
        typeIndex++;
      }
    }

    List<String> remainingTypesToCreate = getRemainingTypesToCreate(typesToCreate, typeIndex);
    if (CollectionUtils.isEmpty(remainingTypesToCreate)) {
      throw new ProductImportException(data.getRawProduct(),
          "No product was created. Variant attributes combination matched an already existing product");
    }

    return createProductHierarchy(remainingTypesToCreate, baseProduct, data, context);
  }

  protected List<String> getRemainingTypesToCreate(List<String> types, int indexFrom) {
    if (indexFrom < types.size()) {
      return types.subList(indexFrom, types.size());
    }
    return Collections.emptyList();
  }

  protected ProductModel createProductHierarchy(List<String> typesToCreate, ProductModel rootBaseProduct, ProductImportData data,
      ProductImportFileContextData context) {
    ProductModel product = null;
    ProductModel baseProduct = rootBaseProduct;

    for (String composedTypeCode : typesToCreate) {
      ComposedTypeModel composedType = typeService.getComposedTypeForCode(composedTypeCode);
      product = modelService.create(composedType.getCode());
      product.setOrigin(ProductOrigin.MARKETPLACE);
      product.setCode(productCodeGenerationStrategy.generateCode(composedType, data.getRawProduct(), baseProduct, context));
      data.getModelsToSave().add(product);
      CatalogVersionModel productCatalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
      product.setCatalogVersion(productCatalogVersion);
      if (baseProduct != null && product instanceof VariantProductModel) {
        ((VariantProductModel) product).setBaseProduct(baseProduct);
        if (composedType instanceof VariantTypeModel) {
          baseProduct.setVariantType((VariantTypeModel) composedType);
        }
      }
      baseProduct = product;
    }

    return product;
  }

  protected VariantProductModel getMatchingVariantFromBaseProduct(ProductModel baseProduct, Set<String> variantAttributes,
      MiraklRawProductModel rawProduct) throws ProductImportException {
    if (baseProduct.getVariantType() == null) {
      return null;
    }
    HashMap<String, Object> attributeValues = new HashMap<>();
    for (String variantAttribute : variantAttributes) {
      attributeValues.put(variantAttribute, rawProduct.getValues().get(variantAttribute));
    }
    Collection<VariantProductModel> variants = variantsService.getVariantProductForAttributeValues(baseProduct, attributeValues);
    if (CollectionUtils.isEmpty(variants)) {
      return null;
    }
    return variants.iterator().next();
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setVariantsService(VariantsService variantsService) {
    this.variantsService = variantsService;
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }

  @Required
  public void setVariantTypeResolutionStrategy(VariantTypeResolutionStrategy variantTypeResolutionStrategy) {
    this.variantTypeResolutionStrategy = variantTypeResolutionStrategy;
  }

  public VariantTypeResolutionStrategy getVariantTypeResolutionStrategy() {
    return variantTypeResolutionStrategy;
  }

  @Required
  public void setProductCodeGenerationStrategy(ProductCodeGenerationStrategy productCodeGenerationStrategy) {
    this.productCodeGenerationStrategy = productCodeGenerationStrategy;
  }
}
