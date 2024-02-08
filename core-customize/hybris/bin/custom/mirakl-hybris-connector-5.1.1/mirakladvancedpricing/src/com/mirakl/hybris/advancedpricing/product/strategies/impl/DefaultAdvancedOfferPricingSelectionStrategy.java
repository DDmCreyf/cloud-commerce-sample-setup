package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder.offerPricingCriteria;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.facades.OfferPricingFacade;
import com.mirakl.hybris.advancedpricing.product.populators.OfferPricingCriteriaBuilderPopulator;
import com.mirakl.hybris.advancedpricing.product.strategies.OfferPricingSortingStrategy;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;

public class DefaultAdvancedOfferPricingSelectionStrategy implements OfferPricingSelectionStrategy {

  protected OfferPricingFacade offerPricingFacade;
  protected OfferPricingSortingStrategy offerPricingSortingStrategy;
  protected List<OfferPricingCriteriaBuilderPopulator> pricingCriteriaPopulators;

  @Override
  public OfferPricingData selectApplicableOfferPricing(OfferModel offer) {
    OfferPricingCriteriaBuilder offerPricingCriteriaBuilder = offerPricingCriteria(offer.getId());
    for (OfferPricingCriteriaBuilderPopulator populator : pricingCriteriaPopulators) {
      populator.populate(offerPricingCriteriaBuilder);
    }
    List<OfferPricingData> offerPricings = offerPricingFacade.getOfferPricingsForCriteria(offerPricingCriteriaBuilder.build());

    if (isNotEmpty(offerPricings)) {
      return selectApplicableOfferPricing(offerPricings);
    }
    return null;
  }

  @Override
  public OfferPricingData selectApplicableOfferPricing(List<OfferPricingData> offerPricings) {
    List<OfferPricingData> sortedPricings = offerPricingSortingStrategy.sort(offerPricings);
    return isNotEmpty(sortedPricings) ? sortedPricings.get(0) : null;
  }

  @Required
  public void setOfferPricingFacade(OfferPricingFacade offerPricingFacade) {
    this.offerPricingFacade = offerPricingFacade;
  }

  @Required
  public void setOfferPricingSortingStrategy(OfferPricingSortingStrategy offerPricingSortingStrategy) {
    this.offerPricingSortingStrategy = offerPricingSortingStrategy;
  }

  @Required
  public void setPricingCriteriaPopulators(List<OfferPricingCriteriaBuilderPopulator> pricingCriteriaPopulators) {
    this.pricingCriteriaPopulators = pricingCriteriaPopulators;
  }

}
