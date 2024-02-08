package com.mirakl.hybris.core.payment.events.listeners;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.core.payment.strategies.RefundRequestEventHandlingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RefundRequestReceivedEventListenerTest {


  @InjectMocks
  private RefundRequestReceivedEventListener listener;

  @Mock
  private RefundRequestEventHandlingStrategy refundRequestEventHandlingStrategy;
  @Mock
  private RefundRequestReceivedEvent event;


  @Test
  public void shouldHandleRefundEvent() {
    listener.onEvent(event);

    verify(refundRequestEventHandlingStrategy).handleEvent(event);
  }


}
