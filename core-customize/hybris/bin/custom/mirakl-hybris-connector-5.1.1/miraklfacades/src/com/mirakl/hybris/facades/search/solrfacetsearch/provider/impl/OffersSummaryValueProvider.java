package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

public class OffersSummaryValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

  protected OfferService offerService;
  protected SessionService sessionService;
  protected CommonI18NService commonI18NService;
  protected Converter<OfferModel, OfferOverviewData> offerOverviewConverter;
  protected Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryConverter;
  protected JsonMarshallingService jsonMarshallingService;
  protected FieldNameProvider fieldNameProvider;

  @Override
  public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
      final Object model) throws FieldValueProviderException {
    validateParameterNotNullStandardMessage("model", model);

    final List<FieldValue> fieldValues = new ArrayList<>();
    if (model instanceof ProductModel) {
      final ProductModel product = (ProductModel) model;

      for (final CurrencyModel currency : getIndexableCurrencies(indexConfig, product)) {
        List<FieldValue> fieldValuesPerCurrency = sessionService.executeInLocalView(new SessionExecutionBody() {

          @Override
          public List<FieldValue> execute() {
            commonI18NService.setCurrentCurrency(currency);
            OffersSummaryData offersSummary = getOffersSummary(product);
            if (offersSummary == null) {
              return Collections.emptyList();
            }

            return createFieldValue(offersSummary, indexedProperty, currency);
          }
        });
        fieldValues.addAll(fieldValuesPerCurrency);
      }
    }

    return fieldValues;
  }

  protected List<FieldValue> createFieldValue(final OffersSummaryData offersSummaryData, final IndexedProperty indexedProperty,
      CurrencyModel currency) {
    final List<FieldValue> fieldValues = new ArrayList<>();
    final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, currency.getIsocode());
    for (final String fieldName : fieldNames) {
      fieldValues.add(new FieldValue(fieldName, jsonMarshallingService.toJson(offersSummaryData)));
    }
    return fieldValues;
  }

  protected OffersSummaryData getOffersSummary(final ProductModel model) {
    List<OfferModel> sortedOffers = offerService.getSortedOffersForProductCode(model.getCode());
    if (CollectionUtils.isEmpty(sortedOffers)) {
      return null;
    }
    List<OfferOverviewData> offerOverviews = offerOverviewConverter.convertAllIgnoreExceptions(sortedOffers);
    return offersSummaryConverter.convert(offerOverviews);
  }

  protected Collection<CurrencyModel> getIndexableCurrencies(final IndexConfig indexConfig, ProductModel productModel) {
    if (isEmpty(indexConfig.getCurrencies())) {
      return Collections.singleton(commonI18NService.getCurrentCurrency());
    }
    return indexConfig.getCurrencies();
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setOffersSummaryConverter(Converter<List<OfferOverviewData>, OffersSummaryData> offersSummaryConverter) {
    this.offersSummaryConverter = offersSummaryConverter;
  }

  @Required
  public void setOfferOverviewConverter(Converter<OfferModel, OfferOverviewData> offerOverviewConverter) {
    this.offerOverviewConverter = offerOverviewConverter;
  }

  @Required
  public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
    this.fieldNameProvider = fieldNameProvider;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

}
