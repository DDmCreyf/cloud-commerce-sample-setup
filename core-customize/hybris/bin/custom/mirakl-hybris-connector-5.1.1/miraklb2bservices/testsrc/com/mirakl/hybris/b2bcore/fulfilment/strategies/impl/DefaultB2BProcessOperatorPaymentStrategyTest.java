package com.mirakl.hybris.b2bcore.fulfilment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.fulfilment.strategies.impl.AbstractProcessOperatorPaymentStrategyTest;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.task.RetryLaterException;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BProcessOperatorPaymentStrategyTest extends AbstractProcessOperatorPaymentStrategyTest {

  @Mock
  private SkipPaymentStrategy skipPaymentStrategy;

  @InjectMocks
  private DefaultB2BProcessOperatorPaymentStrategy testObj;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(skipPaymentStrategy.shouldSkipPayment(order)).thenReturn(true);
  }

  @Test
  public void processPaymentWhenSkipped() throws RetryLaterException, Exception {
    boolean output = testObj.processPayment(order);

    assertThat(output).isTrue();
    verify(order, never()).getOperatorEntries();
    verifyZeroInteractions(takePaymentService);
  }

}
