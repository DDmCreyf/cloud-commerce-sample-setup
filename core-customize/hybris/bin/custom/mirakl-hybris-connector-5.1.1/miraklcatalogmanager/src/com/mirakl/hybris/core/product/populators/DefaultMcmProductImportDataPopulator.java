package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.VARIANT_GROUP_CODE_ATTRIBUTE;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.services.McmProductService;
import com.mirakl.hybris.core.product.strategies.UniqueIdentifierMatchingStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMcmProductImportDataPopulator
    implements Populator<Pair<MiraklRawProductModel, ProductImportFileContextData>, ProductImportData> {

  protected McmProductService mcmProductService;
  protected ModelService modelService;
  protected UniqueIdentifierMatchingStrategy uniqueIdentifierMatchingStrategy;
  protected ProductService productService;

  @Override
  public void populate(Pair<MiraklRawProductModel, ProductImportFileContextData> source, ProductImportData target)
      throws ConversionException {
    MiraklRawProductModel rawProduct = source.getLeft();
    ProductImportFileContextData context = source.getRight();
    CatalogVersionModel catalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());

    target.setModelsToSave(new HashSet<ItemModel>());
    target.setRawProduct(rawProduct);
    resolveProductsByUID(target, context);
    resolveProductBySku(target, rawProduct, catalogVersion);
    if (context.getGlobalContext().getVariantAttributesPerType() != null) {
      String variantGroupCode = rawProduct.getValues().get(getVariantGroupAttributeCode(context));
      target.setVariantGroupCode(variantGroupCode);
      if (StringUtils.isNotBlank(variantGroupCode)) {
        target.setProductResolvedByVariantGroup(
            mcmProductService.getProductForMiraklVariantGroupCode(variantGroupCode, catalogVersion));
      }
    }
  }

  protected void resolveProductsByUID(ProductImportData target, ProductImportFileContextData context) {
    target.setProductsResolvedByUID(uniqueIdentifierMatchingStrategy.getMatches(target, context));
  }

  protected void resolveProductBySku(ProductImportData target, MiraklRawProductModel rawProduct,
      CatalogVersionModel catalogVersion) {
    if (!isBlank(rawProduct.getSku())) {
      target.setProductResolvedBySku(productService.getProductForCode(catalogVersion, rawProduct.getSku()));
    }
  }

  protected String getVariantGroupAttributeCode(ProductImportFileContextData context) {
    return context.getGlobalContext().getCoreAttributePerRole().get(VARIANT_GROUP_CODE_ATTRIBUTE);
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMcmProductService(McmProductService mcmProductService) {
    this.mcmProductService = mcmProductService;
  }

  @Required
  public void setUniqueIdentifierMatchingStrategy(UniqueIdentifierMatchingStrategy uniqueIdentifierMatchingStrategy) {
    this.uniqueIdentifierMatchingStrategy = uniqueIdentifierMatchingStrategy;
  }

  @Required
  public void setProductService(ProductService productService) {
    this.productService = productService;
  }
}
