package com.mirakl.hybris.b2bcore.returns.dao.impl;


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklB2BRefundEntryDaoTest {

  @Mock
  private FlexibleSearchService flexibleSearchService;

  @Mock
  private RefundEntryModel unpaidCompletedRefund;

  @InjectMocks
  DefaultMiraklB2BRefundEntryDao testObj;

  @Before
  public void setUp() throws Exception {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Collections.<Object>singletonList(unpaidCompletedRefund), 1, 0, 0));
  }

  @Test
  public void findUnpaidCompletedMarketplaceRefundEntries() throws Exception {
    List<RefundEntryModel> output = testObj.findUnpaidCompletedMarketplaceRefundEntries(false);

    assertThat(output).containsExactly(unpaidCompletedRefund);
  }

  @Test
  public void shouldReturnEmptyWhenNoResult() throws Exception {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Collections.emptyList(), 1, 0, 0));

    List<RefundEntryModel> output = testObj.findUnpaidCompletedMarketplaceRefundEntries(false);

    assertThat(output).isEmpty();
  }

}
