package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;
import com.mirakl.hybris.core.shop.daos.ShopDao;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.store.BaseStoreModel;

public class ShopIdValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

  protected MiraklProductService miraklProductService;
  protected ShopDao shopDao;
  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
      throws FieldValueProviderException {

    validateParameterNotNullStandardMessage("model", model);

    final List<FieldValue> fieldValues = new ArrayList<>();

    if (model instanceof ProductModel) {
      ProductModel product = (ProductModel) model;
      Collection<ShopModel> shops = shopDao.findShopsForProductCode(product.getCode());
      for (ShopModel shop : shops) {
        Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, shop.getCurrency().getIsocode());
        for (String fieldName : fieldNames) {
          fieldValues.add(new FieldValue(fieldName, shop.getId()));
        }
      }

      if (miraklProductService.isSellableByOperator(product)) {
        addOperatorToShops(indexConfig, indexedProperty, fieldValues);
      }
    }

    return fieldValues;
  }

  protected void addOperatorToShops(IndexConfig indexConfig, IndexedProperty indexedProperty,
      final List<FieldValue> fieldValues) {

    BaseSiteModel baseSite = indexConfig.getBaseSite();
    if (baseSite != null && isNotEmpty(baseSite.getStores())) {
      BaseStoreModel baseStore = baseSite.getStores().get(0);
      Set<CurrencyModel> currencies = baseStore.getCurrencies();
      for (CurrencyModel currency : currencies) {
        Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode());
        for (String fieldName : fieldNames) {
          fieldValues.add(new FieldValue(fieldName, baseSite.getOperatorCode()));
        }
      }
    }
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setShopDao(ShopDao shopDao) {
    this.shopDao = shopDao;
  }

  @Required
  public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
    this.fieldNameProvider = fieldNameProvider;
  }
}
