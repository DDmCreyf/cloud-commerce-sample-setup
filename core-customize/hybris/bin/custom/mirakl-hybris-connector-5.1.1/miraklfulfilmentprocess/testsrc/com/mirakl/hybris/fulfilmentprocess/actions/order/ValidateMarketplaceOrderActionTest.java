package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static java.util.Collections.singleton;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.task.RetryLaterException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ValidateMarketplaceOrderActionTest {

  @InjectMocks
  private ValidateMarketplaceOrderAction action;

  @Mock
  private MiraklOrderService miraklOrderService;

  @Mock
  private OrderProcessModel orderProcess;

  @Mock
  private OrderModel order;

  @Before
  public void setUp() throws Exception {
    when(orderProcess.getOrder()).thenReturn(order);
  }

  @Test
  public void shouldValidateOrder() throws RetryLaterException, Exception {
    when(order.getMarketplaceConsignments()).thenReturn(singleton(mock(MarketplaceConsignmentModel.class)));

    action.executeAction(orderProcess);

    verify(miraklOrderService).validateOrder(order);
  }

  @Test
  public void shouldNotCallOrderConfirmationWhenFullOperator() throws RetryLaterException, Exception {
    when(order.getMarketplaceConsignments()).thenReturn(Collections.<MarketplaceConsignmentModel>emptySet());

    action.executeAction(orderProcess);

    verifyZeroInteractions(miraklOrderService);
  }
}
