package com.mirakl.hybris.advancedpricing.product.facades.impl;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.facades.OfferPricingFacade;
import com.mirakl.hybris.advancedpricing.product.services.OfferPricingService;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultOfferPricingFacade implements OfferPricingFacade {

  protected OfferPricingService offerPricingService;
  protected Converter<OfferPricingModel, OfferPricingData> offerPricingDataConverter;

  @Override
  public List<OfferPricingData> getOfferPricingsForCriteria(OfferPricingCriteria offerPricingCriteria) {
    List<OfferPricingModel> offerPricings = offerPricingService.getOfferPricingsForCriteria(offerPricingCriteria);

    if (isNotEmpty(offerPricings)) {
      return offerPricingDataConverter.convertAll(offerPricings);
    }
    return emptyList();
  }

  @Required
  public void setOfferPricingService(OfferPricingService offerPricingService) {
    this.offerPricingService = offerPricingService;
  }

  @Required
  public void setOfferPricingDataConverter(Converter<OfferPricingModel, OfferPricingData> offerPricingDataConverter) {
    this.offerPricingDataConverter = offerPricingDataConverter;
  }

}
