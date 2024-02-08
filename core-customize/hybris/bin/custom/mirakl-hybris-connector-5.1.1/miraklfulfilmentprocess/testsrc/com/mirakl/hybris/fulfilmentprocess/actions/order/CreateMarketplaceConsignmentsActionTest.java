package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CreateMarketplaceConsignmentsActionTest {

  @InjectMocks
  private CreateMarketplaceConsignmentsAction action;

  @Mock
  private MiraklOrderService miraklOrderService;

  @Mock
  private MarketplaceConsignmentService miraklConsignmentService;

  @Mock
  private OrderModel orderModel;

  @Mock
  private OrderProcessModel orderProcessModel;

  @Mock
  private MiraklCreatedOrders createdOrders;


  @Before
  public void setUp() throws Exception {
    when(miraklOrderService.loadCreatedOrders(orderModel)).thenReturn(createdOrders);
    when(orderProcessModel.getOrder()).thenReturn(orderModel);

  }

  @Test
  public void shouldCreateConsignmentsForOrderWithMarketplaceEntries() {
    action.executeAction(orderProcessModel);

    verify(miraklConsignmentService).createMarketplaceConsignments(orderModel, createdOrders);
  }

  @Test
  public void shouldNotCreateConsignmentsForOrderWithNoMarketplaceEntries() {
    when(miraklOrderService.loadCreatedOrders(orderModel)).thenReturn(null);

    action.executeAction(orderProcessModel);

    verify(miraklConsignmentService, never()).createMarketplaceConsignments(orderModel, createdOrders);
  }


}
