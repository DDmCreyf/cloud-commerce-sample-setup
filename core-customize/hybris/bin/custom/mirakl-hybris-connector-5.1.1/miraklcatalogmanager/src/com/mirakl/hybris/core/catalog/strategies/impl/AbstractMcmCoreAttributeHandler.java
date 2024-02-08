package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.attributes.McmCoreAttributeHandler;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractMcmCoreAttributeHandler<T extends MiraklCoreAttributeModel> implements McmCoreAttributeHandler<T> {

  private static final Logger LOG = Logger.getLogger(AbstractMcmCoreAttributeHandler.class);

  protected CoreAttributeHandler<T> fallbackHandler;
  protected ModelService modelService;

  @Override
  public List<Map<String, String>> getValues(T coreAttribute, MiraklExportCatalogContext context) {
    if (fallbackHandler != null) {
      return fallbackHandler.getValues(coreAttribute, context);
    }
    return Collections.emptyList();
  }

  @Override
  public void setValue(AttributeValueData receivedValue, ProductImportData data, ProductImportFileContextData context)
      throws ProductImportException {
    if (fallbackHandler != null) {
      fallbackHandler.setValue(receivedValue, data, context);
    }
  }

  protected boolean isAttributePresentOnType(String attribute, String typeCode, ProductDataSheetExportContextData context) {
    Set<String> typeAttributes = context.getAttributesPerType().get(typeCode);
    if (typeAttributes == null) {
      LOG.error(format("Unable to determine attributes for type [%s]", typeCode));
      return false;
    }
    return typeAttributes.contains(attribute);
  }

  public void setFallbackHandler(CoreAttributeHandler<T> fallbackHandler) {
    this.fallbackHandler = fallbackHandler;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
