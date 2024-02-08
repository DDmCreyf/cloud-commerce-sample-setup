package com.mirakl.hybris.facades.search.solrfacetsearch.comparators.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferStateFacetValuesComparatorTest {

  private static final String HIGH_PRIORITY_OFFERSTATE_CODE = "11";
  private static final String MEDIUM_PRIORITY_OFFERSTATE_CODE = "15";

  @Mock
  private FacetValue facetValue1;

  @Mock
  private FacetValue facetValue2;

  @InjectMocks
  private OfferStateFacetValuesComparator comparator;

  @Before
  public void setUp() throws Exception {
    comparator.prioritizedOfferStates = asList(HIGH_PRIORITY_OFFERSTATE_CODE, MEDIUM_PRIORITY_OFFERSTATE_CODE);
  }

  @Test
  public void compareReturnsPositiveWhenHighestFirst() {
    when(facetValue1.getName()).thenReturn(HIGH_PRIORITY_OFFERSTATE_CODE);
    when(facetValue2.getName()).thenReturn(MEDIUM_PRIORITY_OFFERSTATE_CODE);

    int compare = comparator.compare(facetValue1, facetValue2);

    assertThat(compare).isPositive();
  }

  @Test
  public void compareReturnsNegativeWhenHighestLast() {
    when(facetValue1.getName()).thenReturn(MEDIUM_PRIORITY_OFFERSTATE_CODE);
    when(facetValue2.getName()).thenReturn(HIGH_PRIORITY_OFFERSTATE_CODE);

    int compare = comparator.compare(facetValue1, facetValue2);

    assertThat(compare).isNegative();
  }

  @Test
  public void compareReturnsNegativeWhenNotPrioritizedFirst() {
    when(facetValue2.getName()).thenReturn(MEDIUM_PRIORITY_OFFERSTATE_CODE);

    int compare = comparator.compare(facetValue1, facetValue2);

    assertThat(compare).isNegative();
  }

  @Test
  public void compareReturnsPositiveWhenNotPrioritizedLast() {
    when(facetValue1.getName()).thenReturn(MEDIUM_PRIORITY_OFFERSTATE_CODE);

    int compare = comparator.compare(facetValue1, facetValue2);

    assertThat(compare).isPositive();
  }

  @Test
  public void compareReturnsZeroWhenNonePrioritized() {
    int compare = comparator.compare(facetValue1, facetValue2);

    assertThat(compare).isEqualTo(0);
  }

}
