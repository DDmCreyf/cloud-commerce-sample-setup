package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static com.google.common.base.Preconditions.checkState;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.OFFER_NEW_STATE_CODE_KEY;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.services.MiraklProductService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.store.BaseStoreModel;

public class OfferStateValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

  protected MiraklProductService miraklProductService;
  protected ConfigurationService configurationService;
  protected OfferDao offerDao;
  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
      throws FieldValueProviderException {
    validateParameterNotNullStandardMessage("model", model);

    final List<FieldValue> fieldValues = new ArrayList<>();

    if (model instanceof ProductModel) {
      ProductModel product = (ProductModel) model;
      List<Pair<OfferState, CurrencyModel>> offerStatesAndCurrency =
          offerDao.findOfferStatesAndCurrencyForProductCode(product.getCode());

      for (Pair<OfferState, CurrencyModel> offerStateAndCurrency : offerStatesAndCurrency) {
        OfferState offerState = offerStateAndCurrency.getLeft();
        CurrencyModel offerCurrency = offerStateAndCurrency.getRight();
        Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, offerCurrency.getIsocode());
        for (String fieldName : fieldNames) {
          fieldValues.add(new FieldValue(fieldName, offerState.getCode()));
        }
      }

      if(miraklProductService.isSellableByOperator(product)) {
        addOperatorProductStates(indexConfig, indexedProperty, fieldValues);
      }
    }

    return fieldValues;
  }

  protected void addOperatorProductStates(IndexConfig indexConfig, IndexedProperty indexedProperty, List<FieldValue> fieldValues) {
    String newOfferStateCode = getNewOfferState().getCode();
    BaseSiteModel baseSite = indexConfig.getBaseSite();
    if (baseSite != null && isNotEmpty(baseSite.getStores())) {
      BaseStoreModel baseStore = baseSite.getStores().get(0);
      Set<CurrencyModel> currencies = baseStore.getCurrencies();
      for (CurrencyModel currency : currencies) {
        Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode());
        for (String fieldName : fieldNames) {
          fieldValues.add(new FieldValue(fieldName, newOfferStateCode));
        }
      }
    }
  }

  protected OfferState getNewOfferState() {
    String newOfferStateCode = configurationService.getConfiguration().getString(OFFER_NEW_STATE_CODE_KEY);
    checkState(isNotBlank(newOfferStateCode), "No NEW offer state code configured");

    return OfferState.valueOf(newOfferStateCode);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }

  @Required
  public void setOfferDao(OfferDao offerDao) {
    this.offerDao = offerDao;
  }

  @Required
  public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
    this.fieldNameProvider = fieldNameProvider;
  }
}
