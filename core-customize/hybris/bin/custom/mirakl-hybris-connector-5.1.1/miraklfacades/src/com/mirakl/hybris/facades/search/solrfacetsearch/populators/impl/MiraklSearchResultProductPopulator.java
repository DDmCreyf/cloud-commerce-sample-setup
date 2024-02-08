package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.facades.constants.MiraklfacadesConstants.OFFERS_SUMMARY_SOLR_PROPERTY_NAME;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklSearchResultProductPopulator implements Populator<SearchResultValueData, ProductData> {

  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void populate(SearchResultValueData source, ProductData target) throws ConversionException {
    String offersSummaryJson = this.getValue(source, OFFERS_SUMMARY_SOLR_PROPERTY_NAME);
    if (isNotBlank(offersSummaryJson)) {
      OffersSummaryData offersSummary = jsonMarshallingService.fromJson(offersSummaryJson, OffersSummaryData.class);
      target.setOffersSummary(decorateOffersSummaryData(offersSummary));
    }
  }

  protected OffersSummaryData decorateOffersSummaryData(OffersSummaryData offersSummary) {
    return offersSummary;
  }

  @SuppressWarnings("unchecked")
  protected <T> T getValue(final SearchResultValueData source, final String propertyName) {
    if (source.getValues() == null) {
      return null;
    }
    return (T) source.getValues().get(propertyName);
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

}
