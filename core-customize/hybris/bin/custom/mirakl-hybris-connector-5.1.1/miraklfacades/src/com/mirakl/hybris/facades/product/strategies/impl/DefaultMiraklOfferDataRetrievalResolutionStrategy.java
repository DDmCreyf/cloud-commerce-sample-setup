package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalResolutionStrategy;
import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


public class DefaultMiraklOfferDataRetrievalResolutionStrategy implements OfferDataRetrievalResolutionStrategy {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklOfferDataRetrievalResolutionStrategy.class);
  protected List<OfferDataRetrievalStrategy> offerDataRetrievalStrategies;

  @Override
  public OfferDataRetrievalStrategy resolveOfferDataRetrievalStrategy() {
    OfferDataRetrievalStrategy firstApplicableRetrievalStrategy =
        CollectionUtils.emptyIfNull(offerDataRetrievalStrategies).stream().filter(OfferDataRetrievalStrategy::isApplicable).findFirst().orElse(null);

    if (firstApplicableRetrievalStrategy == null) {
      LOG.warn(String.format("None of the [%s] existing offer data retrieval strategies were found to be applicable",
          CollectionUtils.emptyIfNull(offerDataRetrievalStrategies).size()));
    }
    return firstApplicableRetrievalStrategy;
  }


  @Required
  public void setOfferDataRetrievalStrategies(final List<OfferDataRetrievalStrategy> offerDataRetrievalStrategies) {
    this.offerDataRetrievalStrategies = offerDataRetrievalStrategies;
  }
}
