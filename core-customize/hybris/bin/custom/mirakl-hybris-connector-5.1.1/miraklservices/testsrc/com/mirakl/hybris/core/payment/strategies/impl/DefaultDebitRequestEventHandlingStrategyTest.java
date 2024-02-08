package com.mirakl.hybris.core.payment.strategies.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.services.MiraklDebitService;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDebitRequestEventHandlingStrategyTest {

  @InjectMocks
  private DefaultDebitRequestEventHandlingStrategy eventHandler;

  @Mock
  private DebitRequestReceivedEvent event;
  @Mock
  private MiraklOrderPayment miraklOrderPayment;
  @Mock
  private MiraklDebitService miraklDebitService;

  @Before
  public void setUp() {
    when(event.getDebitRequest()).thenReturn(miraklOrderPayment);
  }

  @Test
  public void shouldUseDebitService() {
    eventHandler.handleEvent(event);

    verify(miraklDebitService).saveReceivedDebitRequest(miraklOrderPayment);
  }

}
