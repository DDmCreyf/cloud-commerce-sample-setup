package com.mirakl.hybris.core.payment.strategies.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.core.payment.services.MiraklRefundService;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRefundRequestEventHandlingStrategyTest {

  @InjectMocks
  private DefaultRefundRequestEventHandlingStrategy eventHandler;

  @Mock
  private RefundRequestReceivedEvent event;
  @Mock
  private MiraklRefundRequestData refundRequestData;
  @Mock
  private MiraklRefundService miraklRefundService;

  @Before
  public void setUp() {
    when(event.getRefundRequest()).thenReturn(refundRequestData);
  }

  @Test
  public void shouldUseRefundService() {
    eventHandler.handleEvent(event);

    verify(miraklRefundService).saveReceivedRefundRequest(refundRequestData);
  }

}
