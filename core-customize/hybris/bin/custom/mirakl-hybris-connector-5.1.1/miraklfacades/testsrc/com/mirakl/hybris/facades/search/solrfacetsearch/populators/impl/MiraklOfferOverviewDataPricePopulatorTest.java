package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.facades.product.helpers.PriceDataFactoryHelper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOfferOverviewDataPricePopulatorTest {

  private static final BigDecimal PRICE = BigDecimal.valueOf(100);
  private static final BigDecimal ORIGIN_PRICE = BigDecimal.valueOf(150);
  private static final BigDecimal MIN_SHIPPING_PRICE = BigDecimal.valueOf(7.5);
  private static final BigDecimal TOTAL_PRICE = PRICE.add(MIN_SHIPPING_PRICE);

  @InjectMocks
  private MiraklOfferOverviewDataPricePopulator populator;

  @Mock
  private OfferModel offer;
  @Mock
  private PriceDataFactoryHelper priceDataFactoryHelper;
  @Mock
  private OfferService offerService;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private OfferPricingSelectionStrategy offerPricingSelectionStrategy;
  @Mock
  private PriceData priceData, totalPriceData, originPriceData, minShippingPriceData;
  @Mock
  private OfferPricingData offerPricing1, offerPricing2, offerPricing3;
  private List<OfferPricingData> offerPricings;

  @Before
  public void setUp() throws Exception {
    offerPricings = asList(offerPricing1, offerPricing2, offerPricing3);
    when(offerService.loadAllOfferPricings(offer)).thenReturn(offerPricings);
    when(offerPricingSelectionStrategy.selectApplicableOfferPricing(offerPricings)).thenReturn(offerPricing2);
    when(offerPricing2.getPrice()).thenReturn(PRICE);
    when(offerPricing2.getUnitOriginPrice()).thenReturn(ORIGIN_PRICE);
    when(offer.getMinShippingPrice()).thenReturn(MIN_SHIPPING_PRICE);

    when(priceDataFactoryHelper.createPrice(PRICE)).thenReturn(priceData);
    when(priceDataFactoryHelper.createPrice(TOTAL_PRICE)).thenReturn(totalPriceData);
    when(priceDataFactoryHelper.createPrice(ORIGIN_PRICE)).thenReturn(originPriceData);
    when(priceDataFactoryHelper.createPrice(MIN_SHIPPING_PRICE)).thenReturn(minShippingPriceData);
  }

  @Test
  public void shouldPopulatePrices() {
    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getPrice()).isEqualTo(priceData);
    assertThat(result.getOriginPrice()).isEqualTo(originPriceData);
    assertThat(result.getTotalPrice()).isEqualTo(totalPriceData);
    assertThat(result.getMinShippingPrice()).isEqualTo(minShippingPriceData);
  }


}
