package com.mirakl.hybris.b2bcore.payment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSkipPaymentStrategyTest {

  @Mock
  private AbstractOrderModel order;
  @Mock
  private InvoicePaymentInfoModel invoicePaymentInfo;
  @Mock
  private CreditCardPaymentInfoModel creditCardPaymentInfo;

  @InjectMocks
  private DefaultSkipPaymentStrategy testObj;

  @Test
  public void shouldSkipPayment() throws Exception {
    when(order.getPaymentInfo()).thenReturn(invoicePaymentInfo);

    boolean output = testObj.shouldSkipPayment(order);

    assertThat(output).isTrue();
  }

  @Test
  public void shouldNotSkipPayment() throws Exception {
    when(order.getPaymentInfo()).thenReturn(creditCardPaymentInfo);

    boolean output = testObj.shouldSkipPayment(order);

    assertThat(output).isFalse();
  }

}
