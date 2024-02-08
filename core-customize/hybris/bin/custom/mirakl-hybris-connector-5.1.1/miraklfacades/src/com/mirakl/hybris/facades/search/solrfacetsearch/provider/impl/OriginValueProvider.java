package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

public class OriginValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
      throws FieldValueProviderException {

    final ProductModel productModel = getProductModel(model);
    if (productModel == null) {
      return Collections.emptyList();
    }

    return populateFieldValues(indexedProperty, productModel);
  }


  protected ProductModel getProductModel(final Object model) {
    final Object finalModel = model;
    if (finalModel instanceof ProductModel) {
      return (ProductModel) finalModel;
    } else {
      return null;
    }
  }

  protected Collection<FieldValue> populateFieldValues(IndexedProperty indexedProperty, ProductModel productModel) {
    final Collection<FieldValue> fieldValues = new ArrayList<>();
    final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
    for (String fieldName : fieldNames) {
      fieldValues.add(new FieldValue(fieldName, productModel.getOrigin().getCode()));
    }
    return fieldValues;
  }

  @Required
  public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
    this.fieldNameProvider = fieldNameProvider;
  }

}
