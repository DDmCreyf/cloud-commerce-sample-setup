package com.mirakl.hybris.core.payment.jobs;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderRefundsWithPagination;
import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundOrder;
import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundOrders;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.model.MiraklRetrieveRefundRequestsCronJobModel;
import com.mirakl.hybris.core.payment.services.MiraklRefundService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklRetrieveRefundRequestsJobTest {

  @InjectMocks
  private MiraklRetrieveRefundRequestsJob job;
  @Mock
  private MiraklRetrieveRefundRequestsCronJobModel cronJob;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private MiraklRefundService miraklRefundService;
  @Mock
  private Converter<MiraklRefundOrder, List<MiraklRefundRequestData>> miraklRefundRequestsDataConverter;
  @Mock
  private MiraklOrderRefundsWithPagination miraklOrderRefundsWithPagination;
  @Mock
  private MiraklRefundOrders miraklRefundOrders;
  @Mock
  private MiraklRefundOrder miraklRefundOrder1, miraklRefundOrder2;
  @Mock
  private MiraklRefundRequestData miraklRefundRequestData1, miraklRefundRequestData2;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;


  @Before
  public void setUp() {
    when(miraklApi.getOrderRefunds(any())).thenReturn(miraklOrderRefundsWithPagination);
    when(miraklOrderRefundsWithPagination.getOrders()).thenReturn(miraklRefundOrders);
    when(miraklOrderRefundsWithPagination.getTotalCount()).thenReturn(2L);
    when(miraklRefundOrders.getOrder()).thenReturn(Arrays.asList(miraklRefundOrder1, miraklRefundOrder2));
    List<List<MiraklRefundRequestData>> list = new ArrayList<>();
    list.add(Lists.newArrayList(miraklRefundRequestData1, miraklRefundRequestData2));
    when(miraklRefundRequestsDataConverter.convertAll(anyListOf(MiraklRefundOrder.class)))
        .thenReturn(list);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(anyString(), anyInt())).thenReturn(100);
  }


  @Test
  public void performRefunds() {
    PerformResult performResult = job.perform(cronJob);

    verify(miraklRefundService, times(2)).saveReceivedRefundRequest(any());

    assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(performResult.getStatus()).isEqualTo(CronJobStatus.FINISHED);
  }

}
