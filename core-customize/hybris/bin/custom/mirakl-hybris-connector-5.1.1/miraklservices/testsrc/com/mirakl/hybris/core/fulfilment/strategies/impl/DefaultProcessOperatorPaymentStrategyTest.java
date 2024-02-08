package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.task.RetryLaterException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessOperatorPaymentStrategyTest extends AbstractProcessOperatorPaymentStrategyTest {

  @InjectMocks
  private DefaultProcessOperatorPaymentStrategy testObj;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getDigits()).thenReturn(DIGITS);
    when(paymentTransactionEntry.getPaymentTransaction()).thenReturn(paymentTransaction);
    when(miraklCalculationService.calculateOperatorAmount(order)).thenReturn(OPERATOR_AMOUNT);
    when(takePaymentService.partialCapture(eq(order), operatorAmountArgumentCaptor.capture()))
        .thenReturn(paymentTransactionEntry);
    when(takePaymentService.fullCapture(order)).thenReturn(paymentTransactionEntry);
    when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
    when(commonI18NService.roundCurrency(anyDouble(), eq(DIGITS))).thenAnswer(new Answer<Double>() {
      @Override
      public Double answer(InvocationOnMock invocation) throws Throwable {
        return (Double) invocation.getArguments()[0];
      }
    });
    
  }

  @Test
  public void shouldDoNothingForFullMarketplaceOrders() throws RetryLaterException, Exception {
    when(order.getOperatorEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    boolean output = testObj.processPayment(order);

    assertThat(output).isTrue();
    verifyZeroInteractions(takePaymentService);
    verifyZeroInteractions(miraklCalculationService);
  }

  @Test
  public void shouldPerformPartialCaptureForMixedOrders() throws RetryLaterException, Exception {
    when(order.getOperatorEntries()).thenReturn(operatorOrderEntries);
    when(order.getMarketplaceEntries()).thenReturn(marketplaceOrderEntries);

    boolean output = testObj.processPayment(order);

    assertThat(output).isTrue();
    assertThat(operatorAmountArgumentCaptor.getValue()).isEqualTo(OPERATOR_AMOUNT);
    verify(miraklCalculationService).calculateOperatorAmount(order);
    verify(takePaymentService).partialCapture(order, OPERATOR_AMOUNT);
  }

  @Test
  public void shouldPerformFullCaptureForFullOperatorOrders() throws RetryLaterException, Exception {
    when(order.getOperatorEntries()).thenReturn(operatorOrderEntries);
    when(order.getMarketplaceEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    boolean output = testObj.processPayment(order);

    assertThat(output).isTrue();
    verifyZeroInteractions(miraklCalculationService);
    verify(takePaymentService).fullCapture(order);
    verify(order).setStatus(OrderStatus.PAYMENT_CAPTURED);
  }

  @Test
  public void shouldFailOnRejectedCaptures() throws RetryLaterException, Exception {
    when(order.getOperatorEntries()).thenReturn(operatorOrderEntries);
    when(order.getMarketplaceEntries()).thenReturn(marketplaceOrderEntries);
    when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

    boolean output = testObj.processPayment(order);

    assertThat(output).isFalse();
    verify(order).setStatus(OrderStatus.PAYMENT_NOT_CAPTURED);
  }
}
