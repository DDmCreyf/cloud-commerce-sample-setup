package com.mirakl.hybris.fulfilmentprocess.actions.consignment;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.client.mmp.domain.order.state.MiraklOrderStatus;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.fulfilment.strategies.ProcessMarketplacePaymentStrategy;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MarketplaceConsignmentProcessModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.fulfilmentprocess.actions.consignment.ProcessLogisticOrderUpdateAction.Transition;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProcessLogisticOrderUpdateActionTest {

  @InjectMocks
  private ProcessLogisticOrderUpdateAction action;

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private ModelService modelService;
  @Mock
  private Populator<MiraklOrder, MarketplaceConsignmentModel> updateConsignmentPopulator;
  @Mock
  private MarketplaceConsignmentProcessModel marketplaceConsignmentProcess;
  @Mock
  private OrderModel order;
  @Mock
  private MiraklOrder miraklOrder;
  @Mock
  private MiraklOrderStatus miraklOrderStatus;
  @Mock
  private MiraklOrderPayment miraklOrderPayment;
  @Mock
  private ProcessMarketplacePaymentStrategy processMarketplacePaymentStrategy;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;

  private MarketplaceConsignmentModel marketplaceConsignment;

  @Before
  public void setUp() throws Exception {
    marketplaceConsignment = new MarketplaceConsignmentModel();
    marketplaceConsignment.setOrder(order);
    when(marketplaceConsignmentProcess.getConsignment()).thenReturn(marketplaceConsignment);
    when(miraklOrder.getStatus()).thenReturn(miraklOrderStatus);
    when(marketplaceConsignmentService.loadConsignmentUpdate(marketplaceConsignment)).thenReturn(miraklOrder);
    when(marketplaceConsignmentService.loadDebitRequest(marketplaceConsignment)).thenReturn(miraklOrderPayment);
    when(configurationService.getConfiguration()).thenReturn(configuration);
  }

  @Test
  public void shouldWaitIfNoUpdate() throws RetryLaterException, Exception {
    marketplaceConsignment.setConsignmentUpdatePayload(null);

    String transition = action.execute(marketplaceConsignmentProcess);

    assertThat(transition).isEqualTo(Transition.WAIT.name());
  }

  @Test
  public void shouldWaitIfNotFinalUpdate() throws RetryLaterException, Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.SHIPPING);

    String transition = action.execute(marketplaceConsignmentProcess);

    assertThat(transition).isEqualTo(Transition.WAIT.name());
  }

  @Test
  public void shouldReturnClosedIfConsignmentClosed() throws RetryLaterException, Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.CLOSED);

    String transition = action.execute(marketplaceConsignmentProcess);

    assertThat(transition).isEqualTo(Transition.CLOSE.name());
  }

  @Test
  public void shouldReturnCancelledIfConsignmentCancelled() throws RetryLaterException, Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.CANCELED);

    String transition = action.execute(marketplaceConsignmentProcess);

    assertThat(transition).isEqualTo(Transition.CANCEL.name());
  }

  @Test
  public void shouldReturnRefusedIfConsignmentRefused() throws RetryLaterException, Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.REFUSED);

    String transition = action.execute(marketplaceConsignmentProcess);

    assertThat(transition).isEqualTo(Transition.REFUSE.name());
  }

  @Test
  public void shouldExecuteUpdate() throws RetryLaterException, Exception {
    marketplaceConsignment.setLastUpdateProcessed(false);

    action.execute(marketplaceConsignmentProcess);

    verify(updateConsignmentPopulator).populate(eq(miraklOrder), eq(marketplaceConsignment));
    verify(modelService).save(marketplaceConsignment);
  }

  @Test
  public void shouldCallPaymentStrategyWithoutPullDebitRequest() throws Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.WAITING_DEBIT_PAYMENT);

    action.execute(marketplaceConsignmentProcess);

    verify(processMarketplacePaymentStrategy).processPayment(marketplaceConsignment, miraklOrderPayment);
  }

  @Test
  public void shouldNotCallPaymentStrategyWithPullDebitRequest() throws Exception {
    when(miraklOrderStatus.getState()).thenReturn(State.WAITING_DEBIT_PAYMENT);
    when(configuration.getBoolean(MiraklservicesConstants.ENABLE_PAYMENT_REQUEST_PULLING, false)).thenReturn(true);

    action.execute(marketplaceConsignmentProcess);

    verify(processMarketplacePaymentStrategy, never()).processPayment(marketplaceConsignment, miraklOrderPayment);
  }

}
