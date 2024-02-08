package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeOwnerStrategy;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractCoreAttributeHandler<T extends MiraklCoreAttributeModel> implements CoreAttributeHandler<T> {

  protected ModelService modelService;
  protected CoreAttributeOwnerStrategy coreAttributeOwnerStrategy;

  @Override
  public List<Map<String, String>> getValues(T coreAttribute, MiraklExportCatalogContext context) {
    return Collections.emptyList();
  }

  protected ProductModel determineOwner(T coreAttribute, ProductImportData data, ProductImportFileContextData context) {
    return coreAttributeOwnerStrategy.determineOwner(coreAttribute, data, context);
  }

  protected boolean isAttributePresentOnType(String attribute, String typeCode, ProductImportFileContextData context) {
    Set<String> typeAttributes = context.getGlobalContext().getAttributesPerType().get(typeCode);
    return typeAttributes.contains(attribute);
  }

  protected void markItemsToSave(ProductImportData data, ItemModel... items) {
    data.getModelsToSave().addAll(asList(items));
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setCoreAttributeOwnerStrategy(CoreAttributeOwnerStrategy coreAttributeOwnerStrategy) {
    this.coreAttributeOwnerStrategy = coreAttributeOwnerStrategy;
  }

}