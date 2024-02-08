package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static com.google.common.collect.Sets.newHashSet;
import static com.mirakl.hybris.fulfilmentprocess.constants.MiraklfulfilmentprocessConstants.MARKETPLACE_CONSIGNMENT_SUBPROCESS_NAME;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StartMarketplaceConsignmentProcessesActionTest {

  @InjectMocks
  private StartMarketplaceConsignmentProcessesAction action;

  @Mock
  private ModelService modelService;
  @Mock
  private BusinessProcessService businessProcessService;
  @Mock
  private OrderProcessModel orderProcess;
  @Mock
  private OrderModel order;
  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment1, marketplaceConsignment2;

  private Set<MarketplaceConsignmentModel> consignments;


  @Before
  public void setUp() throws Exception {
    consignments = newHashSet(marketplaceConsignment1, marketplaceConsignment2);
    when(orderProcess.getOrder()).thenReturn(order);
    when(order.getMarketplaceConsignments()).thenReturn(consignments);
    when(businessProcessService.createProcess(anyString(), eq(MARKETPLACE_CONSIGNMENT_SUBPROCESS_NAME)))
        .thenReturn(mock(MarketplaceConsignmentProcessModel.class));
  }

  @Test
  public void shouldStartMarketplaceProcesses() throws RetryLaterException, Exception {
    action.executeAction(orderProcess);

    verify(businessProcessService, times(consignments.size())).createProcess(anyString(),
        eq(MARKETPLACE_CONSIGNMENT_SUBPROCESS_NAME));
  }

  @Test
  public void shouldNotStartProcessesIfNoMarketplaceConsignments() throws RetryLaterException, Exception {
    when(order.getMarketplaceConsignments()).thenReturn(Collections.<MarketplaceConsignmentModel>emptySet());

    action.executeAction(orderProcess);

    verifyZeroInteractions(businessProcessService);
  }
}
