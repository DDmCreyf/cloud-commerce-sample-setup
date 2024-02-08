package com.mirakl.hybris.advancedpricing.search.solrfacetsearch.populators.impl;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder.offerPricingCriteria;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.facades.OfferPricingFacade;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl.MiraklOfferOverviewDataPricePopulator;

public class MiraklOfferOverviewDataAdvancedPricePopulator extends MiraklOfferOverviewDataPricePopulator {

  protected OfferPricingFacade offerPricingFacade;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  protected List<OfferPricingData> getOfferPricingsData(OfferModel source) {
    return offerPricingFacade.getOfferPricingsForCriteria(offerPricingCriteria(source.getId()).build());
  }

  @Override
  protected String getAllOfferPricingsJSON(OfferModel offer, List<OfferPricingData> offerPricingsData) {
    return jsonMarshallingService.toJson(offerPricingsData);
  }

  @Required
  public void setOfferPricingFacade(OfferPricingFacade offerPricingFacade) {
    this.offerPricingFacade = offerPricingFacade;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

}
