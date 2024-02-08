package com.mirakl.hybris.core.catalog.populators.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.AttributeValueData;
import com.mirakl.hybris.beans.HeaderInfoData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

public class AttributeValueDataPopulator
    implements Populator<Pair<Entry<String, String>, ProductImportFileContextData>, AttributeValueData> {

  protected ModelService modelService;

  @Override
  public void populate(Pair<Entry<String, String>, ProductImportFileContextData> source, AttributeValueData target)
      throws ConversionException {
    Entry<String, String> entry = source.getLeft();
    ProductImportFileContextData context = source.getRight();

    HeaderInfoData headerInfo = context.getHeaderInfos().get(entry.getKey());
    String attribute = headerInfo.getAttribute();
    target.setCode(attribute);
    target.setLocale(headerInfo.getLocale());
    target.setValue(entry.getValue());

    Map<String, PK> coreAttributes = context.getGlobalContext().getCoreAttributes();
    if (coreAttributes.containsKey(attribute)) {
      target.setCoreAttribute((MiraklCoreAttributeModel) modelService.get(coreAttributes.get(attribute)));
    }
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
