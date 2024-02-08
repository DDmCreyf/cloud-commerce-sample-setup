package com.mirakl.hybris.b2bcore.returns.strategies.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.b2bcore.returns.dao.MiraklB2BRefundEntryDao;
import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.returns.model.RefundEntryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklB2BRefundLookupStrategyTest {

  @Mock
  private MiraklRefundEntryDao miraklRefundEntryDao;
  @Mock
  private MiraklB2BRefundEntryDao miraklB2BRefundEntryDao;
  @Mock
  private RefundEntryModel paidRefund1, paidRefund2, unpaidCompletedRefund1;

  @InjectMocks
  private DefaultMiraklB2BRefundLookupStrategy testObj;

  @Before
  public void setUp() {
    when(miraklRefundEntryDao.findPaidMarketplaceRefundEntries(false)).thenReturn(asList(paidRefund1, paidRefund2));
    when(miraklB2BRefundEntryDao.findUnpaidCompletedMarketplaceRefundEntries(false))
        .thenReturn(singletonList(unpaidCompletedRefund1));
  }

  @Test
  public void getProcessedRefundEntriesPendingConfirmation() throws Exception {
      List<RefundEntryModel> output = testObj.getProcessedRefundEntriesPendingConfirmation();

      assertThat(output).containsExactly(paidRefund1, paidRefund2, unpaidCompletedRefund1);
  }

}
