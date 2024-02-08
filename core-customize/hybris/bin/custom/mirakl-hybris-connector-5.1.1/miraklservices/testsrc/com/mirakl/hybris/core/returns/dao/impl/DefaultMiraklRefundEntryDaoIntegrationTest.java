package com.mirakl.hybris.core.returns.dao.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMiraklRefundEntryDaoIntegrationTest extends ServicelayerTest {

  @Resource
  private DefaultMiraklRefundEntryDao defaultMiraklRefundEntryDao;

  @Before
  public void setUp() throws Exception {
    createCoreData();
    createDefaultUsers();
    createHardwareCatalog();
    importCsv("/miraklservices/test/testRefundEntries.impex", "utf-8");
  }

  @Test
  public void shouldFindWaitingRefundEntries() {
    List<RefundEntryModel> refundEntries =
        defaultMiraklRefundEntryDao.findMarketplaceRefundEntriesForStatuses(false, ReturnStatus.WAIT);

    assertThat(refundEntries).hasSize(2);
  }

  @Test
  public void shouldFindRefundEntriesForMultipleStatuses() {
    List<RefundEntryModel> refundEntries = defaultMiraklRefundEntryDao.findMarketplaceRefundEntriesForStatuses(false,
        ReturnStatus.WAIT, ReturnStatus.PAYMENT_REVERSED);

    assertThat(refundEntries).hasSize(3);
  }

  @Test
  public void shouldFindNotConfirmedPaidMarketplaceRefundEntries() {
    List<RefundEntryModel> refundEntries = defaultMiraklRefundEntryDao.findPaidMarketplaceRefundEntries(false);

    assertThat(refundEntries).hasSize(1);
  }

  @Test
  public void shouldFindConfirmedPaidMarketplaceRefundEntries() {
    List<RefundEntryModel> refundEntries = defaultMiraklRefundEntryDao.findPaidMarketplaceRefundEntries(true);

    assertThat(refundEntries).hasSize(2);
  }

}
