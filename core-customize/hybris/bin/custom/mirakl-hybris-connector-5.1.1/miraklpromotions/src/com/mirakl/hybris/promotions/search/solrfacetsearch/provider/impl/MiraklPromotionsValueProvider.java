package com.mirakl.hybris.promotions.search.solrfacetsearch.provider.impl;

import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_INDEX_PROMOTION_ID_SEPARATOR;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

public class MiraklPromotionsValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

  protected OfferService offerService;
  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
      throws FieldValueProviderException {
    validateParameterNotNullStandardMessage("model", model);

    final List<FieldValue> fieldValues = new ArrayList<>();

    if (model instanceof ProductModel) {
      ProductModel product = (ProductModel) model;
      List<OfferModel> offers = offerService.getOffersForProductCode(product.getCode());
      for (OfferModel offer : offers) {
        Set<MiraklPromotionModel> allPromotions = getAllPromotions(offer);
        addPromotionFieldValues(allPromotions, indexedProperty, fieldValues, offer.getCurrency());
      }
    }

    return fieldValues;
  }

  protected void addPromotionFieldValues(Set<MiraklPromotionModel> promotions, IndexedProperty indexedProperty,
      final List<FieldValue> fieldValues, CurrencyModel currency) {
    if (isEmpty(promotions)) {
      return;
    }
    Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode());
    for (String fieldName : fieldNames) {
      for (MiraklPromotionModel promotion : promotions) {
        fieldValues.add(new FieldValue(fieldName,
            Joiner.on(SOLR_INDEX_PROMOTION_ID_SEPARATOR).join(promotion.getShopId(), promotion.getInternalId())));
      }
    }
  }

  protected Set<MiraklPromotionModel> getAllPromotions(OfferModel offer) {
    Set<MiraklPromotionModel> allPromotions = new HashSet<>();
    if (isNotEmpty(offer.getRewardPromotions())) {
      allPromotions.addAll(offer.getRewardPromotions());
    }
    if (isNotEmpty(offer.getTriggerPromotions())) {
      allPromotions.addAll(offer.getTriggerPromotions());
    }
    return allPromotions;
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
