package com.mirakl.hybris.core.returns.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRefundLookupStrategyTest {

  @InjectMocks
  private DefaultMiraklRefundLookupStrategy lookupStrategy;

  @Mock
  private MiraklRefundEntryDao refundEntryDao;

  @Mock
  private RefundEntryModel refundEntry1, refundEntry2;

  @Test
  public void shouldGetRefundEntriesPendingPayment() {
    when(refundEntryDao.findMarketplaceRefundEntriesForStatuses(false, ReturnStatus.WAIT))
        .thenReturn(asList(refundEntry1, refundEntry2));

    List<RefundEntryModel> refundEntries = lookupStrategy.getRefundEntriesPendingPayment();

    assertThat(refundEntries).containsExactly(refundEntry1, refundEntry2);
  }

  @Test
  public void shouldGtProcessedRefundEntriesPendingConfirmation() {
    when(refundEntryDao.findPaidMarketplaceRefundEntries(false)).thenReturn(asList(refundEntry1));

    List<RefundEntryModel> refundEntries = lookupStrategy.getProcessedRefundEntriesPendingConfirmation();

    assertThat(refundEntries).containsExactly(refundEntry1);
  }

}
