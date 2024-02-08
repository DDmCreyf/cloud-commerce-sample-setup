package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static com.google.common.collect.FluentIterable.from;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferStateSummaryData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.facades.product.helpers.PriceDataFactoryHelper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.enumeration.EnumerationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOffersStateSummaryDataPopulatorTest {

  @InjectMocks
  private MiraklOffersStateSummaryDataPopulator populator;

  private static final String OFFER_STATE_NEW = "NEW";
  private static final String OFFER_STATE_NEW_LABEL = "Offer State New";
  private static final String OFFER_STATE_REFURBISHED = "REFURBISHED";
  private static final String OFFER_STATE_REFURBISHED_LABEL = "Offer State Refurbished";
  private static final BigDecimal PRICE_1 = BigDecimal.valueOf(200);
  private static final BigDecimal PRICE_2 = BigDecimal.valueOf(160);
  private static final BigDecimal PRICE_3 = BigDecimal.valueOf(150);

  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private PriceDataFactoryHelper priceDataFactoryHelper;
  @Mock
  private OfferState offerStateNew, offerStateRefurbished;
  private OfferOverviewData offerOverviewData1, offerOverviewData2, offerOverviewData3;
  private List<OfferOverviewData> offerOverviews;
  private List<OfferStateSummaryData> offerStateSummaries;

  @Before
  public void setUp() throws Exception {
    when(enumerationService.getEnumerationValue(OfferState.class, OFFER_STATE_NEW)).thenReturn(offerStateNew);
    when(enumerationService.getEnumerationValue(OfferState.class, OFFER_STATE_REFURBISHED)).thenReturn(offerStateRefurbished);
    when(enumerationService.getEnumerationName(offerStateNew)).thenReturn(OFFER_STATE_NEW_LABEL);
    when(enumerationService.getEnumerationName(offerStateRefurbished)).thenReturn(OFFER_STATE_REFURBISHED_LABEL);

    offerOverviewData1 = createOfferOverview(PRICE_1, OFFER_STATE_NEW);
    offerOverviewData2 = createOfferOverview(PRICE_2, OFFER_STATE_REFURBISHED);
    offerOverviewData3 = createOfferOverview(PRICE_3, OFFER_STATE_NEW);
    offerOverviews = Lists.newArrayList(offerOverviewData1, offerOverviewData2, offerOverviewData3);
    offerStateSummaries = new ArrayList<>();
  }

  @Test
  public void testPopulate() throws Exception {
    populator.populate(offerOverviews, offerStateSummaries);

    assertThat(offerStateSummaries).hasSize(2);
    
    OfferStateSummaryData summaryForStateNEW = getSummaryForState(offerStateSummaries, OFFER_STATE_NEW);
    assertThat(summaryForStateNEW.getOfferCount()).isEqualTo(2);
    assertThat(summaryForStateNEW.getMinPrice().getValue()).isEqualTo(PRICE_3);
    assertThat(summaryForStateNEW.getStateLabel()).isEqualTo(OFFER_STATE_NEW_LABEL);

    OfferStateSummaryData summaryForStateREFURBISHED = getSummaryForState(offerStateSummaries, OFFER_STATE_REFURBISHED);
    assertThat(summaryForStateREFURBISHED.getOfferCount()).isEqualTo(1);
    assertThat(summaryForStateREFURBISHED.getMinPrice().getValue()).isEqualTo(PRICE_2);
    assertThat(summaryForStateREFURBISHED.getStateLabel()).isEqualTo(OFFER_STATE_REFURBISHED_LABEL);
  }

  protected OfferOverviewData createOfferOverview(BigDecimal price, String offerStateCode) {
    OfferOverviewData offerOverviewData = new OfferOverviewData();
    offerOverviewData.setStateCode(offerStateCode);
    PriceData priceData = new PriceData();
    offerOverviewData.setPrice(priceData);
    priceData.setValue(price);
    return offerOverviewData;
  }

  protected OfferStateSummaryData getSummaryForState(final List<OfferStateSummaryData> summaries, final String offerState) {
    return from(summaries).firstMatch(new Predicate<OfferStateSummaryData>() {

      @Override
      public boolean apply(OfferStateSummaryData summary) {
        return offerState.equals(summary.getStateCode());
      }
    }).get();
  }



}
