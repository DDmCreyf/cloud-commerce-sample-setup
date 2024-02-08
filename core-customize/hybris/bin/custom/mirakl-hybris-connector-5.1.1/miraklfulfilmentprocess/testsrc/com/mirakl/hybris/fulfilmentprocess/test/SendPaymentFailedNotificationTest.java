/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * hybris.
 *
 *
 */
package com.mirakl.hybris.fulfilmentprocess.test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.fulfilmentprocess.actions.order.SendPaymentFailedNotificationAction;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.events.PaymentFailedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendPaymentFailedNotificationTest {
  @InjectMocks
  private final SendPaymentFailedNotificationAction sendPaymentFailedNotification = new SendPaymentFailedNotificationAction();

  @Mock
  private EventService eventService;
  @Captor
  private ArgumentCaptor<PaymentFailedEvent> event;

  /**
   * Test method for
   * {@link de.hybris.platform.yacceleratorfulfilmentprocess.actions.order.SendPaymentFailedNotificationAction#executeAction(OrderProcessModel)}
   * .
   */
  @Test
  public void testExecuteActionOrderProcessModel() {
    final OrderProcessModel process = new OrderProcessModel();
    sendPaymentFailedNotification.executeAction(process);

    verify(eventService).publishEvent(event.capture());
    assertThat(event.getValue().getProcess()).isEqualTo(process);


  }
}
