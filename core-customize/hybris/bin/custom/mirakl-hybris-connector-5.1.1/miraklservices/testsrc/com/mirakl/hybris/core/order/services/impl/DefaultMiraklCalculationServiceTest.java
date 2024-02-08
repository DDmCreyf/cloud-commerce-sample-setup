package com.mirakl.hybris.core.order.services.impl;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMiraklCalculationServiceTest {

  private static final double TOTAL_PRICE = 155.75;
  private static final double PRICE_CONSIGNMENT2 = 15.75;
  private static final double PRICE_CONSIGNMENT1 = 30.5;
  private static final int DIGITS = 2;
  private static final BigDecimal CAPTURED_AMOUNT = BigDecimal.valueOf(30d);

  @InjectMocks
  private DefaultMiraklCalculationService miraklCalculationService;

  @Mock
  private CommonI18NService commonI18NService;

  @Mock
  private OrderModel order;

  @Mock
  private CurrencyModel currency;

  @Mock
  private AbstractOrderEntryModel orderEntry1, orderEntry2, orderEntry3;

  @Mock
  private PaymentTransactionEntryModel capturePaymentTransactionEntry, authorizationPaymentTransactionEntry;

  @Mock
  private PaymentTransactionModel paymentTransaction;

  @Mock
  private ConsignmentModel consignment;

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment1, marketplaceConsignment2;

  private List<AbstractOrderEntryModel> operatorOrderEntries;
  private List<AbstractOrderEntryModel> marketplaceOrderEntries;
  private Set<MarketplaceConsignmentModel> marketplaceConsignments;

  @Before
  public void setUp() throws Exception {
    operatorOrderEntries = asList(orderEntry1, orderEntry2);
    marketplaceOrderEntries = asList(orderEntry3);
    marketplaceConsignments = newHashSet(marketplaceConsignment1, marketplaceConsignment2);

    when(order.getOperatorEntries()).thenReturn(operatorOrderEntries);
    when(order.getMarketplaceEntries()).thenReturn(marketplaceOrderEntries);
    when(order.getMarketplaceConsignments()).thenReturn(marketplaceConsignments);
    when(order.getTotalPrice()).thenReturn(TOTAL_PRICE);
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getDigits()).thenReturn(DIGITS);
    when(order.getPaymentTransactions()).thenReturn(asList(paymentTransaction));
    when(marketplaceConsignment1.getTotalPrice()).thenReturn(PRICE_CONSIGNMENT1);
    when(marketplaceConsignment2.getTotalPrice()).thenReturn(PRICE_CONSIGNMENT2);
    when(paymentTransaction.getEntries()).thenReturn(asList(authorizationPaymentTransactionEntry));
    when(authorizationPaymentTransactionEntry.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
    when(commonI18NService.roundCurrency(anyDouble(), eq(DIGITS))).thenAnswer(new Answer<Double>() {

      @Override
      public Double answer(InvocationOnMock invocation) throws Throwable {
        return (Double) invocation.getArguments()[0];
      }
    });
  }


  @Test
  public void shouldCalcuateOperatorAmount() {
    double operatorAmount = miraklCalculationService.calculateOperatorAmount(order);

    assertThat(operatorAmount).isEqualTo(TOTAL_PRICE - PRICE_CONSIGNMENT1 - PRICE_CONSIGNMENT2);
  }

  @Test
  public void calcuateOperatorAmountShouldReturnZeroWhenFullMarketplace() {
    when(order.getOperatorEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    double operatorAmount = miraklCalculationService.calculateOperatorAmount(order);

    assertThat(operatorAmount).isEqualTo(0d);
  }

  @Test(expected = IllegalStateException.class)
  public void calcuateOperatorAmountShouldThrowExceptionWhenNoMarketplaceConsignments() {
    when(order.getMarketplaceEntries()).thenReturn(marketplaceOrderEntries);
    when(order.getMarketplaceConsignments()).thenReturn(Collections.<MarketplaceConsignmentModel>emptySet());

    miraklCalculationService.calculateOperatorAmount(order);
  }

  @Test
  public void shouldCalculateAlreadyCapturedAmount() {
    setUpCaptureTx();

    double alreadyCapturedAmount = miraklCalculationService.calculateAlreadyCapturedAmount(order);

    assertThat(alreadyCapturedAmount).isEqualTo(CAPTURED_AMOUNT.doubleValue());
  }

  @Test
  public void shouldCalculateAlreadyCapturedAmountReturnZeroWhenNoCapturedAmount() {
    double alreadyCapturedAmount = miraklCalculationService.calculateAlreadyCapturedAmount(order);

    assertThat(alreadyCapturedAmount).isEqualTo(0d);
  }

  @Test
  public void shouldCalculateAlreadyCapturedAmountReturnZeroWhenNotAccepted() {
    setUpCaptureTx();
    when(capturePaymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());

    double alreadyCapturedAmount = miraklCalculationService.calculateAlreadyCapturedAmount(order);

    assertThat(alreadyCapturedAmount).isEqualTo(0d);
  }

  @Test
  public void shouldCalculateAlreadyCapturedAmountReturnZeroWhenNoPaymentTransactions() {
    when(order.getPaymentTransactions()).thenReturn(Collections.<PaymentTransactionModel>emptyList());

    double alreadyCapturedAmount = miraklCalculationService.calculateAlreadyCapturedAmount(order);

    assertThat(alreadyCapturedAmount).isEqualTo(0d);
  }

  protected void setUpCaptureTx() {
    when(paymentTransaction.getEntries())
        .thenReturn(asList(capturePaymentTransactionEntry, authorizationPaymentTransactionEntry));
    when(capturePaymentTransactionEntry.getAmount()).thenReturn(CAPTURED_AMOUNT);
    when(capturePaymentTransactionEntry.getType()).thenReturn(PaymentTransactionType.PARTIAL_CAPTURE);
    when(capturePaymentTransactionEntry.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
  }

}
