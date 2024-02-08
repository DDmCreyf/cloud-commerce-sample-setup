/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
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

import com.mirakl.hybris.fulfilmentprocess.actions.order.SendAuthorizationFailedNotificationAction;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.orderprocessing.events.AuthorizationFailedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendAuthorizationFailedNotificationTest
{
  @InjectMocks
  private final SendAuthorizationFailedNotificationAction sendAuthorizationFailedNotification = new SendAuthorizationFailedNotificationAction();

  @Mock
  private EventService eventService;
  @Captor
  private ArgumentCaptor<AuthorizationFailedEvent> event;

  /**
   * Test method for
   * {@link de.hybris.platform.yacceleratorfulfilmentprocess.actions.order.SendOrderPlacedNotificationAction#executeAction(OrderProcessModel)}
   * .
   */
  @Test
  public void testExecuteActionOrderProcessModel()
  {
    final OrderProcessModel process = new OrderProcessModel();
    sendAuthorizationFailedNotification.executeAction(process);

    verify(eventService).publishEvent(event.capture());
    assertThat(event.getValue().getProcess()).isEqualTo(process);
  }
}
