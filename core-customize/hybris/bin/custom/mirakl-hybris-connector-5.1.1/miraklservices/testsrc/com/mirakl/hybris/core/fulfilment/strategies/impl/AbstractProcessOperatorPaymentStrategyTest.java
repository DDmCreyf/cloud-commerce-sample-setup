package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Ignore;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mirakl.hybris.core.order.services.MiraklCalculationService;
import com.mirakl.hybris.core.order.services.TakePaymentService;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@Ignore
public abstract class AbstractProcessOperatorPaymentStrategyTest {

  protected static final double OPERATOR_AMOUNT = 105.5;
  protected static final int DIGITS = 2;

  @Mock
  protected TakePaymentService takePaymentService;
  @Mock
  protected MiraklCalculationService miraklCalculationService;
  @Mock
  protected ModelService modelService;
  @Mock
  protected CommonI18NService commonI18NService;
  @Mock
  protected OrderProcessModel orderProcess;
  @Mock
  protected OrderModel order;
  @Mock
  protected CurrencyModel currency;
  @Mock
  protected AbstractOrderEntryModel operatorOrderEntry, marketplaceOrderEntry;
  @Mock
  protected PaymentTransactionModel paymentTransaction;
  @Mock
  protected CreditCardPaymentInfoModel creditCardPaymentInfo;
  @Mock
  protected PaymentTransactionEntryModel paymentTransactionEntry;
  @Captor
  protected ArgumentCaptor<Double> operatorAmountArgumentCaptor;

  protected List<AbstractOrderEntryModel> operatorOrderEntries;
  protected List<AbstractOrderEntryModel> marketplaceOrderEntries;
  protected List<PaymentTransactionModel> paymentTransactions;

  protected void setUp() throws Exception {
    operatorOrderEntries = singletonList(operatorOrderEntry);
    marketplaceOrderEntries = singletonList(marketplaceOrderEntry);
    paymentTransactions = singletonList(paymentTransaction);
  }
}
