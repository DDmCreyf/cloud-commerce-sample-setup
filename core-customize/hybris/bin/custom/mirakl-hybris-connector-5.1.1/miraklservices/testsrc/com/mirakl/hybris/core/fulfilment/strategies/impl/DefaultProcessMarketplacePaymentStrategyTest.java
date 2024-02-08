package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus;
import com.mirakl.client.mmp.domain.payment.MiraklPaymentStatus;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.task.RetryLaterException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessMarketplacePaymentStrategyTest extends AbstractProcessMarketplacePaymentStrategyTest {

  @InjectMocks
  DefaultProcessMarketplacePaymentStrategy testObj;

  @Before
  public void setUp() {
    super.setUp();
    
    when(miraklOrderPayment.getAmount()).thenReturn(DEBIT_AMOUNT);
    when(takePaymentService.partialCapture(order, miraklOrderPayment.getAmount().doubleValue())).thenReturn(captureTxnEntry);
    when(takePaymentService.fullCapture(order)).thenReturn(captureTxnEntry);
    when(captureTxnEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
  }

  @Test
  public void shouldConfirmPaymentFailureToMirakl() throws Exception {
    marketplaceConsignment.setPaymentStatus(MarketplaceConsignmentPaymentStatus.INITIAL);
    when(order.getTotalPrice()).thenReturn(DEBIT_AMOUNT.doubleValue());
    when(captureTxnEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

    boolean output = testObj.processPayment(marketplaceConsignment, miraklOrderPayment);

    verify(miraklApi).confirmOrderDebit(confirmRequestCaptor.capture());
    MiraklOrderPayment sentPaymentConfirmation = confirmRequestCaptor.getValue().getOrderPayments().get(0);
    verify(sentPaymentConfirmation).setPaymentStatus(MiraklPaymentStatus.REFUSED);
    assertThat(output).isFalse();
  }

  @Test
  public void shouldPerformFullCapture() throws RetryLaterException, Exception {
    marketplaceConsignment.setPaymentStatus(MarketplaceConsignmentPaymentStatus.INITIAL);
    when(order.getTotalPrice()).thenReturn(DEBIT_AMOUNT.doubleValue());

    boolean output = testObj.processPayment(marketplaceConsignment, miraklOrderPayment);

    verify(takePaymentService).fullCapture(order);
    verify(miraklApi).confirmOrderDebit(confirmRequestCaptor.capture());
    MiraklOrderPayment sentPaymentConfirmation = confirmRequestCaptor.getValue().getOrderPayments().get(0);
    verify(sentPaymentConfirmation).setPaymentStatus(MiraklPaymentStatus.OK);
    assertThat(output).isTrue();
  }

  @Test
  public void shouldPerformPartialCapture() throws RetryLaterException, Exception {
    marketplaceConsignment.setPaymentStatus(MarketplaceConsignmentPaymentStatus.INITIAL);
    when(order.getTotalPrice()).thenReturn(DEBIT_AMOUNT.doubleValue() * 2);

    boolean output = testObj.processPayment(marketplaceConsignment, miraklOrderPayment);

    verify(miraklApi).confirmOrderDebit(confirmRequestCaptor.capture());
    MiraklOrderPayment sentPaymentConfirmation = confirmRequestCaptor.getValue().getOrderPayments().get(0);
    verify(sentPaymentConfirmation).setPaymentStatus(MiraklPaymentStatus.OK);
    assertThat(output).isTrue();
  }

}
