package com.mirakl.hybris.core.fulfilment.events.listeners;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.fulfilment.strategies.NotShippableOffersHandlingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class NotShippableOffersEventListenerTest {

  @InjectMocks
  private NotShippableOffersEventListener eventListener;

  @Mock
  private NotShippableOffersEvent event;

  @Mock
  private NotShippableOffersHandlingStrategy notShippableOffersHandlingStrategy;

  @Test
  public void shouldHandleNotShippableOffers() {
    eventListener.onEvent(event);

    verify(notShippableOffersHandlingStrategy).handleEvent(event);
  }


}
