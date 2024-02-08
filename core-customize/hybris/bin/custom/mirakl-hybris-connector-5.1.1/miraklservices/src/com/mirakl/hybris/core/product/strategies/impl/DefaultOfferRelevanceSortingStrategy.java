package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.ComparableOfferDataSortingStrategy;
import com.mirakl.hybris.core.product.strategies.OfferRelevanceSortingStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DefaultOfferRelevanceSortingStrategy implements OfferRelevanceSortingStrategy {

  protected ComparableOfferDataSortingStrategy sortingStrategy;
  protected Converter<OfferModel, ComparableOfferData<OfferModel>> comparableOfferDataConverter;


  @Override
  public List<OfferModel> sort(List<OfferModel> offers) {
    return sortingStrategy.sort(comparableOfferDataConverter.convertAll(offers));
  }

  @Required
  public void setSortingStrategy(ComparableOfferDataSortingStrategy sortingStrategy) {
    this.sortingStrategy = sortingStrategy;
  }

  @Required
  public void setOfferComparisonBeanConverter(
      Converter<OfferModel, ComparableOfferData<OfferModel>> comparableOfferDataConverter) {
    this.comparableOfferDataConverter = comparableOfferDataConverter;
  }

}
