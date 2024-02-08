package com.mirakl.hybris.advancedpricing.product.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.advancedpricing.product.services.impl.DefaultOfferPricingService;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.bootstrap.annotations.UnitTest;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferPricingServiceTest {

  @InjectMocks
  private DefaultOfferPricingService offerPricingService;

  @Mock
  private OfferPricingDao offerPricingDao;
  @Mock
  private OfferPricingCriteria searchCriteria;
  @Mock
  private List<OfferPricingModel> offerPricings;

  @Test
  public void shouldReturnOfferPricings() {
    when(offerPricingDao.findOfferPricingsByCriteria(searchCriteria)).thenReturn(offerPricings);

    List<OfferPricingModel> result = offerPricingService.getOfferPricingsForCriteria(searchCriteria);

    assertThat(result).isEqualTo(offerPricings);
  }


}
