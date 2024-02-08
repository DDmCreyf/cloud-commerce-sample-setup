package com.mirakl.hybris.core.catalog.events.listeners;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.catalog.events.ExportableCategoryEvent;
import com.mirakl.hybris.core.catalog.strategies.LookupExportableAttributesStrategy;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LookupExportableAttributeEventListenerTest {

  @InjectMocks
  private LookupExportableAttributeEventListener listener;

  @Mock
  private LookupExportableAttributesStrategy strategy;
  @Mock
  private ExportableCategoryEvent event;

  @Test
  public void onEvent() {
    listener.onEvent(event);

    verify(strategy).handleEvent(event);
  }
}
