package com.mirakl.hybris.b2bcore.returns.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklB2BRefundProcessingStrategyTest {

  @Mock
  private SkipPaymentStrategy skipPaymentStrategy;
  @Mock
  private RefundEntryModel refundEntry;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private PaymentService paymentService;
  @Mock
  private ModelService modelService;

  @InjectMocks
  private DefaultMiraklB2BRefundProcessingStrategy testObj;

  @Before
  public void setUp() throws Exception {
    when(refundEntry.getOrderEntry()).thenReturn(orderEntry);
    when(orderEntry.getOrder()).thenReturn(order);
    when(skipPaymentStrategy.shouldSkipPayment(order)).thenReturn(true);
  }

  @Test
  public void processRefund() throws Exception {
    boolean output = testObj.processRefund(refundEntry);

    verify(refundEntry).setStatus(ReturnStatus.COMPLETED);
    verify(modelService).save(refundEntry);
    verifyZeroInteractions(paymentService);
    assertThat(output).isTrue();
  }

}
