package com.mirakl.hybris.facades.search.solrfacetsearch.comparators.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class FacetShopComparatorTest {

  private static final String OPERATOR_CODE = "operator-code";

  @InjectMocks
  private FacetShopComparator facetShopComparator;

  @Mock
  private BaseSiteService baseSiteService;

  @Mock
  private BaseSiteModel baseSite;

  @Mock
  private FacetValue operatorFacetValue, shopFacetValue1, shopFacetValue2;

  @Before
  public void setUp() throws Exception {
    when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
    when(baseSite.getOperatorCode()).thenReturn(OPERATOR_CODE);
    when(operatorFacetValue.getName()).thenReturn(OPERATOR_CODE);
  }

  @Test
  public void shouldCompareReturnPositiveWhenOperatorFirst() {
    int compare = facetShopComparator.compare(operatorFacetValue, shopFacetValue1);

    assertThat(compare).isPositive();
  }

  @Test
  public void shouldCompareReturnNegativeWhenOperatorLast() {
    int compare = facetShopComparator.compare(shopFacetValue1, operatorFacetValue);

    assertThat(compare).isNegative();
  }

  @Test
  public void shouldCompareReturnZeroWhenNoOperator() {
    int compare = facetShopComparator.compare(shopFacetValue1, shopFacetValue2);

    assertThat(compare).isZero();
  }

}
