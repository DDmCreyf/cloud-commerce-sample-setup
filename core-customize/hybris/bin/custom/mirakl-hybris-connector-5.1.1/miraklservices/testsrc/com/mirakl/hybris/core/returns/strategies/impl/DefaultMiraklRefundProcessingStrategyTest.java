package com.mirakl.hybris.core.returns.strategies.impl;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRefundProcessingStrategyTest {

  private static final BigDecimal REFUND_AMOUT = BigDecimal.valueOf(123.33);

  @InjectMocks
  private DefaultMiraklRefundProcessingStrategy refundProcessingStrategy;

  @Mock
  private PaymentService paymentService;
  @Mock
  private ModelService modelService;
  @Mock
  private RefundEntryModel refundEntry;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private PaymentTransactionModel paymentTransaction;
  @Mock
  private PaymentTransactionEntryModel paymentTransactionEntry;

  @Before
  public void setUp() {
    when(refundEntry.getAmount()).thenReturn(REFUND_AMOUT);
    when(refundEntry.getOrderEntry()).thenReturn(orderEntry);
    when(orderEntry.getOrder()).thenReturn(order);
    when(order.getPaymentTransactions()).thenReturn(asList(paymentTransaction));
    when(paymentService.refundFollowOn(paymentTransaction, REFUND_AMOUT)).thenReturn(paymentTransactionEntry);
  }

  @Test
  public void shouldHandleSuccessfulRefund() {
    when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

    refundProcessingStrategy.processRefund(refundEntry);

    verify(paymentService).refundFollowOn(paymentTransaction, REFUND_AMOUT);
    verify(refundEntry).setPaymentTransactionEntry(paymentTransactionEntry);
    verify(refundEntry).setStatus(ReturnStatus.COMPLETED);
    verify(modelService).save(refundEntry);
  }

  @Test
  public void shouldHandleFailedRefund() {
    when(paymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

    refundProcessingStrategy.processRefund(refundEntry);

    verify(paymentService).refundFollowOn(paymentTransaction, REFUND_AMOUT);
    verify(refundEntry).setPaymentTransactionEntry(paymentTransactionEntry);
    verify(refundEntry).setStatus(ReturnStatus.PAYMENT_REVERSAL_FAILED);
    verify(modelService).save(refundEntry);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfAlreadyProcessed() {
    when(refundEntry.getPaymentTransactionEntry()).thenReturn(mock(PaymentTransactionEntryModel.class));

    refundProcessingStrategy.processRefund(refundEntry);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenNoPaymentTransaction() {
    when(order.getPaymentTransactions()).thenReturn(Collections.<PaymentTransactionModel>emptyList());

    refundProcessingStrategy.processRefund(refundEntry);
  }

}
