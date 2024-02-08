package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.core.product.strategies.ComparableOfferDataSortingStrategy;
import com.mirakl.hybris.core.product.strategies.OfferOverviewRelevanceSortingStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DefaultOfferOverviewRelevanceSortingStrategy implements OfferOverviewRelevanceSortingStrategy {

  protected ComparableOfferDataSortingStrategy sortingStrategy;
  protected Converter<OfferOverviewData, ComparableOfferData<OfferOverviewData>> comparableOfferDataConverter;

  @Override
  public List<OfferOverviewData> sort(List<OfferOverviewData> offers) {
    return sortingStrategy.sort(comparableOfferDataConverter.convertAll(offers));
  }

  @Required
  public void setSortingStrategy(ComparableOfferDataSortingStrategy sortingStrategy) {
    this.sortingStrategy = sortingStrategy;
  }

  @Required
  public void setOfferComparisonBeanConverter(
      Converter<OfferOverviewData, ComparableOfferData<OfferOverviewData>> comparableOfferDataConverter) {
    this.comparableOfferDataConverter = comparableOfferDataConverter;
  }

}
