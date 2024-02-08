package com.mirakl.hybris.core.product.strategies.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMcmProductUpdateStrategy extends DefaultProductUpdateStrategy {

  protected Converter<MiraklRawProductModel, ProductModel> mcmProductValuesConverter;

  @Override
  public void applyValues(ProductImportData data, ProductImportFileContextData context) throws ProductImportException {
    populateMcmSpecialAttributeValues(data, context);
    super.applyValues(data, context);
  }

  protected void populateMcmSpecialAttributeValues(ProductImportData data, ProductImportFileContextData context) {
    mcmProductValuesConverter.convert(data.getRawProduct(), data.getProductToUpdate());
    Set<ItemModel> modelsToSave = data.getModelsToSave();
    modelsToSave.add(data.getProductToUpdate());
    data.setModelsToSave(modelsToSave);
  }

  @Required
  public void setMcmProductValuesConverter(Converter<MiraklRawProductModel, ProductModel> mcmProductValuesConverter) {
    this.mcmProductValuesConverter = mcmProductValuesConverter;
  }
}
