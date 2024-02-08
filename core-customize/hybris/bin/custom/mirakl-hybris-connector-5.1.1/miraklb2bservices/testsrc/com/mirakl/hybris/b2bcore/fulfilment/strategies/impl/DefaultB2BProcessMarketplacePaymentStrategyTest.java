package com.mirakl.hybris.b2bcore.fulfilment.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus;
import com.mirakl.hybris.core.fulfilment.strategies.impl.AbstractProcessMarketplacePaymentStrategyTest;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BProcessMarketplacePaymentStrategyTest extends AbstractProcessMarketplacePaymentStrategyTest {

  @Mock
  private SkipPaymentStrategy skipPaymentStrategy;

  @InjectMocks
  private DefaultB2BProcessMarketplacePaymentStrategy testObj;

  @Before
  public void setUp() {
    super.setUp();
    when(skipPaymentStrategy.shouldSkipPayment(order)).thenReturn(true);
  }

  @Test
  public void processPayment() {
    marketplaceConsignment.setPaymentStatus(MarketplaceConsignmentPaymentStatus.INITIAL);

    boolean output = testObj.processPayment(marketplaceConsignment, miraklOrderPayment);

    assertThat(marketplaceConsignment.getPaymentStatus()).isEqualTo(MarketplaceConsignmentPaymentStatus.SUCCESS);
    verifyZeroInteractions(takePaymentService);
    assertThat(marketplaceConsignment.getPaymentTransactionEntry()).isNull();
    assertThat(output).isTrue();
  }

}
