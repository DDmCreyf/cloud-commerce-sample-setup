package com.mirakl.hybris.facades.product.impl;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;
import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalResolutionStrategy;
import com.mirakl.hybris.facades.product.strategies.OfferDataRetrievalStrategy;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class DefaultOfferFacade implements OfferFacade {

  private static final Logger LOG = Logger.getLogger(DefaultOfferFacade.class);

  protected OfferService offerService;
  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  protected OfferDataRetrievalResolutionStrategy offerDataRetrievalResolutionStrategy;

  @Override
  public List<OfferData> getOffersForProductCode(String productCode) {
    OfferDataRetrievalStrategy offerDataRetrievalStrategy = offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy();

    return Optional.ofNullable(offerDataRetrievalStrategy)
            .map(resolvedRetrievalStrategy -> resolvedRetrievalStrategy.getOffersForProductCode(productCode)).orElse(Collections.emptyList());

  }

  @Override
  public Map<String, List<OfferData>> getOffersForProductCodes(List<String> productCodes) {
    Map<String, List<OfferData>> offersForProductCodes = new HashMap<>();

    if (!emptyIfNull(productCodes).isEmpty()) {
      OfferDataRetrievalStrategy offerDataRetrievalStrategy = offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy();
      offersForProductCodes = Optional.ofNullable(offerDataRetrievalStrategy)
              .map(resolvedRetrievalStrategy -> resolvedRetrievalStrategy.getOffersForProductCodes(productCodes)).orElse(Collections.emptyMap());
    }
    return offersForProductCodes;
  }

  @Override
  public OfferModel getOfferForCode(String offerCode) {
    validateParameterNotNullStandardMessage("code", offerCode);
    if (!offerCodeGenerationStrategy.isOfferCode(offerCode)) {
      throw new UnknownIdentifierException(format("[%s] is not an offer code", offerCode));
    }

    return offerService.getOfferForId(offerCodeGenerationStrategy.translateCodeToId(offerCode));
  }

  @Override
  public OfferModel getOfferForCodeIgnoreSearchRestrictions(String offerCode) {
    validateParameterNotNullStandardMessage("code", offerCode);
    if (!offerCodeGenerationStrategy.isOfferCode(offerCode)) {
      throw new UnknownIdentifierException(format("[%s] is not an offer code", offerCode));
    }
    String offerId = offerCodeGenerationStrategy.translateCodeToId(offerCode);

    return offerService.getOfferForIdIgnoreSearchRestrictions(offerId);
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

  @Required
  public void setOfferDataRetrievalResolutionStrategy(final OfferDataRetrievalResolutionStrategy offerDataRetrievalResolutionStrategy) {
    this.offerDataRetrievalResolutionStrategy = offerDataRetrievalResolutionStrategy;
  }

}
