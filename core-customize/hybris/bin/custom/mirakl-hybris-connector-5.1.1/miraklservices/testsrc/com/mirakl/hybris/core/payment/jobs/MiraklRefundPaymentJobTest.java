package com.mirakl.hybris.core.payment.jobs;

import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderLineRefund;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.payment.debit.MiraklConfirmOrderRefundRequest;
import com.mirakl.hybris.core.model.MiraklRefundPaymentCronJobModel;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundLookupStrategy;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundProcessingStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.fest.assertions.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(Parameterized.class)
public class MiraklRefundPaymentJobTest {

  //Parameter fields
  int configuredBatchSize;
  int nbPendingRefunds;
  int expectedNumberOfBatches;
  int sizeOfAllButLastBatches;
  int sizeOfLastBatch;

  @InjectMocks
  private MiraklRefundPaymentJob job;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklRefundLookupStrategy miraklRefundLookupStrategy;
  @Mock
  private MiraklRefundProcessingStrategy miraklRefundProcessingStrategy;
  @Mock
  private MiraklRefundPaymentCronJobModel cronJob;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;

  @Mock
  private PaymentTransactionEntryModel paymentTransactionEntry;
  @Mock
  private CurrencyModel currency;
  @Mock
  private Converter<RefundEntryModel, MiraklOrderLineRefund> miraklOrderLineRefundConverter;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Captor
  private ArgumentCaptor<List<RefundEntryModel>> savedRefundsCaptor;

  public MiraklRefundPaymentJobTest(final int configuredBatchSize, final int nbPendingRefunds, final int expectedNumberOfBatches,
      final int sizeOfAllButLastBatches, final int sizeOfLastBatch) {
    this.configuredBatchSize = configuredBatchSize;
    this.nbPendingRefunds = nbPendingRefunds;
    this.expectedNumberOfBatches = expectedNumberOfBatches;
    this.sizeOfAllButLastBatches = sizeOfAllButLastBatches;
    this.sizeOfLastBatch = sizeOfLastBatch;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {{-1, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {1, 0, 0, 0, 0}, {2, 0, 0, 0, 0}, {3, 0, 0, 0, 0}, {-1, 1, 1, 0, 1}, {0, 1, 1, 0, 1},
            {1, 1, 1, 0, 1}, {2, 1, 1, 0, 1}, {2, 1, 1, 0, 1}, {-1, 2, 2, 1, 1}, {0, 2, 2, 1, 1}, {1, 2, 2, 1, 1}, {2, 2, 1, 0, 2}, {3, 2, 1, 0, 2},
            {-1, 3, 3, 1, 1}, {0, 3, 3, 1, 1}, {1, 3, 3, 1, 1}, {2, 3, 2, 2, 1}, {3, 3, 1, 0, 3},});
  }


  @Before
  public void setUp()
  {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void performRefunds() {
    when(configuration.getInt(org.mockito.Matchers.anyString(), Mockito.anyInt())).thenReturn(configuredBatchSize);

    List<RefundEntryModel> pendingRefunds = new ArrayList<>();
    for (int i = 0; i < nbPendingRefunds; i++) {
      pendingRefunds.add(Mockito.mock(RefundEntryModel.class));
    }

    when(miraklRefundLookupStrategy.getRefundEntriesPendingPayment()).thenReturn(pendingRefunds);
    when(miraklRefundLookupStrategy.getProcessedRefundEntriesPendingConfirmation()).thenReturn(pendingRefunds);
    when(configurationService.getConfiguration()).thenReturn(configuration);

    PerformResult performResult = job.perform(cronJob);

    assertThat(performResult.getResult(), equalTo(CronJobResult.SUCCESS));
    assertThat(performResult.getStatus(), equalTo(CronJobStatus.FINISHED));

    if (pendingRefunds.isEmpty()) {
      assertNoActionPerformed(pendingRefunds);
    } else {
      verify(miraklRefundProcessingStrategy, times(nbPendingRefunds)).processRefund(Mockito.any(RefundEntryModel.class));

      verify(miraklApi, times(expectedNumberOfBatches)).confirmOrderRefund(org.mockito.Matchers.any(MiraklConfirmOrderRefundRequest.class));
      for (RefundEntryModel refundWaitingConfirmation : pendingRefunds) {
        verify(refundWaitingConfirmation).setConfirmedToMirakl(true);
      }

      verify(modelService, times(expectedNumberOfBatches)).saveAll(savedRefundsCaptor.capture());
      List<List<RefundEntryModel>> actualBatches = savedRefundsCaptor.getAllValues();
      assertThat(actualBatches, allOf(Matchers.notNullValue(), hasSize(expectedNumberOfBatches)));

      // Check that each batch contains the expected number of items
      int nbBatches = actualBatches.size();
      Assertions.assertThat(nbBatches).isGreaterThanOrEqualTo(1);
      final List<List<RefundEntryModel>> allButLastActualBatches = actualBatches.subList(0, nbBatches - 1);
      CollectionUtils.emptyIfNull(allButLastActualBatches)
          .forEach(actualBatch -> Assertions.assertThat(actualBatch).hasSize(sizeOfAllButLastBatches));
      Assertions.assertThat(actualBatches.get(nbBatches - 1)).hasSize(sizeOfLastBatch);
    }
  }

  private void assertNoActionPerformed(final List<RefundEntryModel> pendingRefunds) {
    verify(miraklRefundProcessingStrategy, never()).processRefund(Mockito.any(RefundEntryModel.class));
    verify(miraklApi, never()).confirmOrderRefund(org.mockito.Matchers.any(MiraklConfirmOrderRefundRequest.class));
    verify(modelService, never()).saveAll(Mockito.anyList());


  }

}
