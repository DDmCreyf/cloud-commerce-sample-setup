package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The default strategy retrieves data from Model (database persisted) items
 */
public class DefaultMiraklPersistentOfferDataRetrievalStrategy implements OfferDataRetrievalStrategy {

  protected Converter<OfferModel, OfferData> offerConverter;
  protected OfferService offerService;

  protected MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode;

  @Override
  public boolean isApplicable() {
    return !realtimeOfferPriceRetrievalMode.isActive();
  }

  @Override
  public List<OfferData> getOffersForProductCode(final String productCode) {
    return offerConverter.convertAll(offerService.getSortedOffersForProductCode(productCode));
  }

  @Override
  public Map<String, List<OfferData>> getOffersForProductCodes(List<String> productCodes) {
    return offerService.getSortedOffersForProductCodes(productCodes).entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> offerConverter.convertAll(entry.getValue())));
  }

  @Required
  public void setOfferConverter(final Converter<OfferModel, OfferData> offerConverter) {
    this.offerConverter = offerConverter;
  }

  @Required
  public void setOfferService(final OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setRealtimeOfferPriceRetrievalMode(final MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode) {
    this.realtimeOfferPriceRetrievalMode = realtimeOfferPriceRetrievalMode;
  }
}
