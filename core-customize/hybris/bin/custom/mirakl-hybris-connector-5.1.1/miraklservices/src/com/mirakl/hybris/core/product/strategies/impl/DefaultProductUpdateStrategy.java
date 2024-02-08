package com.mirakl.hybris.core.product.strategies.impl;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeUpdateStrategy;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;
import com.mirakl.hybris.core.product.strategies.ProductUpdateStrategy;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultProductUpdateStrategy implements ProductUpdateStrategy {

  protected ModelService modelService;
  protected ClassificationAttributeUpdateStrategy classificationAttributeUpdateStrategy;
  protected CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  protected Converter<Pair<Entry<String, String>, ProductImportFileContextData>, AttributeValueData> attributeValueDataConverter;

  @Override
  public void applyValues(final ProductImportData data, final ProductImportFileContextData context)
      throws ProductImportException {
    Set<AttributeValueData> classificationAttributeValues = new HashSet<>();
    MiraklRawProductModel rawProduct = data.getRawProduct();

    for (Entry<String, String> entry : rawProduct.getValues().entrySet()) {
      AttributeValueData attributeValueData = attributeValueDataConverter.convert(Pair.of(entry, context));
      if (attributeValueData.getCoreAttribute() != null) {
        handleCoreAttribute(attributeValueData, data, context);
      } else {
        classificationAttributeValues.add(attributeValueData);
      }
    }

    saveAllModels(data);

    handleClassificationAttributes(classificationAttributeValues, data, context);
  }

  protected void handleCoreAttribute(AttributeValueData attribute, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    CoreAttributeHandler<MiraklCoreAttributeModel> handler =
        coreAttributeHandlerResolver.determineHandler(attribute.getCoreAttribute(), data, context);

    if (handler == null) {
      throw new IllegalStateException(format("Unable to define a handler for core attribute [%s]", attribute.getCode()));
    }

    handler.setValue(attribute, data, context);
  }

  protected void handleClassificationAttributes(Collection<AttributeValueData> attributes, ProductImportData data,
      ProductImportFileContextData context) throws ProductImportException {
    classificationAttributeUpdateStrategy.updateAttributes(attributes, data, context);
  }

  protected void saveAllModels(final ProductImportData data) {
    data.getModelsToSave().remove(null);
    modelService.saveAll(data.getModelsToSave());
    data.setModelsToSave(new HashSet<ItemModel>());
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setClassificationAttributeUpdateStrategy(
      ClassificationAttributeUpdateStrategy classificationAttributeUpdateStrategy) {
    this.classificationAttributeUpdateStrategy = classificationAttributeUpdateStrategy;
  }

  @Required
  public void setCoreAttributeHandlerResolver(CoreAttributeHandlerResolver coreAttributeHandlerResolver) {
    this.coreAttributeHandlerResolver = coreAttributeHandlerResolver;
  }

  @Required
  public void setAttributeValueDataConverter(
      Converter<Pair<Entry<String, String>, ProductImportFileContextData>, AttributeValueData> attributeValueDataConverter) {
    this.attributeValueDataConverter = attributeValueDataConverter;
  }

}
