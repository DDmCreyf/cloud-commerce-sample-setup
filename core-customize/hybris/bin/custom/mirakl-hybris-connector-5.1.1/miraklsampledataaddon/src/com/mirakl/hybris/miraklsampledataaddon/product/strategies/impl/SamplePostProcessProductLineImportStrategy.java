package com.mirakl.hybris.miraklsampledataaddon.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.strategies.PostProcessProductLineImportStrategy;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.ProductTaxGroup;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

public class SamplePostProcessProductLineImportStrategy implements PostProcessProductLineImportStrategy {

  protected static final String TAX_GROUP_CODE_SUFFIX = ".tax.group.code";
  protected static final String DEFAULT_TAX_GROUP_CODE_PROPERTY = "default" + TAX_GROUP_CODE_SUFFIX;

  protected UnitService unitService;
  protected ModelService modelService;
  protected EnumerationService enumerationService;
  protected ConfigurationService configurationService;

  @Override
  public void postProcess(ProductImportData data, MiraklRawProductModel rawProduct, ProductImportFileContextData context) {
    applyProductUnit(data);
    assignPrimaryImage(data);
    assignProductTaxGroup(data, context);
  }

  protected void assignProductTaxGroup(ProductImportData data, ProductImportFileContextData context) {
    data.getProductToUpdate().setEurope1PriceFactory_PTG(getProductTaxGroup(data, context));
  }

  protected ProductTaxGroup getProductTaxGroup(ProductImportData data, ProductImportFileContextData context) {
    List<String> taxPropertyKeys = generateTaxPropertyKeys(data, context);
    for (String taxPropertyKey : taxPropertyKeys) {
      if (!isEmpty(Config.getParameter(taxPropertyKey))) {
        return enumerationService.getEnumerationValue(ProductTaxGroup.class,
            configurationService.getConfiguration().getString(taxPropertyKey));
      }
    }
    return null;
  }

  protected List<String> generateTaxPropertyKeys(ProductImportData data, ProductImportFileContextData context) {
    List<String> propertyKeys = new ArrayList<>();
    CatalogVersionModel productCatalogVersion = modelService.get(context.getGlobalContext().getProductCatalogVersion());
    CatalogModel productCatalog = productCatalogVersion.getCatalog();
    StringBuilder propertyKey = new StringBuilder(productCatalog.getId());
    propertyKeys.add(DEFAULT_TAX_GROUP_CODE_PROPERTY);
    propertyKeys.add(propertyKey + TAX_GROUP_CODE_SUFFIX);
    propertyKeys.add(propertyKey.append(".").append(productCatalog.getVersion()) + TAX_GROUP_CODE_SUFFIX);
    propertyKeys.add(propertyKey.append(".").append(getCategoryCode(data, context)) + TAX_GROUP_CODE_SUFFIX);
    return Lists.reverse(propertyKeys);
  }

  private String getCategoryCode(ProductImportData data, ProductImportFileContextData context) {
    MiraklCategoryCoreAttributeModel categoryCoreAttribute =
        modelService.get(context.getGlobalContext().getCategoryRoleAttribute());
    return data.getRawProduct().getValues().get(categoryCoreAttribute.getCode());
  }

  protected void applyProductUnit(ProductImportData data) {
    ProductModel product = data.getProductToUpdate();
    product.setUnit(unitService.getUnitForCode("pieces"));
    data.getModelsToSave().add(product);
  }

  protected void assignPrimaryImage(ProductImportData data) {
    ProductModel product = data.getProductToUpdate();
    if (isNotEmpty(product.getGalleryImages())) {
      Optional<MediaContainerModel> mediaContainerOptional = product.getGalleryImages().stream().findFirst();
      if (mediaContainerOptional.isPresent() && isNotEmpty(mediaContainerOptional.get().getMedias())) {
        product.setPicture(mediaContainerOptional.get().getMedias().iterator().next());
      }
    }
  }

  @Required
  public void setUnitService(UnitService unitService) {
    this.unitService = unitService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

}
