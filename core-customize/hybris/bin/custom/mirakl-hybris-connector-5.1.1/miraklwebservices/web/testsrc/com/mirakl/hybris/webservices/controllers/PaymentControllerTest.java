package com.mirakl.hybris.webservices.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.webservices.dto.DebitRequestWsDTO;
import com.mirakl.hybris.webservices.dto.MiraklOrderLinePaymentWsDTO;
import com.mirakl.hybris.webservices.dto.MiraklOrderPaymentWsDTO;
import com.mirakl.hybris.webservices.dto.MiraklRefundWsDto;
import com.mirakl.hybris.webservices.dto.RefundRequestWsDTO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class PaymentControllerTest {

  private static final String REFUND_ID_1 = "refund-id-1";
  private static final String REFUND_ID_2 = "refund-id-2";
  private static final String ORDER_ID_1 = "order-id-1";
  private static final String ORDER_LINE_ID_1 = "order-line-id-1";
  private static final String COMMERCIAL_ID_1 = "commercial-id-1";
  private static final Double REFUND_AMOUNT_1 = 100d;
  private static final String ORDER_ID_2 = "order-id-2";
  private static final String ORDER_LINE_ID_2 = "order-line-id-2";
  private static final String COMMERCIAL_ID_2 = "commercial-id-2";
  private static final Double REFUND_AMOUNT_2 = 50d;

  private static final int NUMBER_OF_REQUESTS = 3;

  @InjectMocks
  private PaymentController paymentController;

  @Mock
  private EventService eventService;
  @Mock
  private DataMapper dataMapper;
  @Mock
  private DebitRequestWsDTO debitRequestWsDTO;
  @Mock
  private RefundRequestWsDTO refundRequestWsDTO;
  @Mock
  private MiraklOrderPaymentWsDTO refundOrder1, refundOrder2;
  @Mock
  private MiraklOrderLinePaymentWsDTO orderLine1, orderLine2;
  @Mock
  private MiraklRefundWsDto refund1, refund2;

  @Captor
  private ArgumentCaptor<RefundRequestReceivedEvent> refundRequestEventArgumentCaptor;

  private List<MiraklOrderLinePaymentWsDTO> orderLines1, orderLines2;
  private List<MiraklRefundWsDto> refunds1, refunds2;

  @Before
  public void setUp() {
    orderLines1 = singletonList(orderLine1);
    orderLines2 = singletonList(orderLine2);
    refunds1 = singletonList(refund1);
    refunds2 = singletonList(refund2);

    when(refundOrder1.getOrder_commercial_id()).thenReturn(COMMERCIAL_ID_1);
    when(refundOrder1.getOrder_id()).thenReturn(ORDER_ID_1);
    when(refundOrder1.getOrderLines()).thenReturn(orderLines1);
    when(orderLine1.getRefunds()).thenReturn(refunds1);
    when(orderLine1.getOrder_line_id()).thenReturn(ORDER_LINE_ID_1);
    when(refund1.getAmount()).thenReturn(REFUND_AMOUNT_1);
    when(refund1.getId()).thenReturn(REFUND_ID_1);

    when(refundOrder2.getOrder_commercial_id()).thenReturn(COMMERCIAL_ID_2);
    when(refundOrder2.getOrder_id()).thenReturn(ORDER_ID_2);
    when(refundOrder2.getOrderLines()).thenReturn(orderLines2);
    when(orderLine2.getRefunds()).thenReturn(refunds2);
    when(orderLine2.getOrder_line_id()).thenReturn(ORDER_LINE_ID_2);
    when(refund2.getAmount()).thenReturn(REFUND_AMOUNT_2);
    when(refund2.getId()).thenReturn(REFUND_ID_2);

    when(dataMapper.map(any(MiraklOrderPaymentWsDTO.class), eq(MiraklOrderPayment.class)))
        .thenReturn(mock(MiraklOrderPayment.class));
  }

  @Test
  public void shouldPublishMultipleDebitEvents() {
    when(debitRequestWsDTO.getOrders()).thenReturn(mockIncomingRequests(NUMBER_OF_REQUESTS, MiraklOrderPaymentWsDTO.class));

    paymentController.debitPayment(debitRequestWsDTO);

    verify(eventService, times(NUMBER_OF_REQUESTS)).publishEvent(any(DebitRequestReceivedEvent.class));
  }

  @Test
  public void shouldPublishRefundEvent() {
    when(refundRequestWsDTO.getOrders()).thenReturn(singletonList(refundOrder1));

    paymentController.refundPayment(refundRequestWsDTO);

    verify(eventService).publishEvent(refundRequestEventArgumentCaptor.capture());
    RefundRequestReceivedEvent refundRequestEvent = refundRequestEventArgumentCaptor.getValue();
    MiraklRefundRequestData refundRequestData = refundRequestEvent.getRefundRequest();
    assertThat(refundRequestData).isNotNull();
    assertThat(refundRequestData.getAmount()).isEqualTo(BigDecimal.valueOf(REFUND_AMOUNT_1));
    assertThat(refundRequestData.getCommercialOrderId()).isEqualTo(COMMERCIAL_ID_1);
    assertThat(refundRequestData.getMiraklOrderId()).isEqualTo(ORDER_ID_1);
    assertThat(refundRequestData.getMiraklOrderLineId()).isEqualTo(ORDER_LINE_ID_1);
    assertThat(refundRequestData.getRefundId()).isEqualTo(REFUND_ID_1);
  }

  @Test
  public void shouldPublishMultipleRefundEvents() {
    when(refundRequestWsDTO.getOrders()).thenReturn(asList(refundOrder1, refundOrder2));

    paymentController.refundPayment(refundRequestWsDTO);

    verify(eventService, times(refunds1.size() + refunds2.size())).publishEvent(any(RefundRequestReceivedEvent.class));
  }

  protected <T> List<T> mockIncomingRequests(int numberOfDebitRequests, Class<T> requestClass) {
    List<T> requests = new ArrayList<>(numberOfDebitRequests);
    for (int i = 0; i < numberOfDebitRequests; i++) {
      requests.add(mock(requestClass));
    }
    return requests;
  }


}
