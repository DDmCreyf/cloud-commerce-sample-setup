package com.mirakl.hybris.b2bcore.payment.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mirakl.client.mmp.domain.payment.MiraklPaymentStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderLineRefund;
import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.payment.populators.AbstractMiraklOrderLineRefundPopulatorTest;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklB2BOrderLineRefundPopulatorTest extends AbstractMiraklOrderLineRefundPopulatorTest {

  @Mock
  private SkipPaymentStrategy skipPaymentStrategy;
  @Mock
  private OrderModel order;
  @Mock
  private OrderEntryModel orderEntry;

  @InjectMocks
  private MiraklB2BOrderLineRefundPopulator testObj;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(orderEntry.getOrder()).thenReturn(order);
    when(refundEntry.getOrderEntry()).thenReturn(orderEntry);
    when(skipPaymentStrategy.shouldSkipPayment(order)).thenReturn(true);
  }

  @Test
  public void populate() throws Exception {
    MiraklOrderLineRefund output = new MiraklOrderLineRefund();
    testObj.populate(refundEntry, output);

    assertThat(output.getPaymentStatus()).isEqualTo(MiraklPaymentStatus.OK);
    assertThat(output.getRefundId()).isEqualTo(MIRAKL_REFUND_ID);
    assertThat(output.getAmount()).isEqualTo(REFUND_ENTRY_AMOUNT);
    assertThat(output.getCurrencyIsoCode().name()).isEqualTo(CURRENCY_ISO_CODE);
  }

}
