package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.task.RetryLaterException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelMarketplaceOrderActionTest {

  @InjectMocks
  private CancelMarketplaceOrderAction action;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private OrderProcessModel orderProcess;
  @Mock
  private OrderModel order;
  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment1, marketplaceConsignment2;

  HashSet<MarketplaceConsignmentModel> marketplaceConsignments;

  @Before
  public void setUp() throws Exception {
    marketplaceConsignments = newHashSet(marketplaceConsignment1, marketplaceConsignment2);
    when(orderProcess.getOrder()).thenReturn(order);
    when(order.getMarketplaceConsignments()).thenReturn(marketplaceConsignments);
  }

  @Test
  public void shouldCancelMarketplaceOrder() throws RetryLaterException, Exception {
    action.executeAction(orderProcess);

    for (MarketplaceConsignmentModel marketplaceConsignment : marketplaceConsignments) {
      verify(marketplaceConsignmentService).cancelMarketplaceConsignment(marketplaceConsignment);
    }
  }

}
