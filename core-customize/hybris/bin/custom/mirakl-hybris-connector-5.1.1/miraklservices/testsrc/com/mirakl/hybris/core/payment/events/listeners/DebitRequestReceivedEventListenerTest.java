package com.mirakl.hybris.core.payment.events.listeners;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.strategies.DebitRequestEventHandlingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DebitRequestReceivedEventListenerTest {

  @InjectMocks
  private DebitRequestReceivedEventListener listener;

  @Mock
  private DebitRequestEventHandlingStrategy debitRequestEventHandlingStrategy;
  @Mock
  private DebitRequestReceivedEvent event;

  @Test
  public void shouldHandleCaptureEvent() {
    listener.onEvent(event);

    verify(debitRequestEventHandlingStrategy).handleEvent(event);
  }

}
