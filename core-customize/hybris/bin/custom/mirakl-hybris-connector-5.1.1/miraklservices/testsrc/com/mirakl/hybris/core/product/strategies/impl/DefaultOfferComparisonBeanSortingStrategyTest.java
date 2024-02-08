package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.OFFER_NEW_STATE_CODE_KEY;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.servicelayer.config.ConfigurationService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferComparisonBeanSortingStrategyTest {
  private static final int LOWEST_PRICE = 11;
  private static final int HIGHEST_PRICE = 18;
  private static final int ALMOST_LOWEST_PRICE = 16;
  private static final int ALMOST_HIGHEST_PRICE = 17;

  private static final String OFFER_STATE_NEW_CODE = "offerStateNewCode";
  private static final OfferState NEW_OFFER_STATE = OfferState.valueOf(OFFER_STATE_NEW_CODE);
  private static final OfferState NOT_NEW_OFFER_STATE = OfferState.valueOf("lower_priority_than_new");

  @InjectMocks
  private DefaultOfferComparisonBeanSortingStrategy sortingStrategy;

  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configurationMock;
  @Mock
  private OfferModel offer1, offer2, offer3, offer4;

  private ComparableOfferData<OfferModel> comparableOfferData1, comparableOfferData2, comparableOfferData3, comparableOfferData4;

  @Before
  public void setup() {
    when(configurationService.getConfiguration()).thenReturn(configurationMock);
    when(configurationMock.getString(OFFER_NEW_STATE_CODE_KEY)).thenReturn(OFFER_STATE_NEW_CODE);

    comparableOfferData1 = createComparableOfferData(offer1, NEW_OFFER_STATE, BigDecimal.valueOf(ALMOST_HIGHEST_PRICE));
    comparableOfferData2 = createComparableOfferData(offer2, NOT_NEW_OFFER_STATE, BigDecimal.valueOf(LOWEST_PRICE));
    comparableOfferData3 = createComparableOfferData(offer3, NOT_NEW_OFFER_STATE, BigDecimal.valueOf(HIGHEST_PRICE));
    comparableOfferData4 = createComparableOfferData(offer4, NEW_OFFER_STATE, BigDecimal.valueOf(ALMOST_LOWEST_PRICE));
  }

  protected ComparableOfferData<OfferModel> createComparableOfferData(OfferModel offer, OfferState offerState, BigDecimal price) {
    ComparableOfferData<OfferModel> comparableOfferData = new ComparableOfferData<>();
    comparableOfferData.setOffer(offer);
    comparableOfferData.setState(offerState);
    comparableOfferData.setTotalPrice(price);
    return comparableOfferData;
  }

  @Test
  public void sorting() {
    List<ComparableOfferData<OfferModel>> toBeSorted =
        newArrayList(comparableOfferData1, comparableOfferData2, comparableOfferData3, comparableOfferData4);

    List<OfferModel> result = sortingStrategy.sort(toBeSorted);

    assertThat(result, IsIterableContainingInOrder.contains(offer4, offer1, offer2, offer3));
  }

  @Test(expected = IllegalStateException.class)
  public void sortThrowsIllegalStateExceptionIfNoNewOfferStateCodeIsConfigured() {
    when(configurationMock.getString(OFFER_NEW_STATE_CODE_KEY)).thenReturn(EMPTY);

    sortingStrategy.sort(newArrayList(comparableOfferData1, comparableOfferData2, comparableOfferData3, comparableOfferData4));
  }


}
