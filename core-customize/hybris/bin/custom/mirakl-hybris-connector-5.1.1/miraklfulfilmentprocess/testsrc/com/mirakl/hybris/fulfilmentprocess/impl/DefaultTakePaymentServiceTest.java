package com.mirakl.hybris.fulfilmentprocess.impl;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.order.services.impl.DefaultTakePaymentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultTakePaymentServiceTest {

  private static final double CAPTURE_AMOUNT = 100.75;

  @InjectMocks
  private DefaultTakePaymentService takePaymentService;

  @Mock
  private PaymentService paymentService;

  @Mock
  private OrderModel order;

  @Mock
  private PaymentTransactionModel txn;

  @Mock
  private PaymentTransactionEntryModel captureTxnEntry, authorizationTxnEntry;

  @Captor
  private ArgumentCaptor<BigDecimal> operatorAmountArgumentCaptor;

  @Before
  public void setUp() throws Exception {
    when(paymentService.capture(txn)).thenReturn(captureTxnEntry);
    when(paymentService.partialCapture(eq(txn), operatorAmountArgumentCaptor.capture())).thenReturn(captureTxnEntry);
    when(order.getPaymentTransactions()).thenReturn(singletonList(txn));
  }

  @Test
  public void shouldFullCapture() {
    when(captureTxnEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
    PaymentTransactionEntryModel result = takePaymentService.fullCapture(order);
    assertThat(result).isEqualTo(captureTxnEntry);
  }

  @Test
  public void shouldPartialCapture() {
    PaymentTransactionEntryModel result = takePaymentService.partialCapture(order, CAPTURE_AMOUNT);

    assertThat(result).isEqualTo(captureTxnEntry);
    assertThat(operatorAmountArgumentCaptor.getValue().doubleValue()).isEqualTo(CAPTURE_AMOUNT);
  }

  @Test
  public void shouldGetPaymentTransactionForCapture() {
    PaymentTransactionModel result = takePaymentService.getPaymentTransactionToUseForCapture(order, CAPTURE_AMOUNT);

    assertThat(result).isEqualTo(txn);
  }
}
