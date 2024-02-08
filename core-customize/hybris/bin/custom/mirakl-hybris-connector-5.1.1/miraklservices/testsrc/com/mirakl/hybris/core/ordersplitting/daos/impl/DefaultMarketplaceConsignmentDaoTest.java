package com.mirakl.hybris.core.ordersplitting.daos.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceConsignmentDaoTest {

  private static final String CONSIGNMENT_CODE = "consignment-code";

  @InjectMocks
  private DefaultMarketplaceConsignmentDao miraklConsignmentDao;

  @Mock
  private FlexibleSearchService flexibleSearchService;

  @Mock
  private MarketplaceConsignmentModel consignment, secondConsignment;

  @Test
  public void shouldFindConsignmentByCode() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(consignment), 1, 0, 0));

    ConsignmentModel result = miraklConsignmentDao.findMarketplaceConsignmentByCode(CONSIGNMENT_CODE);

    assertThat(result).isSameAs(consignment);
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void shouldFindByCodeThrowAmbiguousIdentifierExceptionWhenMultipleResults() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(consignment, secondConsignment), 1, 0, 0));

    miraklConsignmentDao.findMarketplaceConsignmentByCode(CONSIGNMENT_CODE);
  }

  @Test
  public void shouldFindByCodeReturnNullWhenNoResult() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Collections.emptyList(), 0, 0, 0));

    ConsignmentModel result = miraklConsignmentDao.findMarketplaceConsignmentByCode(CONSIGNMENT_CODE);

    assertThat(result).isNull();
  }

}
