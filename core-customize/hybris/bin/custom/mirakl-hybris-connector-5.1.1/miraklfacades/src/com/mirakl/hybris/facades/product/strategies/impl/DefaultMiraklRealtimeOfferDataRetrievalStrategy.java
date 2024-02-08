package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.product.services.MiraklRealtimeOfferService;
import com.mirakl.hybris.core.product.strategies.ComparableOfferDataSortingStrategy;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This strategy gets the offer data from the mirakl API in real-time
 */
public class DefaultMiraklRealtimeOfferDataRetrievalStrategy implements OfferDataRetrievalStrategy {

  protected MiraklRealtimeOfferService realtimeOfferService;
  protected Converter<Pair<MiraklOfferOnProduct, String>, OfferData> offerConverter;
  protected ComparableOfferDataSortingStrategy offerDataSortingStrategy;
  protected Converter<OfferData, ComparableOfferData<OfferData>> offerDataComparableConverter;
  protected MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode;


  @Override
  public List<OfferData> getOffersForProductCode(String productCode) {
    Map<String, List<MiraklOfferOnProduct>> offersForProductCode =
        realtimeOfferService.getOffersForProductCodes(Collections.singletonList(productCode));
    List<OfferData> offers = new ArrayList<>(offersForProductCode.size());
    // The product code is provided to the converter in order to fill in the relevant offer field
    for (MiraklOfferOnProduct offer : CollectionUtils.emptyIfNull(offersForProductCode.get(productCode))) {
      offers.add(offerConverter.convert(Pair.of(offer, productCode)));
    }
    return offerDataSortingStrategy.sort(offerDataComparableConverter.convertAll(offers));
  }

  @Override
  public Map<String, List<OfferData>> getOffersForProductCodes(List<String> productCodes) {
    Map<String, List<MiraklOfferOnProduct>> offersForProductCode = realtimeOfferService.getOffersForProductCodes(productCodes);
    return offersForProductCode.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> offerDataSortingStrategy.sort(
        offerDataComparableConverter.convertAll(
            entry.getValue().stream().map(offer -> offerConverter.convert(Pair.of(offer, entry.getKey()))).collect(Collectors.toList())))));

  }

  @Override
  public boolean isApplicable() {
    return realtimeOfferPriceRetrievalMode.isActive();
  }

  @Required
  public void setOfferDataComparableConverter(Converter<OfferData, ComparableOfferData<OfferData>> offerDataComparableConverter) {
    this.offerDataComparableConverter = offerDataComparableConverter;
  }

  @Required
  public void setOfferDataSortingStrategy(ComparableOfferDataSortingStrategy offerDataSortingStrategy) {
    this.offerDataSortingStrategy = offerDataSortingStrategy;
  }

  @Required
  public void setOfferConverter(Converter<Pair<MiraklOfferOnProduct, String>, OfferData> offerConverter) {
    this.offerConverter = offerConverter;
  }

  @Required
  public void setRealtimeOfferService(MiraklRealtimeOfferService realtimeOfferService) {
    this.realtimeOfferService = realtimeOfferService;
  }

  @Required
  public void setRealtimeOfferPriceRetrievalMode(final MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode) {
    this.realtimeOfferPriceRetrievalMode = realtimeOfferPriceRetrievalMode;
  }

}
