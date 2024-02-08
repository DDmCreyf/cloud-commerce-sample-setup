package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.advancedpricing.product.facades.OfferPricingFacade;
import com.mirakl.hybris.advancedpricing.product.populators.OfferPricingCriteriaBuilderPopulator;
import com.mirakl.hybris.advancedpricing.product.strategies.OfferPricingSortingStrategy;
import com.mirakl.hybris.advancedpricing.product.strategies.impl.DefaultAdvancedOfferPricingSelectionStrategy;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAdvancedOfferPricingSelectionStrategyTest {

  @InjectMocks
  private DefaultAdvancedOfferPricingSelectionStrategy offerPricingSelectionStrategy;

  @Mock
  private OfferPricingFacade offerPricingFacade;
  @Mock
  private OfferPricingSortingStrategy offerPricingSortingStrategy;
  @Mock
  private OfferPricingCriteriaBuilderPopulator populator1, populator2;
  @Mock
  private OfferModel offer;
  @Mock
  private List<OfferPricingData> offerPricingsData;
  @Mock
  private OfferPricingData offerPricing1, offerPricing2, offerPricing3;
  private List<OfferPricingCriteriaBuilderPopulator> pricingCriteriaPopulators;

  @Before
  public void setUp() throws Exception {
    pricingCriteriaPopulators = asList(populator1, populator2);
    offerPricingSelectionStrategy.setPricingCriteriaPopulators(pricingCriteriaPopulators);
  }

  @Test
  public void shouldSelectApplicablePricingForOffer() {
    when(offerPricingFacade.getOfferPricingsForCriteria(any(OfferPricingCriteria.class))).thenReturn(offerPricingsData);
    when(offerPricingSortingStrategy.sort(offerPricingsData)).thenReturn(asList(offerPricing2, offerPricing1, offerPricing3));
    
    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offer);

    assertThat(offerPricing).isEqualTo(offerPricing2);
  }

  @Test
  public void shouldSelectApplicablePricingForOfferPricings() {
    when(offerPricingSortingStrategy.sort(offerPricingsData)).thenReturn(asList(offerPricing1, offerPricing2, offerPricing3));

    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offerPricingsData);

    assertThat(offerPricing).isEqualTo(offerPricing1);
  }

  @Test
  public void shouldSelectNoApplicablePricingWhenNoPrice() {
    when(offerPricingFacade.getOfferPricingsForCriteria(any(OfferPricingCriteria.class))).thenReturn(emptyList());

    OfferPricingData offerPricing = offerPricingSelectionStrategy.selectApplicableOfferPricing(offer);

    assertThat(offerPricing).isNull();
  }

}
