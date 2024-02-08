package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

public class HasMiraklOfferValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable {

  protected OfferService offerService;
  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
      throws FieldValueProviderException {

    final ProductModel productModel = getProductModel(model);
    if (productModel == null) {
      return Collections.emptyList();
    }

    boolean hasOffers = offerService.hasOffers(productModel.getCode());
    final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
    final Collection<FieldValue> fieldValues = new ArrayList<>();
    for (String fieldName : fieldNames) {
      fieldValues.add(new FieldValue(fieldName, hasOffers));
    }
    return fieldValues;
  }

  protected ProductModel getProductModel(final Object model) {
    final Object finalModel = model;
    if (finalModel instanceof ProductModel) {
      return (ProductModel) finalModel;
    } else {
      return null;
    }
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }


  @Required
  public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
    this.fieldNameProvider = fieldNameProvider;
  }

}
