package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferStateSummaryData;
import com.mirakl.hybris.core.enums.OfferState;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklOffersStateSummaryDataPopulator implements Populator<List<OfferOverviewData>, List<OfferStateSummaryData>> {

  protected EnumerationService enumerationService;

  @Override
  public void populate(List<OfferOverviewData> source, List<OfferStateSummaryData> target) throws ConversionException {
    Map<String, OfferStateSummaryData> summaries = new HashMap<>();

    for (OfferOverviewData offer : source) {
      if (!summaries.containsKey(offer.getStateCode())) {
        addOfferStateSummary(offer, summaries);
      } else {
        updateOfferStateSummary(offer, summaries);
      }
    }
    target.clear();
    target.addAll(summaries.values());
  }

  protected void addOfferStateSummary(OfferOverviewData offer, Map<String, OfferStateSummaryData> summaries) {
    OfferStateSummaryData summary = new OfferStateSummaryData();
    OfferState offerState = enumerationService.getEnumerationValue(OfferState.class, offer.getStateCode());
    summary.setStateLabel(enumerationService.getEnumerationName(offerState));
    summary.setStateCode(offer.getStateCode());
    summary.setOfferCount(1);
    summary.setMinPrice(offer.getPrice());
    summaries.put(offer.getStateCode(), summary);
  }

  protected void updateOfferStateSummary(OfferOverviewData offer, Map<String, OfferStateSummaryData> summaries) {
    OfferStateSummaryData summary = summaries.get(offer.getStateCode());
    summary.setOfferCount(summary.getOfferCount() + 1);
    if (offer.getPrice().getValue().doubleValue() < summary.getMinPrice().getValue().doubleValue()) {
      summary.setMinPrice((offer.getPrice()));
    }
    summaries.put(offer.getStateCode(), summary);
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }
}
