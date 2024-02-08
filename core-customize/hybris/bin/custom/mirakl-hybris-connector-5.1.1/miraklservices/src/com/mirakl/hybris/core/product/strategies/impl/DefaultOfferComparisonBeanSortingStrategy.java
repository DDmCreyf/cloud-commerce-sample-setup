package com.mirakl.hybris.core.product.strategies.impl;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.comparators.OfferPriceComparator;
import com.mirakl.hybris.core.comparators.OfferStateComparator;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.product.strategies.ComparableOfferDataSortingStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.OFFER_NEW_STATE_CODE_KEY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DefaultOfferComparisonBeanSortingStrategy implements ComparableOfferDataSortingStrategy {

  protected ConfigurationService configurationService;


  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> sort(List<ComparableOfferData<T>> wrappedOffers) {
    ComparatorChain comparatorChain = new ComparatorChain();
    comparatorChain.addComparator(new OfferStateComparator<T>(getNewOfferState()));
    comparatorChain.addComparator(new OfferPriceComparator<T>());
    Collections.sort(wrappedOffers, comparatorChain);
    return extractWrappedOffers(wrappedOffers);
  }

  protected <T> ImmutableList<T> extractWrappedOffers(List<ComparableOfferData<T>> offers) {
    return FluentIterable.from(offers).transform(new Function<ComparableOfferData<T>, T>() {

      @Override
      public T apply(ComparableOfferData<T> comparableOfferData) {
        return comparableOfferData.getOffer();
      }

    }).toList();
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

}
