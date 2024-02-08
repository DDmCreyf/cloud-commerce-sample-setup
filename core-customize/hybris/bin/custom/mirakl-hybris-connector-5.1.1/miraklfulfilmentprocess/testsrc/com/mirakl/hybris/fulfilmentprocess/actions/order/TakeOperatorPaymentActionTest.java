package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.fulfilment.strategies.ProcessOperatorPaymentStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class TakeOperatorPaymentActionTest {

  @Mock
  private ProcessOperatorPaymentStrategy processOperatorPaymentStrategy;
  @Mock
  private OrderProcessModel process;
  @Mock
  private OrderModel order;
  @InjectMocks
  private TakeOperatorPaymentAction testObj;

  @Before
  public void setup() {
    when(process.getOrder()).thenReturn(order);
  }

  @Test
  public void testExecuteAction() {
    when(processOperatorPaymentStrategy.processPayment(order)).thenReturn(true);

    Transition output = testObj.executeAction(process);

    assertThat(output).isEqualTo(Transition.OK);
  }

  @Test
  public void testExecuteActionWhenPaymentFails() {
    when(processOperatorPaymentStrategy.processPayment(order)).thenReturn(false);

    Transition output = testObj.executeAction(process);

    assertThat(output).isEqualTo(Transition.NOK);
  }

}
