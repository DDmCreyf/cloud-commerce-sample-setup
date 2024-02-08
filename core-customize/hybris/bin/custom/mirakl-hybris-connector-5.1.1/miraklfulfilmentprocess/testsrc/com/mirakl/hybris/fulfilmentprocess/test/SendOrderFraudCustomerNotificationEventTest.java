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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.fulfilmentprocess.actions.order.NotifyCustomerAboutFraudAction;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.events.OrderFraudCustomerNotificationEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import junit.framework.Assert;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendOrderFraudCustomerNotificationEventTest {
  @InjectMocks
  private final NotifyCustomerAboutFraudAction action = new NotifyCustomerAboutFraudAction();

  @Mock
  private EventService eventService;
  @Mock
  private ModelService modelService;
  @Captor
  private ArgumentCaptor<OrderFraudCustomerNotificationEvent> event;

  @Test
  public void testExecuteAction() {
    final OrderProcessModel process = new OrderProcessModel();
    final OrderModel order = new OrderModel();
    process.setOrder(order);
    action.executeAction(process);

    verify(eventService).publishEvent(event.capture());
    assertThat(event.getValue().getProcess()).isEqualTo(process);

    Mockito.verify(modelService).save(order);
    Assert.assertEquals(OrderStatus.SUSPENDED, order.getStatus());
  }
}
