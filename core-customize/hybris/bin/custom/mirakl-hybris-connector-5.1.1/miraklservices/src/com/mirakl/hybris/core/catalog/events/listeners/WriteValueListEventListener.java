package com.mirakl.hybris.core.catalog.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.strategies.WriteValueListStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class WriteValueListEventListener extends AbstractEventListener<ExportableAttributeEvent> {

  protected WriteValueListStrategy writeValueListStrategy;

  @Override
  protected void onEvent(ExportableAttributeEvent event) {
    writeValueListStrategy.handleEvent(event);
    }

  @Required
  public void setWriteValueListStrategy(WriteValueListStrategy writeValueListStrategy) {
    this.writeValueListStrategy = writeValueListStrategy;
  }
}
