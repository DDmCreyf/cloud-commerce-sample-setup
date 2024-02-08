package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CancelMarketplaceConsignmentActionTest {

  private static final String ORDER_PROCESS_CODE = "order-process-code";

  @InjectMocks
  private CancelMarketplaceConsignmentAction action;

  @Mock
  private MarketplaceConsignmentProcessModel consignmentProcess;
  @Mock
  private OrderProcessModel orderProcess;
  @Mock
  private ModelService modelService;
  @Mock
  private BusinessProcessService businessProcessService;
  @Mock
  private MarketplaceConsignmentModel consignment;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private AbstractOrderEntryModel orderEntry;

  @Before
  public void setUp() throws Exception {
    when(consignmentProcess.getConsignment()).thenReturn(consignment);
    when(consignmentProcess.getParentProcess()).thenReturn(orderProcess);
    when(orderProcess.getCode()).thenReturn(ORDER_PROCESS_CODE);
    when(consignment.getConsignmentEntries()).thenReturn(newHashSet(consignmentEntry));
    when(consignmentEntry.getOrderEntry()).thenReturn(orderEntry);
  }

  @Test
  public void shouldMarkConsignmentAsCancelledAndTerminateProcess() {
    action.executeAction(consignmentProcess);

    verify(consignment).setStatus(ConsignmentStatus.CANCELLED);
    verify(modelService).save(consignment);
    verify(businessProcessService).triggerEvent(anyString());
    verify(consignmentProcess).setDone(true);
    verify(orderEntry).setQuantityStatus(OrderEntryStatus.DEAD);
  }

}
