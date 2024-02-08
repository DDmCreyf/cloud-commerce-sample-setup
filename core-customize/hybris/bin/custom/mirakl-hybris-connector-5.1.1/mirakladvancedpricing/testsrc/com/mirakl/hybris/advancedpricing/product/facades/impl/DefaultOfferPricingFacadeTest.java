package com.mirakl.hybris.advancedpricing.product.facades.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.advancedpricing.product.facades.impl.DefaultOfferPricingFacade;
import com.mirakl.hybris.advancedpricing.product.services.OfferPricingService;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferPricingFacadeTest {

  @InjectMocks
  private DefaultOfferPricingFacade offerPricingFacade;

  @Mock
  private OfferPricingService offerPricingService;
  @Mock
  private Converter<OfferPricingModel, OfferPricingData> offerPricingDataConverter;
  @Mock
  private OfferPricingCriteria criteria;
  @Mock
  private OfferPricingModel offerPricingModel1, offerPricingModel2;
  @Mock
  private OfferPricingData offerPricingData1, offerPricingData2;
  private List<OfferPricingModel> models;
  private List<OfferPricingData> data;

  @Before
  public void setUp() throws Exception {
    models = asList(offerPricingModel1, offerPricingModel2);
    data = asList(offerPricingData1, offerPricingData1);
  }

  @Test
  public void shouldGetOfferPricingsForCriteria() {
    when(offerPricingService.getOfferPricingsForCriteria(criteria)).thenReturn(models);
    when(offerPricingDataConverter.convertAll(models)).thenReturn(data);

    List<OfferPricingData> offerPricings = offerPricingFacade.getOfferPricingsForCriteria(criteria);

    assertThat(offerPricings).isEqualTo(data);
  }

  @Test
  public void shouldGetOfferPricingsForCriteriaWhenNoResult() {
    when(offerPricingService.getOfferPricingsForCriteria(criteria)).thenReturn(emptyList());

    List<OfferPricingData> offerPricings = offerPricingFacade.getOfferPricingsForCriteria(criteria);

    assertThat(offerPricings).isEqualTo(emptyList());
    verifyZeroInteractions(offerPricingDataConverter);
  }

}
