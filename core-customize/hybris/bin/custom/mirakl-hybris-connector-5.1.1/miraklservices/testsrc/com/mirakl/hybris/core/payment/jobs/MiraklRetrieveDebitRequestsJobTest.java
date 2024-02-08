package com.mirakl.hybris.core.payment.jobs;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.mirakl.client.mmp.domain.payment.debit.MiraklDebitOrder;
import com.mirakl.client.mmp.domain.payment.debit.MiraklDebitOrders;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderDebitsWithPagination;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.core.model.MiraklRetrieveDebitRequestsCronJobModel;
import com.mirakl.hybris.core.payment.services.MiraklDebitService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklRetrieveDebitRequestsJobTest {

  @InjectMocks
  private MiraklRetrieveDebitRequestsJob job;
  @Mock
  private MiraklRetrieveDebitRequestsCronJobModel cronJob;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private MiraklDebitService miraklDebitService;
  @Mock
  private Converter<MiraklDebitOrder, MiraklOrderPayment> miraklDebitsRequestDataConverter;
  @Mock
  private MiraklOrderDebitsWithPagination miraklOrderDebitsWithPagination;
  @Mock
  private MiraklDebitOrders miraklDebitOrders;
  @Mock
  private MiraklDebitOrder miraklDebitOrder1, miraklDebitOrder2;
  @Mock
  private MiraklOrderPayment miraklOrderPayment1;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;


  @Before
  public void setUp() {
    when(miraklApi.getOrderDebits(any())).thenReturn(miraklOrderDebitsWithPagination);
    when(miraklOrderDebitsWithPagination.getOrders()).thenReturn(miraklDebitOrders);
    when(miraklOrderDebitsWithPagination.getTotalCount()).thenReturn(2L);
    when(miraklDebitOrders.getOrder()).thenReturn(Arrays.asList(miraklDebitOrder1, miraklDebitOrder2));
    List<MiraklOrderPayment> list = new ArrayList<>();
    list.add(miraklOrderPayment1);
    when(miraklDebitsRequestDataConverter.convertAll(anyListOf(MiraklDebitOrder.class)))
        .thenReturn(list);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(anyString(), anyInt())).thenReturn(100);
  }


  @Test
  public void performDebits() {
    PerformResult performResult = job.perform(cronJob);

    verify(miraklDebitService, times(1)).saveReceivedDebitRequest(any());

    assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

}
